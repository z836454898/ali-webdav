package com.github.maruifu.webdav.config;

import com.github.maruifu.webdav.client.AliYunDriverClient;
import com.github.maruifu.webdav.client.okhttp.Authenticator;
import com.github.maruifu.webdav.client.okhttp.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@EnableConfigurationProperties注解 使使用 @ConfigurationProperties 注解的类生效
public class AliYunDriverAutoConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(AliYunDriverAutoConfig.class);
	
	@Autowired
	private AliYunDriveProperties aliYunDriveProperties;
	
	@Autowired
	private Interceptor interceptor;
	
	@Autowired
	private Authenticator authenticator;
	
	@Bean
	public AliYunDriverClient getAliyunDriverClient(ApplicationContext applicationContext) throws Exception {
		return new AliYunDriverClient(aliYunDriveProperties, interceptor, authenticator);
	}
}
