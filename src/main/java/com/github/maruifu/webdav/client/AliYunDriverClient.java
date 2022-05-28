package com.github.maruifu.webdav.client;

import com.github.maruifu.webdav.client.okhttp.Authenticator;
import com.github.maruifu.webdav.client.okhttp.Interceptor;
import com.github.maruifu.webdav.config.AliYunDriveProperties;
import com.github.maruifu.webdav.util.JsonUtil;
import net.sf.webdav.exceptions.WebdavException;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AliYunDriverClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(AliYunDriverClient.class);
	private OkHttpClient okHttpClient;
	private AliYunDriveProperties aliYunDriveProperties;
	
	public AliYunDriverClient(
		AliYunDriveProperties aliYunDriveProperties,
		Interceptor interceptor,
		Authenticator authenticator
	) {
//		添加请求拦截器，用于身份验证
		this.okHttpClient = new OkHttpClient.Builder().addInterceptor(interceptor)
			// 添加验证器用于获取accessToken和captchaToken
			.authenticator(authenticator)
			.readTimeout(1, TimeUnit.MINUTES)
			.writeTimeout(1, TimeUnit.MINUTES)
			.connectTimeout(1, TimeUnit.MINUTES)
			.build();
		this.aliYunDriveProperties = aliYunDriveProperties;
		init(); //获取client_id
	}
	
	private void login() {
		// todo 暂不支持登录功能
	}
	
	/**
	 * 初始化时获取client_id
	 */
	public void init() {
		login();
		if (getDriveId() == null) {
			String personalJson = post("/user/get", Collections.emptyMap());
			String driveId = (String) JsonUtil.getJsonNodeValue(personalJson, "default_drive_id");
			aliYunDriveProperties.setDriveId(driveId);
		}
	}
	
	
	public String getDriveId() {
		return aliYunDriveProperties.getClient_id();
	}
	
	/**
	 * 判断请求头中的range是否超出size的范围，如果超出了则抹去
	 *
	 * @param url                请求url
	 * @param httpServletRequest 请求
	 * @param size               文件大小
	 * @return 请求响应
	 */
	public Response download(String url, HttpServletRequest httpServletRequest, long size) {
		//解决跨域问题
		Request.Builder builder = new Request.Builder().header("referer", "https://pan.xunlei.com/");
		//下载文件的范围，支持断点续传
		String range = httpServletRequest.getHeader("range");
		if (range != null) {
			// 如果range最后 >= size， 则去掉
			String[] split = range.split("-");
			if (split.length == 2) {
				String end = split[1];
				if (Long.parseLong(end) >= size) {
					range = range.substring(0, range.lastIndexOf('-') + 1);
				}
			}
			builder.header("range", range);
		}
		
		String ifRange = httpServletRequest.getHeader("if-range");
		if (ifRange != null) {
			builder.header("if-range", ifRange);
		}
		
		
		Request request = builder.url(url).build();
		Response response = null;
		try {
			response = okHttpClient.newCall(request).execute();
			return response;
		} catch (IOException e) {
			throw new WebdavException(e);
		}
	}
	
	public void upload(String url, byte[] bytes, final int offset, final int byteCount) {
		Request request = new Request.Builder()
			.put(RequestBody.create(MediaType.parse(""), bytes, offset, byteCount))
			.url(url).build();
		try (Response response = okHttpClient.newCall(request).execute()) {
			LOGGER.info("upload {}, code {}", url, response.code());
			if (!response.isSuccessful()) {
				LOGGER.error("请求失败，url={}, code={}, body={}", url, response.code(), response.body().string());
				throw new WebdavException("请求失败：" + url);
			}
		} catch (IOException e) {
			throw new WebdavException(e);
		}
	}
	
	/**
	 * 发送post请求
	 *
	 * @param url  请求url
	 * @param body 请求体，实体类形式
	 * @return 响应体，字符串形式
	 */
	public String post(String url, Object body) {
		//将实体类转为Json字符串类型
		String bodyAsJson = JsonUtil.toJson(body);
		Request request = new Request.Builder()
			.post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), bodyAsJson))
			.url(getTotalUrl(url)).build();
		try (Response response = okHttpClient.newCall(request).execute()) {
			LOGGER.info("post {}, body {}, code {}", url, bodyAsJson, response.code());
			if (!response.isSuccessful()) {
				LOGGER.error("请求失败，url={}, code={}, body={}", url, response.code(), response.body().string());
				throw new WebdavException("请求失败：" + url);
			}
			return toString(response.body());
		} catch (IOException e) {
			throw new WebdavException(e);
		}
	}
	
	/**
	 * 发送put请求
	 *
	 * @param url  请求url
	 * @param body 请求体，实体类形式
	 * @return 响应体String字符串形式
	 */
	public String put(String url, Object body) {
		Request request = new Request.Builder()
			.put(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), JsonUtil.toJson(body)))
			.url(getTotalUrl(url)).build();
		try (Response response = okHttpClient.newCall(request).execute()) {
			LOGGER.info("put {}, code {}", url, response.code());
			if (!response.isSuccessful()) {
				LOGGER.error("请求失败，url={}, code={}, body={}", url, response.code(), response.body().string());
				throw new WebdavException("请求失败：" + url);
			}
			return toString(response.body());
		} catch (IOException e) {
			throw new WebdavException(e);
		}
	}
	
	/**
	 * 发送get请求
	 *
	 * @param url    请求url
	 * @param params 请求参数
	 * @return 响应体的String字符串形式
	 */
	public String get(String url, Map<String, String> params) {
		try {
			HttpUrl.Builder urlBuilder = HttpUrl.parse(getTotalUrl(url)).newBuilder();
			params.forEach(urlBuilder::addQueryParameter);
			
			Request request = new Request.Builder().get().url(urlBuilder.build()).build();
			try (Response response = okHttpClient.newCall(request).execute()) {
				LOGGER.info("get {}, code {}", urlBuilder.build(), response.code());
				if (!response.isSuccessful()) {
					throw new WebdavException("请求失败：" + urlBuilder.build().toString());
				}
				return toString(response.body());
			}
			
		} catch (Exception e) {
			throw new WebdavException(e);
		}
		
	}
	
	/**
	 * 以字符串的形式返回responseBody响应
	 */
	private String toString(ResponseBody responseBody) throws IOException {
		if (responseBody == null) {
			return null;
		}
		return responseBody.string();
	}
	
	/**
	 * 根据用户配置信息，拼接url前缀
	 *
	 * @param url 源url
	 * @return 拼接好的url
	 */
	private String getTotalUrl(String url) {
		if (url.startsWith("http")) {
			return url;
		}
		return aliYunDriveProperties.getUrl() + url;
	}
}
