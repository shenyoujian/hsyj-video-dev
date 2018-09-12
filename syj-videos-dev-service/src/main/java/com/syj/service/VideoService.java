package com.syj.service;

import com.syj.pojo.Videos;

public interface VideoService {

	/**
	 * @Description:保存视频
	 */
	String saveVideo(Videos video);
	
	/**
	 * @Description:更新视频封面到数据库
	 */
	void updateVideo(String videoId, String coverPath);

}
