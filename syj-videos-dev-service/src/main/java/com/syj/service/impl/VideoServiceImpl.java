package com.syj.service.impl;

import java.util.List;

import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.syj.mapper.VideosMapper;
import com.syj.mapper.VideosMapperCustom;
import com.syj.pojo.Videos;
import com.syj.pojo.vo.VideosVO;
import com.syj.service.VideoService;
import com.syj.utils.PagedResult;

@Service
public class VideoServiceImpl implements VideoService {

	@Autowired
	private VideosMapper videoMapper;
	
	@Autowired
	private VideosMapperCustom videosMapperCustom;


	@Autowired
	private Sid sid;

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public String saveVideo(Videos video) {
		// 在controller是没有设置id，需要使用sid给他设置id
		video.setId(sid.nextShort());
		videoMapper.insertSelective(video);
		//当保存成功后返回视频id，使上传的封面可以保存到对应的视频
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

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public PagedResult getAllVideos(Integer page, Integer pageSize) {
		PageHelper pageHelper = new PageHelper();
		pageHelper.startPage(page,pageSize);
		List<VideosVO> list = videosMapperCustom.queryAllVideos();
		
		PageInfo <VideosVO> pageList = new PageInfo<>(list);
		PagedResult pagedResult = new PagedResult();
		
		pagedResult.setPage(page);
		pagedResult.setTotal(pageList.getPages());
		pagedResult.setRows(list);
		pagedResult.setRecords(pageList.getTotal());
		
		return pagedResult;
	}

}
