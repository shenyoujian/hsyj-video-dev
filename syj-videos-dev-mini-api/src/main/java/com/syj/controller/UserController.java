package com.syj.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.syj.pojo.Users;
import com.syj.pojo.vo.PublisherVideo;
import com.syj.pojo.vo.UsersVO;
import com.syj.service.UserService;
import com.syj.utils.SyjJSONResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "用户相关操作的接口", tags = { "用户相关操作的controller" })
@RequestMapping("/user")
public class UserController extends BasicController {

	@Autowired
	private UserService userService;

	@ApiOperation(value = "用户上传头像", notes = "用户上传头像的接口")
	@ApiImplicitParam(name = "userId", value = "用户Id", required = true, dataType = "String", paramType = "query")
	@PostMapping("/uploadFace")
	public SyjJSONResult upload(String userId, @RequestParam("file") MultipartFile[] files) throws Exception {

		if (StringUtils.isBlank(userId)) {
			return SyjJSONResult.errorMsg("用户id不能为空！");
		}

		// 文件保存的命名空间
		String fileSpace = "E:/CodeSpace/syj_videos_dev";
		// 保存到数据库的相对路径
		String uploadPathDB = "/" + userId + "/face";

		FileOutputStream fileOutputStream = null;
		InputStream inputStream = null;

		try {
			if (files != null && files.length > 0) {
				String fileName = files[0].getOriginalFilename();
				if (StringUtils.isNotBlank(fileName)) {
					// 文件上传的最终保存路径
					String finalFacePath = fileSpace + uploadPathDB + "/" + fileName;
					// 设置数据库保存路径
					uploadPathDB += "/" + fileName;

					// 根据最终保存路径开始创建文件夹
					File outFile = new File(finalFacePath);
					if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
						// 创建父文件夹
						outFile.getParentFile().mkdirs();
					}

					// 文件创建成功后开始写入
					fileOutputStream = new FileOutputStream(outFile);
					// 先读到内存
					inputStream = files[0].getInputStream();
					// 使用io工具类把读到内存里的开始写入硬盘
					IOUtils.copy(inputStream, fileOutputStream);

				} else {
					return SyjJSONResult.errorMsg("上传出错！");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return SyjJSONResult.errorMsg("上传出错！");
		} finally {
			if (fileOutputStream != null) {
				fileOutputStream.flush();
				fileOutputStream.close();
			}
		}

		// 上传和保存成功后，开始更新user
		Users user = new Users();
		user.setId(userId);
		user.setFaceImage(uploadPathDB);
		userService.updateUserInfo(user);

		// 返回存储路径使前端可以获取头像相对地址
		return SyjJSONResult.ok(uploadPathDB);

	}

	@ApiOperation(value = "查询用户信息", notes = "查询用户信息的接口")
	@ApiImplicitParam(name = "userId", value = "用户Id", required = true, dataType = "String", paramType = "query")
	@PostMapping("/query")
	public SyjJSONResult query(String userId) throws Exception {

		if (StringUtils.isBlank(userId)) {
			return SyjJSONResult.errorMsg("用户id不能为空...");
		}
		
		Users userInfo = userService.queryUserInfo(userId);
		UsersVO userVO = new UsersVO();
		//在vo里设置了jsonignore所以不需要自己设置
		//userVO.setPassword("");
		BeanUtils.copyProperties(userInfo, userVO);
		return SyjJSONResult.ok(userVO);

	}
	
	@ApiOperation(value = "查询视频发布者信息", notes = "查询视频发布者信息的接口")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "loginUserId", value = "用户Id", required = true, dataType = "String", paramType = "form"),
		@ApiImplicitParam(name = "videoId", value = "视频Id", required = true, dataType = "String", paramType = "form"),
		@ApiImplicitParam(name = "publishUserId", value = "视频发布者Id", required = true, dataType = "String", paramType = "form")
	})
	@PostMapping("/queryPublisher")
	public SyjJSONResult queryPublisher(String loginUserId, String videoId, 
			String publishUserId) throws Exception {
		
		if (StringUtils.isBlank(publishUserId)) {
			return SyjJSONResult.errorMsg("");
		}
		
		// 1. 查询视频发布者的信息
		Users userInfo = userService.queryUserInfo(publishUserId);
		UsersVO publisher = new UsersVO();
		BeanUtils.copyProperties(userInfo, publisher);
		
		// 2. 查询当前登录者和视频的点赞关系
		boolean userLikeVideo = userService.isUserLikeVideo(loginUserId, videoId);
		
		PublisherVideo bean = new PublisherVideo();
		bean.setPublisher(publisher);
		bean.setUserLikeVideo(userLikeVideo);
		
		return SyjJSONResult.ok(bean);
	}
}
