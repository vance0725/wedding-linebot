package com.wedding.bot.service;

import java.io.InputStream;
import java.util.List;

import com.wedding.bot.model.LineProfile;
import com.wedding.bot.model.Pic;

public interface LineService {
	
	/**
	 * 取得line用戶資訊
	 * @param userId
	 * @return
	 */
	public LineProfile getLineUserProfile(String userId);
	
	/**
	 * 取得line media資訊(若影片檔案太大，會丟出http status code 500)
	 * @param messageId
	 * @return
	 * @throws Exception
	 */
	public InputStream getLineContent(String messageId) throws Exception;
	
	public List<Pic> getUncheckedPics();
	
	public void saveCheckedPic(List<String> validPics);

}
