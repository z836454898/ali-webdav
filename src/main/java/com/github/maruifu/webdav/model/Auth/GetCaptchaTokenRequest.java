package com.github.maruifu.webdav.model.Auth;

import lombok.Data;

import java.io.Serializable;

@Data
public class GetCaptchaTokenRequest implements Serializable {
	private String action = "get:/drive/v1/about";
	//	private String captcha_token;
	private String client_id = "Xqp0kJBXWhwaTpB6";
	private String device_id = "de89df8ba0e69d5cee22c468bfc8ef2f";
	
	private Meta meta = new Meta();
}

@Data
class Meta {
	private String captcha_sign = "1.b7fa83a5f07b26d29363bb8f21b817f8";
	private String client_version = "1.55.1";
	private String email = "";
	private String package_name = "pan.xunlei.com";
	private String phone_number = "";
	private String timestamp;
	private String user_id = "160361321";
	private String username = "";
	
	public Meta() {
		this.timestamp = System.currentTimeMillis() / 1000L + "596";
	}
}