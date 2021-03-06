package com.syj.service.impl;

import java.util.Date;
import java.util.List;

import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.syj.mapper.CommentsMapper;
import com.syj.mapper.CommentsMapperCustom;
import com.syj.mapper.SearchRecordsMapper;
import com.syj.mapper.UsersLikeVideosMapper;
import com.syj.mapper.UsersMapper;
import com.syj.mapper.VideosMapper;
import com.syj.mapper.VideosMapperCustom;
import com.syj.pojo.Comments;
import com.syj.pojo.SearchRecords;
import com.syj.pojo.UsersLikeVideos;
import com.syj.pojo.Videos;
import com.syj.pojo.vo.CommentsVO;
import com.syj.pojo.vo.VideosVO;
import com.syj.service.VideoService;
import com.syj.utils.PagedResult;
import com.syj.utils.TimeAgoUtils;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class VideoServiceImpl implements VideoService {
	@Autowired
	private UsersMapper usersMapper;

	@Autowired
	private VideosMapper videoMapper;

	@Autowired
	private VideosMapperCustom videosMapperCustom;

	@Autowired
	private SearchRecordsMapper searchRecordsMapper;

	@Autowired
	private UsersLikeVideosMapper usersLikeVideosMapper;
	
	@Autowired
	private CommentsMapper commentsMapper;
	
	@Autowired
	private CommentsMapperCustom commentsMapperCustom;

	@Autowired
	private Sid sid;

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public String saveVideo(Videos video) {
		// 在controller是没有设置id，需要使用sid给他设置id
		video.setId(sid.nextShort());
		videoMapper.insertSelective(video);
		// 当保存成功后返回视频id，使上传的封面可以保存到对应的视频
		return video.getId();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void updateVideo(String videoId, String coverPath) {
		Videos video = new Videos();
		video.setId(videoId);
		video.setCoverPath(coverPath);
		videoMapper.updateByPrimaryKeySelective(video);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public PagedResult getAllVideos(Videos video, Integer isSaveRecord, Integer page, Integer pageSize) {

		// 保存热搜词
		String videoDesc = video.getVideoDesc();
		String userId = video.getUserId();
		if (isSaveRecord != null && isSaveRecord == 1) {
			SearchRecords searchRecords = new SearchRecords();
			String id = sid.nextShort();
			searchRecords.setId(id);
			searchRecords.setContent(videoDesc);
			searchRecordsMapper.insert(searchRecords);
		}

		// 分页查询，添加desc查询
		PageHelper pageHelper = new PageHelper();
		pageHelper.startPage(page, pageSize);
		List<VideosVO> list = videosMapperCustom.queryAllVideos(videoDesc, userId);

		PageInfo<VideosVO> pageList = new PageInfo<>(list);
		PagedResult pagedResult = new PagedResult();

		pagedResult.setPage(page);
		pagedResult.setTotal(pageList.getPages());
		pagedResult.setRows(list);
		pagedResult.setRecords(pageList.getTotal());

		return pagedResult;
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public PagedResult queryMyLikeVideos(String userId, Integer page, Integer pageSize) {
		PageHelper.startPage(page, pageSize);
		List<VideosVO> list = videosMapperCustom.queryMyLikeVideos(userId);

		PageInfo<VideosVO> pageList = new PageInfo<>(list);

		PagedResult pagedResult = new PagedResult();
		pagedResult.setTotal(pageList.getPages());
		pagedResult.setRows(list);
		pagedResult.setPage(page);
		pagedResult.setRecords(pageList.getTotal());

		return pagedResult;
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public PagedResult queryMyFollowVideos(String userId, Integer page, Integer pageSize) {
		PageHelper.startPage(page, pageSize);
		List<VideosVO> list = videosMapperCustom.queryMyFollowVideos(userId);

		PageInfo<VideosVO> pageList = new PageInfo<>(list);

		PagedResult pagedResult = new PagedResult();
		pagedResult.setTotal(pageList.getPages());
		pagedResult.setRows(list);
		pagedResult.setPage(page);
		pagedResult.setRecords(pageList.getTotal());

		return pagedResult;
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public List<String> getHotwords() {
		return searchRecordsMapper.getHotwords();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void userLikeVideo(String userId, String videoId, String videoCreaterId) {
		// 1、保存用户和视频的喜欢点赞关联关系表
		String likeId = sid.nextShort();
		UsersLikeVideos ulv = new UsersLikeVideos();
		ulv.setId(likeId);
		ulv.setUserId(userId);
		ulv.setVideoId(videoId);
		usersLikeVideosMapper.insert(ulv);

		// 2、视频喜欢数量累加
		videosMapperCustom.addVideoLikeCount(videoId);

		// 3、用户受喜欢的数量累加
		usersMapper.addReceiveLikeCount(userId);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void userUnLikeVideo(String userId, String videoId, String videoCreaterId) {
		// 1. 删除用户和视频的喜欢点赞关联关系表

		Example example = new Example(UsersLikeVideos.class);
		Criteria criteria = example.createCriteria();

		criteria.andEqualTo("userId", userId);
		criteria.andEqualTo("videoId", videoId);

		usersLikeVideosMapper.deleteByExample(example);

		// 2. 视频喜欢数量累减
		videosMapperCustom.reduceVideoLikeCount(videoId);

		// 3. 用户受喜欢数量的累减
		usersMapper.reduceReceiveLikeCount(videoCreaterId);

	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void saveComment(Comments comment) {
		String sid = Sid.next();
		comment.setId(sid);
		comment.setCreateTime(new Date());
		commentsMapper.insert(comment);
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public PagedResult getAllComments(String videoId, Integer page, Integer pageSize) {

		PageHelper.startPage(page, pageSize);
		List<CommentsVO> list = commentsMapperCustom.queryComments(videoId);

		for (CommentsVO c : list) {
			String timeAgo = TimeAgoUtils.format(c.getCreateTime());
			c.setTimeAgoStr(timeAgo);
		}

		PageInfo<CommentsVO> pageList = new PageInfo<>(list);

		PagedResult grid = new PagedResult();
		grid.setTotal(pageList.getPages());
		grid.setRows(list);
		grid.setPage(page);
		grid.setRecords(pageList.getTotal());

		return grid;
	}
}
