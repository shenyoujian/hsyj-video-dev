package com.syj.mapper;

import java.util.List;

import com.syj.pojo.SearchRecords;
import com.syj.utils.MyMapper;

public interface SearchRecordsMapper extends MyMapper<SearchRecords> {
	
	List<String> getHotwords();

}