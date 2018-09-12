package com.syj.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.syj.utils.RedisOperator;

@RestController
public class BasicController {

	@Autowired
	public RedisOperator redis;

	public static final String USER_REDIS_SESSION = "user-redis-session";

	// ffmpegexe所在目录
	public static final String FFMPEG_EXE = "E:\\soft\\ffmpeg\\bin\\ffmpeg.exe";

	// 文件保存的命名空间
	public static final String FILE_SPACE = "E:/CodeSpace/syj_videos_dev";

	// 每页分页的记录数
	public static final int PAGE_SIZE = 5;
}
