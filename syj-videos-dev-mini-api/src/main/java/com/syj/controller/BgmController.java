package com.syj.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.syj.service.BgmService;
import com.syj.utils.SyjJSONResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/bgm")
@Api(value = "背景音乐业务的接口", tags = { "背景音乐业务的controller" })
public class BgmController extends BasicController {

	@Autowired
	private BgmService bgmService;

	@PostMapping("/list")
	@ApiOperation(value = "获取背景音乐列表", notes = "获取背景音乐列表的接口")
	public SyjJSONResult List() {
		return SyjJSONResult.ok(bgmService.queryBgmList());
	}

}
