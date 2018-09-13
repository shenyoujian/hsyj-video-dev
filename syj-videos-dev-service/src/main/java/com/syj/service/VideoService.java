package com.syj.service;

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

}
