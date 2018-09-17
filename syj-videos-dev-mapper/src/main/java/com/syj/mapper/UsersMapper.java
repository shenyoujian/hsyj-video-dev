package com.syj.mapper;

import com.syj.pojo.Users;
import com.syj.utils.MyMapper;

public interface UsersMapper extends MyMapper<Users> {
	/**
	 * @Description: 用户受喜欢数累加
	 */
	public void addReceiveLikeCount(String userId);
	
	/**
	 * @Description: 用户受喜欢数累减
	 */
	public void reduceReceiveLikeCount(String userId);
}