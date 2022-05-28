package com.github.maruifu.webdav.model.Auth;

import lombok.Data;

@Data
public class GetTokenRequest {
	private String client_id;
	private String grant_type="refresh_token";
	private String refresh_token;
}
