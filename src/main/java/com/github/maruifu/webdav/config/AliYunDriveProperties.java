package com.github.maruifu.webdav.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@ConfigurationProperties(prefix = "aliyundrive", ignoreUnknownFields = true)
@Data
public class AliYunDriveProperties {
	private String url = "https://api-pan.xunlei.com/drive/v1";
	
	private String getTokenUrl = "https://xluser-ssl.xunlei.com/v1/auth/token";
	private String authorization = "";
	
	private String getCaptchaTokenUrl = "https://xluser-ssl.xunlei.com/v1/shield/captcha/init";
	private String refreshToken;
	
	private String refreshTokenJsonKey = "refresh_token";
	
	private String accessTokenJsonKey = "access_token";
	
	private String client_id;
	
	private String device_id;
	
	private String captcha_token = "";
	
	private Date expires_at; //captcha_token的过期时间
	
	private String workDir = "/usr/local/java/docker/";
	private String agent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_0_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36";
	private String driveId;
	private Auth auth;
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getAuthorization() {
		return authorization;
	}
	
	public void setAuthorization(String authorization) {
		this.authorization = authorization;
	}
	
	public String getAgent() {
		return agent;
	}
	
	public void setAgent(String agent) {
		this.agent = agent;
	}
	
	public String getDriveId() {
		return driveId;
	}
	
	public String getRefreshToken() {
		return refreshToken;
	}
	
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
	public String getWorkDir() {
		return workDir;
	}
	
	public void setWorkDir(String workDir) {
		this.workDir = workDir;
	}
	
	public void setDriveId(String driveId) {
		this.driveId = driveId;
	}
	
	public Auth getAuth() {
		return auth;
	}
	
	public void setAuth(Auth auth) {
		this.auth = auth;
	}
	
	public static class Auth {
		private Boolean enable = true;
		private String username;
		private String password;
		
		public Boolean getEnable() {
			return enable;
		}
		
		public void setEnable(Boolean enable) {
			this.enable = enable;
		}
		
		
		public String getUsername() {
			return username;
		}
		
		public void setUsername(String username) {
			this.username = username;
		}
		
		public String getPassword() {
			return password;
		}
		
		public void setPassword(String password) {
			this.password = password;
		}
	}
}
