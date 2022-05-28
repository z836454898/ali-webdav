package com.github.maruifu.webdav.util;

import com.github.maruifu.webdav.config.AliYunDriveProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class RefreshTokenManage {
	private static final Logger LOGGER = LoggerFactory.getLogger(RefreshTokenManage.class);
	@Autowired
	AliYunDriveProperties aliYunDriveProperties;
	
	/**
	 * 写入refreshToken到用户定义的硬盘文件路径中
	 *
	 * @param newRefreshToken 要写入的refreshToken
	 */
	public void writeRefreshToken(String newRefreshToken) {
		//获取用户定义的refreshToken，写入的文件名为refresh-token
		String refreshTokenPath = aliYunDriveProperties.getWorkDir() + "refresh-token";
		try {
			Files.writeString(Paths.get(refreshTokenPath), newRefreshToken);
		} catch (IOException e) {
			LOGGER.warn("写入refreshToken文件 {} 失败: ", refreshTokenPath, e);
		}
		//写入到内存中
		aliYunDriveProperties.setRefreshToken(newRefreshToken);
	}
	
	public String readRefreshToken() {
		//从硬盘文件中读取refresh-token
		String refreshTokenPath = aliYunDriveProperties.getWorkDir() + "refresh-token";
		Path path = Paths.get(refreshTokenPath);
		
		if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
			try {
				Files.createDirectories(path.getParent());
				Files.createFile(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			byte[] bytes = Files.readAllBytes(path);
			if (bytes.length != 0) {
				return new String(bytes, StandardCharsets.UTF_8);
			}
		} catch (IOException e) {
			LOGGER.warn("读取refreshToken文件 {} 失败: ", refreshTokenPath, e);
		}
		writeRefreshToken(aliYunDriveProperties.getRefreshToken());
		return aliYunDriveProperties.getRefreshToken();
	}
	
	/**
	 * 从硬盘中删除refreshToken
	 */
	public void deleteRefreshTokenFile() {
		String refreshTokenPath = aliYunDriveProperties.getWorkDir() + "refresh-token";
		Path path = Paths.get(refreshTokenPath);
		try {
			Files.delete(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
