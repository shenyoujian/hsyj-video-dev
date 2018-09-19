package com.syj.mapper;

import java.util.List;

import com.syj.pojo.Comments;
import com.syj.pojo.vo.CommentsVO;
import com.syj.utils.MyMapper;

public interface CommentsMapper extends MyMapper<Comments> {

	/**
	 * @Description:查询某个视频的所有留言
	 */
	List<CommentsVO> queryComments(String videoId);
}