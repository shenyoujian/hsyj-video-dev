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
}
