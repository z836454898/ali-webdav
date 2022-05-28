package com.github.maruifu.webdav.client.okhttp;

import com.github.maruifu.webdav.config.AliYunDriveProperties;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class Interceptor implements okhttp3.Interceptor {
	@Autowired
	AliYunDriveProperties aliYunDriveProperties;
	
	@Override
	public Response intercept(Chain chain) throws IOException {
		Request request = chain.request();
//			添加请求头
		request = request.newBuilder()
			.removeHeader("User-Agent")
			.addHeader("User-Agent", aliYunDriveProperties.getAgent())
			.removeHeader("authorization")
			.addHeader("authorization", aliYunDriveProperties.getAuthorization())
			.removeHeader("x-device-id")
			.addHeader("x-device-id", aliYunDriveProperties.getDevice_id())
			.removeHeader("x-client-id")
			.addHeader("x-client-id", aliYunDriveProperties.getClient_id())
			.removeHeader("x-captcha-token")
			.addHeader("x-captcha-token", aliYunDriveProperties.getCaptcha_token())
			.build();
		return chain.proceed(request);
//			如果accessToken已过期，利用refreshToken刷新token
	
	}
}
