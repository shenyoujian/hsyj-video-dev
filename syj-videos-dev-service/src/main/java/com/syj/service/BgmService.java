package com.syj.service;

import java.util.List;

import com.syj.pojo.Bgm;

public interface BgmService {

	
	/**
	 * @Description:查询Bgm列表
	 */
	List<Bgm> queryBgmList();
	
	/**
	 * @Description:根据id获取bgm
	 */
	Bgm queryBgmById(String bgmId);
	
	
	
}
