package com.syj.controller;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.util.StringUtil;
import com.syj.pojo.Users;
import com.syj.service.UserService;
import com.syj.utils.SyjJSONResult;
import com.syj.utils.MD5Utils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "用户注册登录的接口", tags = { "注册和登录的controller" })
public class RegistLoginController {

	@Autowired
	private UserService userService;

	@ApiOperation(value = "用户注册", notes = "用户注册的接口")
	@PostMapping("/regist")
	public SyjJSONResult regist(@RequestBody Users user) throws Exception {

		// 1、判断用户名和密码必须不为空
		if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())) {
			return SyjJSONResult.errorMsg("用户名和密码不能为空");
		}

		// 2、判断用户名是否存在
		boolean usernameIsExist = userService.queryUsernameIsExist(user.getUsername());

		// 3、保存用户，注册信息
		if (!usernameIsExist) {
			user.setNickname(user.getUsername());
			user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
			user.setFansCounts(0);
			user.setFollowCounts(0);
			user.setReceiveLikeCounts(0);
			userService.saveUser(user);
		} else {
			return SyjJSONResult.errorMsg("用户名已经存在，请换一个！");
		}

		// 4、user返回给前端数据，为了安全起见，不需要传密码给前端
		user.setPassword("");
		return SyjJSONResult.ok(user);
	}

	@ApiOperation(value = "用户登录", notes = "用户登录的接口")
	@PostMapping("/login")
	public SyjJSONResult login(@RequestBody Users user) throws Exception {

		String username = user.getUsername();
		String password = user.getPassword();

		// 1、判断用户名和密码是否为空
		if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
			return SyjJSONResult.errorMsg("用户名和密码不能为空！");
		}

		// 2、检查用户名和密码是否正确
		Users userResult = userService.queryUserForLogin(username, MD5Utils.getMD5Str(password));

		// 3、user返回给前端数据，为了安全起见，不需要传密码给前端
		if (userResult != null) {
			user.setPassword("");
			return SyjJSONResult.ok(user);
		} else {
			return SyjJSONResult.errorMsg("用户名或密码错误！");
		}
	}

}
