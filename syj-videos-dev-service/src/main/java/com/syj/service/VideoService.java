package com.syj.service;

import java.util.List;

import com.syj.pojo.Comments;
import com.syj.pojo.Videos;
import com.syj.utils.PagedResult;

public interface VideoService {

	/**
	 * @Description:保存视频
	 */
	String saveVideo(Videos video);

	/**
	 * @Description:更新视频封面到数据库
	 */
	void updateVideo(String videoId, String coverPath);

	/**
	 * @Description:分页查询视频列表
	 */
	PagedResult getAllVideos(Videos video, Integer isSaveRecord, Integer page, Integer pageSize);

	/**
	 * @Description: 查询我喜欢的视频列表
	 */
	public PagedResult queryMyLikeVideos(String userId, Integer page, Integer pageSize);
	
	/**
	 * @Description: 查询我关注的人的视频列表
	 */
	public PagedResult queryMyFollowVideos(String userId, Integer page, Integer pageSize);
	
	/**
	 * @Description:获取热搜词列表
	 */
	List<String> getHotwords();

	/**
	 * @Description:用户喜欢/点赞视频
	 */
	void userLikeVideo(String userId, String videoId, String videoCreaterId);

	/**
	 * @Description:用户不喜欢/取消点赞视频
	 */
	void userUnLikeVideo(String userId, String videoId, String videoCreaterId);

	/**
	 * @Description:保存用户留言
	 */
	void saveComment(Comments comment);
}
