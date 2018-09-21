package com.syj.mapper;

import java.util.List;

import com.syj.pojo.Comments;
import com.syj.pojo.vo.CommentsVO;
import com.syj.utils.MyMapper;

public interface CommentsMapperCustom extends MyMapper<Comments> {
	List<CommentsVO> queryComments(String videoId);
}