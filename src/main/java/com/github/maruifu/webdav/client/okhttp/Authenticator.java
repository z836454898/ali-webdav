package com.github.maruifu.webdav.client.okhttp;

import com.github.maruifu.webdav.client.AliYunDriverClient;
import com.github.maruifu.webdav.config.AliYunDriveProperties;
import com.github.maruifu.webdav.model.Auth.GetCaptchaTokenRequest;
import com.github.maruifu.webdav.model.Auth.GetTokenRequest;
import com.github.maruifu.webdav.util.JsonUtil;
import com.github.maruifu.webdav.util.RefreshTokenManage;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

@Component
public class Authenticator implements okhttp3.Authenticator {
	@Autowired
	AliYunDriveProperties aliYunDriveProperties;
	@Autowired
	AliYunDriverClient aliYunDriverClient;
	@Autowired
	RefreshTokenManage tokenManage;
	
	// 如果accessToken已过期或者首次使用没有accessToken则通过refreshToken来获取accessToken
	@Override
	public Request authenticate(Route route, Response response) throws IOException {
		// 获取captchaToken
		boolean captchaTokenChanged = getCaptchaToken(response);
		if (response.code() == 401 && response.body() != null && response.body().string().contains("unauthenticated")) {
// 获取accessToken
			String accessToken = getAccessToken(response);
			// 加入到的token到请求头中
			return response.request().newBuilder()
				.removeHeader("authorization")
				.header("authorization", accessToken)
				.removeHeader("x-captcha-token")
				.header("x-captcha-token", aliYunDriveProperties.getCaptcha_token())
				.build();
		}
		//加入获取到的captchaToken
		if (captchaTokenChanged) {
			return response.request().newBuilder()
				.removeHeader("x-captcha-token")
				.header("x-captcha-token", aliYunDriveProperties.getCaptcha_token())
				.build();
		}
		return null; //放弃验证
	}
	
	//获取captchaToken
	private boolean getCaptchaToken(Response response) throws IOException {
		// 没有captchaToken或者captchaToken为空
		if (
			response.body() != null &&
				response.body().string().contains("captcha_invalid")
//			aliYunDriveProperties.getCaptcha_token() == null ||
//				Objects.equals(aliYunDriveProperties.getCaptcha_token(), "") ||
//				aliYunDriveProperties.getExpires_at().after(new Date())
		) {
			// 发请求获取Captcha_Token
			GetCaptchaTokenRequest captchaTokenRequest = new GetCaptchaTokenRequest();
			String captchaTokenResult = aliYunDriverClient.post(aliYunDriveProperties.getGetCaptchaTokenUrl(), captchaTokenRequest);
			String captchaToken = (String) JsonUtil.getJsonNodeValue(captchaTokenResult, "captcha_token");
			// 把captchaToken存到AliyunDriveProperties对象里
			aliYunDriveProperties.setCaptcha_token(captchaToken);
//					设置过期时间
			Date now = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(now);
			calendar.add(Calendar.MINUTE, 5);//时间加5分钟
			aliYunDriveProperties.setExpires_at(calendar.getTime());
			return true;
		} else {
			return false;
		}
	}
	
	//获取accessToken
	private String getAccessToken(Response response) throws IOException {
		// 响应状态码为401 响应体为空 响应体包含「unauthenticated」字段
		String refreshTokenResult;
		GetTokenRequest tokenRequest = new GetTokenRequest();
		tokenRequest.setClient_id(aliYunDriveProperties.getClient_id());
		tokenRequest.setRefresh_token(tokenManage.readRefreshToken());
		try {
			refreshTokenResult = aliYunDriverClient.post(aliYunDriveProperties.getGetTokenUrl(), tokenRequest);
//					refreshTokenResult = post(aliYunDriveProperties.getGetTokenUrl(), Collections.singletonMap("refresh_token", readRefreshToken()));
		} catch (Exception e) {
			// 如果置换token失败，先清空原token文件，再尝试一次
			tokenManage.deleteRefreshTokenFile();
			refreshTokenResult = aliYunDriverClient.post(aliYunDriveProperties.getGetTokenUrl(), tokenRequest);
//					refreshTokenResult = post(aliYunDriveProperties.getGetTokenUrl(), Collections.singletonMap("refresh_token", readRefreshToken()));
		}
		//获取到的新accessToken和refreshToken
		String accessToken = (String) JsonUtil.getJsonNodeValue(refreshTokenResult, aliYunDriveProperties.getAccessTokenJsonKey());
		String refreshToken = (String) JsonUtil.getJsonNodeValue(refreshTokenResult, aliYunDriveProperties.getRefreshTokenJsonKey());
		//判断是否能成功获取token
		Assert.hasLength(accessToken, "获取accessToken失败");
		Assert.hasLength(refreshToken, "获取refreshToken失败");
		//把accessToken存储到aliyunDriveProperties对象中
		accessToken = "Bearer " + accessToken; // 迅雷云盘的accessToken需要加前缀
		aliYunDriveProperties.setAuthorization(accessToken);
		tokenManage.writeRefreshToken(refreshToken);
		return accessToken;
	}
}
