package com.syj.controller.interceptor;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.syj.utils.JsonUtils;
import com.syj.utils.RedisOperator;
import com.syj.utils.SyjJSONResult;

public class MiniInterceptor implements HandlerInterceptor {

	@Autowired
	public RedisOperator redis;
	public static final String USER_REDIS_SESSION = "user-redis-session";

	/**
	 * 拦截请求，在调用controller方法之前
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		//获取前端header传递的userId和userToken
		String userId = request.getHeader("userId");
		String userToken = request.getHeader("userToken");
		
		//进行判断,需要两个都不为空,否则重新登录
		if(StringUtils.isNotBlank(userId)&&StringUtils.isNotBlank(userToken)) {
			//不为空就取出redis的token判断和header是否一致
			String uniqueToken = redis.get(USER_REDIS_SESSION + ":" + userId);
			if (StringUtils.isEmpty(uniqueToken) && StringUtils.isBlank(uniqueToken)) {
				System.out.println("请登录...");
				returnErrorResponse(response, new SyjJSONResult().errorTokenMsg("请登录..."));
				return false;
			}else {
				if (!uniqueToken.equals(userToken)) {
					System.out.println("账号被挤出...");
					returnErrorResponse(response, new SyjJSONResult().errorTokenMsg("账号被挤出..."));
					return false;
				}
			}
		}else {
			System.out.println("请登录...");
			returnErrorResponse(response, new SyjJSONResult().errorTokenMsg("请登录..."));
			return false;
		}
		
		
		/**
		 * 返回 false：请求被拦截，返回
		 * 返回 true ：请求OK，可以继续执行，放行
		 */
		return true;
	}

	public void returnErrorResponse(HttpServletResponse response, SyjJSONResult result)
			throws IOException, UnsupportedEncodingException {
		OutputStream out = null;
		try {
			response.setCharacterEncoding("utf-8");
			response.setContentType("text/json");
			out = response.getOutputStream();
			out.write(JsonUtils.objectToJson(result).getBytes("utf-8"));
			out.flush();
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	/**
	 * contorller调用后，渲染视图之前
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {

	}

}
