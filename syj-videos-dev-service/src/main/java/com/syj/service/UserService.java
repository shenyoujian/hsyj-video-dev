package com.syj.service;

import com.syj.pojo.Users;

public interface UserService {

	/**
	 * @Description: 判断用户名是否存在
	 */
	boolean queryUsernameIsExist(String username);
	
	/**
	 * @Description: 保存用户(用户注册)
	 */
	void saveUser(Users user);
	
	/**
	 * @Description: 根据用户名和密码查询用户
	 */
	Users queryUserForLogin(String username, String password);

	/**
	 * @Description: 更新用户
	 */
	void updateUserInfo(Users user);

	/**
	 * @Description:查询用户信息
	 */
	Users queryUserInfo(String userId);
}
