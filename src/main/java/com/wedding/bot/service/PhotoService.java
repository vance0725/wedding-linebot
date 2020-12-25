package com.wedding.bot.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.wedding.bot.model.Pic;

public interface PhotoService {
	
	/**
	 * 上傳檔案到VM/google Photo
	 * @param picList
	 * @param batchStr
	 * @return
	 */
	public int uploadFile(List<Pic> picList, String batchStr);
	
	/**
	 * 處理VM上檔案
	 * @param p
	 * @param in
	 * @throws IOException
	 */
	public void processPhoto(Pic p, InputStream in) throws IOException;
	
	/**
	 * 取得要分享的圖片
	 * 只抓圖片，不要影片
	 * @return
	 */
	public Pic getShowImage();
	
	/**
	 * 取得要抽獎的圖片
	 * 只抓圖片，不要影片
	 * @param size 圖片數量
	 * @return
	 */
	public List<Pic> getLotteryImages(Integer size);

}
