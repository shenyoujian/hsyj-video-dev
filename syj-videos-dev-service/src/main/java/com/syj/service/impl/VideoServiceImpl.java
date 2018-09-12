package com.syj.service.impl;

import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.syj.mapper.VideosMapper;
import com.syj.pojo.Videos;
import com.syj.service.VideoService;

@Service
public class VideoServiceImpl implements VideoService {

	@Autowired
	private VideosMapper videoMapper;

	@Autowired
	private Sid sid;

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void saveVideo(Videos video) {
		// 在controller是没有设置id，需要使用sid给他设置id
		video.setId(sid.nextShort());
		videoMapper.insertSelective(video);
	}

}
