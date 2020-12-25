package com.wedding.bot.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.wedding.bot.model.Pic;

public interface GoogleService {
	
	/**
	 * 重新取得accessToken(因效期只有10分鐘)
	 * @return
	 */
	public String refreshAccessToken();
	
	/**
	 * 使用byte格式上傳影音
	 * @param accessToken
	 * @param in
	 * @return
	 */
	public String uploadingBytes(String accessToken, InputStream in, boolean isImg);
	
	/**
	 * 建立影音物件
	 * @param accessToken
	 * @param uploadToken
	 * @param fileName
	 * @param userName
	 * @return
	 */
	public Map<String, Object> createMediaItems(String accessToken, String uploadToken, String fileName, String userName);
	
	/**
	 * 上傳影音到google photo(整合function)
	 * @param batchStr
	 * @param p
	 * @param in
	 * @return
	 */
	public Pic uploadMedia(String batchStr, Pic p, InputStream in);
	
	/**
	 * 重新取得圖片baseUrl
	 * @param accessToken
	 * @return
	 */
	public List<Pic> refreshBaseUrl(String accessToken);
	
	/**
	 * 取得公開分享連結
	 * @param accessToken
	 * @param mediaItemId
	 * @return
	 */
	public String getBaseUrl(String accessToken, String mediaItemId);
	
}
