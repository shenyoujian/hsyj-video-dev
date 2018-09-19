package com.syj.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.syj.emums.VideoStatusEnum;
import com.syj.pojo.Bgm;
import com.syj.pojo.Comments;
import com.syj.pojo.Videos;
import com.syj.service.BgmService;
import com.syj.service.UserService;
import com.syj.service.VideoService;
import com.syj.utils.FetchVideoCover;
import com.syj.utils.MergeVideoMp3;
import com.syj.utils.PagedResult;
import com.syj.utils.SyjJSONResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/video")
@Api(value = "视频相关操作的接口", tags = { "视频相关操作的controller" })
public class VideoController extends BasicController {

	@Autowired
	private UserService userService;

	@Autowired
	private BgmService bgmService;

	@Autowired
	private VideoService videoService;

	/**
	 * @Description:用户上传视频
	 */
	@ApiOperation(value = "用户上传视频", notes = "用户上传视频的接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "userId", value = "用户Id", required = true, dataType = "String", paramType = "form"),
			@ApiImplicitParam(name = "bgmId", value = "背景音乐Id", required = false, dataType = "String", paramType = "form"),
			@ApiImplicitParam(name = "videoSeconds", value = "视频长度", required = true, dataType = "String", paramType = "form"),
			@ApiImplicitParam(name = "videoHeight", value = "视频高度", required = true, dataType = "String", paramType = "form"),
			@ApiImplicitParam(name = "videoWidth", value = "视频宽度", required = true, dataType = "String", paramType = "form"),
			@ApiImplicitParam(name = "desc", value = "视频描述", required = false, dataType = "String", paramType = "form"), })
	@PostMapping(value = "/upload", headers = "content-type=multipart/form-data")
	public SyjJSONResult upload(String userId, String bgmId, double videoSeconds, int videoHeight, int videoWidth,
			String desc, @ApiParam(value = "短视频", required = true) MultipartFile file) throws Exception {

		if (StringUtils.isBlank(userId)) {
			return SyjJSONResult.errorMsg("用户id不能为空！");
		}

		// 文件保存的命名空间
		// 不要使用魔鬼数字
		// String fileSpace = "E:/CodeSpace/syj_videos_dev";
		// 保存到数据库的相对路径
		String uploadPathDB = "/" + userId + "/video";
		String coverPathDB = "/" + userId + "/video";

		FileOutputStream fileOutputStream = null;
		InputStream inputStream = null;
		// 文件上传的最终保存路径
		String finalVideoPath = "";

		try {
			if (file != null) {
				String fileName = file.getOriginalFilename();

				// 对上传的视频名字进行分割用于封面名字,.需要进行转义
				String fileNamePrefix = fileName.split("\\.")[0];

				if (StringUtils.isNotBlank(fileName)) {
					finalVideoPath = FILE_SPACE + uploadPathDB + "/" + fileName;
					// 设置数据库保存路径
					uploadPathDB += "/" + fileName;
					coverPathDB += "/" + fileNamePrefix + ".jpg";
					// 根据最终保存路径开始创建文件夹
					File outFile = new File(finalVideoPath);
					if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
						// 创建父文件夹
						outFile.getParentFile().mkdirs();
					}

					// 文件创建成功后开始写入
					fileOutputStream = new FileOutputStream(outFile);
					// 先读到内存
					inputStream = file.getInputStream();
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

		// 上传成功后，判断bgmId是否为空，如果不为空
		// 那就查询bgm信息，然后合并视频，并且生成新的视频
		if (StringUtils.isNotBlank(bgmId)) {
			Bgm bgm = bgmService.queryBgmById(bgmId);
			// MergeVideoMp3 mvm = new MergeVideoMp3("E:\\soft\\ffmpeg\\bin\\ffmpeg.exe");
			// 不要魔鬼数字
			// 开始整合
			MergeVideoMp3 mvm = new MergeVideoMp3(FFMPEG_EXE);
			String mp3InputPath = FILE_SPACE + bgm.getPath();
			String videoInputPath = finalVideoPath;
			// 生成随机名字
			String videoOutputName = UUID.randomUUID().toString() + ".mp4";
			uploadPathDB = "/" + userId + "/video" + "/" + videoOutputName;
			finalVideoPath = FILE_SPACE + uploadPathDB;
			mvm.convertor(videoInputPath, mp3InputPath, videoSeconds, finalVideoPath);
		}

		System.out.println("uploadPathDB:" + uploadPathDB);
		System.out.println("finalVideoPath:" + finalVideoPath);

		// 对视频进行截图
		FetchVideoCover videoInfo = new FetchVideoCover(FFMPEG_EXE);
		videoInfo.getCover(finalVideoPath, FILE_SPACE + coverPathDB);

		// 保存视频信息到数据库
		Videos video = new Videos();
		video.setAudioId(bgmId);
		video.setUserId(userId);
		video.setVideoHeight(videoHeight);
		video.setVideoSeconds((float) videoSeconds);
		video.setVideoWidth(videoWidth);
		video.setVideoPath(uploadPathDB);
		video.setVideoDesc(desc);
		video.setStatus(VideoStatusEnum.SUCCESS.value);
		video.setCreateTime(new Date());
		video.setCoverPath(coverPathDB);

		String videoId = videoService.saveVideo(video);

		// 保存成功后返回视频id，是前端success的res有这个id
		// 然后上传的封面就可以保存到对应的视频里
		return SyjJSONResult.ok(videoId);

	}

	@ApiOperation(value = "用户上传视频封面", notes = "用户上传视频封面的接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "videoId", value = "视频主键Id", required = true, dataType = "String", paramType = "form"),
			@ApiImplicitParam(name = "userId", value = "用户Id", required = true, dataType = "String", paramType = "form"), })
	@PostMapping(value = "/uploadCover", headers = "content-type=multipart/form-data")
	public SyjJSONResult upload(String videoId, String userId,
			@ApiParam(value = "视频封面", required = true) MultipartFile file) throws Exception {

		if (StringUtils.isBlank(videoId) || StringUtils.isBlank(userId)) {
			return SyjJSONResult.errorMsg("视频主键和用户id不能为空！");
		}

		// 文件保存的命名空间
		// 不要使用魔鬼数字
		// String fileSpace = "E:/CodeSpace/syj_videos_dev";
		// 保存到数据库的相对路径，和视频保存在一起
		String uploadPathDB = "/" + userId + "/video";

		FileOutputStream fileOutputStream = null;
		InputStream inputStream = null;
		// 文件上传的最终保存路径
		String finalVideoPath = "";

		try {
			if (file != null) {
				String fileName = file.getOriginalFilename();
				if (StringUtils.isNotBlank(fileName)) {
					finalVideoPath = FILE_SPACE + uploadPathDB + "/" + fileName;
					// 设置数据库保存路径
					uploadPathDB += "/" + fileName;

					// 根据最终保存路径开始创建文件夹
					File outFile = new File(finalVideoPath);
					if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
						// 创建父文件夹
						outFile.getParentFile().mkdirs();
					}

					// 文件创建成功后开始写入
					fileOutputStream = new FileOutputStream(outFile);
					// 先读到内存
					inputStream = file.getInputStream();
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

		System.out.println("uploadPathDB:" + uploadPathDB);
		System.out.println("finalVideoPath:" + finalVideoPath);

		// 更新视频封面信息到数据库
		videoService.updateVideo(videoId, uploadPathDB);

		return SyjJSONResult.ok();

	}

	/**
	 * @Description:分页和搜索查询视频列表 isSaveRecord：1 - 代表需要保存 0 - 不需要保存，或者为空
	 */
	@PostMapping(value = "/showAll")
	public SyjJSONResult showAll(@RequestBody Videos video, Integer isSaveRecord, Integer page) {
		if (page == null) {
			page = 1;
		}
		PagedResult result = videoService.getAllVideos(video, isSaveRecord, page, PAGE_SIZE);
		return SyjJSONResult.ok(result);
	}
	
	
	/**
	 * @Description: 我关注的人发的视频
	 */
	@PostMapping("/showMyFollow")
	public SyjJSONResult showMyFollow(String userId, Integer page) throws Exception {
		
		if (StringUtils.isBlank(userId)) {
			return SyjJSONResult.ok();
		}
		
		if (page == null) {
			page = 1;
		}

		int pageSize = 6;
		
		PagedResult videosList = videoService.queryMyFollowVideos(userId, page, pageSize);
		
		return SyjJSONResult.ok(videosList);
	}
	
	
	/**
	 * @Description: 我收藏(点赞)过的视频列表
	 */
	@PostMapping("/showMyLike")
	public SyjJSONResult showMyLike(String userId, Integer page, Integer pageSize) throws Exception {
		
		if (StringUtils.isBlank(userId)) {
			return SyjJSONResult.ok();
		}
		
		if (page == null) {
			page = 1;
		}

		if (pageSize == null) {
			pageSize = 6;
		}
		
		PagedResult videosList = videoService.queryMyLikeVideos(userId, page, pageSize);
		
		return SyjJSONResult.ok(videosList);
	}
	

	/**
	 * @Description:
	 */
	@PostMapping(value = "/hot")
	public SyjJSONResult hot() throws Exception {
		return SyjJSONResult.ok(videoService.getHotwords());
	}

	
	@PostMapping(value = "/userLike")
	@ApiOperation(value="用户点赞视频",notes="用户点赞视频接口")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "userId", value = "用户Id", required = true, dataType = "String", paramType = "form"),
		@ApiImplicitParam(name = "videoId", value = "视频Id", required = true, dataType = "String", paramType = "form"), 
		@ApiImplicitParam(name = "videoCreaterId", value = "上传视频用户Id", required = true, dataType = "String", paramType = "form"), 
		})
	public SyjJSONResult userLike(String userId, String videoId, String videoCreaterId) throws Exception {
		videoService.userLikeVideo(userId, videoId, videoCreaterId);
		return SyjJSONResult.ok("点赞成功...");
	}
	
	

	@PostMapping(value = "/userUnLike")
	@ApiOperation(value="用户点赞视频",notes="用户点赞视频接口")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "userId", value = "用户Id", required = true, dataType = "String", paramType = "form"),
		@ApiImplicitParam(name = "videoId", value = "视频Id", required = true, dataType = "String", paramType = "form"), 
		@ApiImplicitParam(name = "videoCreaterId", value = "上传视频用户Id", required = true, dataType = "String", paramType = "form"), 
		})
	public SyjJSONResult userUnLike(String userId, String videoId, String videoCreaterId) throws Exception {
		videoService.userUnLikeVideo(userId, videoId, videoCreaterId);
		return SyjJSONResult.ok("取消点赞成功...");
	}
	
	@PostMapping("/saveComment")
	public SyjJSONResult saveComment(@RequestBody Comments comment) throws Exception{
		
		videoService.saveComment(comment);
		return SyjJSONResult.ok("发表留言成功...");
	}

}
