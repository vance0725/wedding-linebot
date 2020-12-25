package com.wedding.bot.service;

import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.ImageMessageContent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.message.VideoMessageContent;

/**
 * Line SDK, 訊息類型分類&處理
 * @author vance
 *
 */
public interface MessageService {
	
	/**
	 * 預設訊息類別
	 * @param event
	 */
	public void handleDefaultMsg(Event event);
	
	/**
	 * 文字訊息
	 * @param event
	 */
	public void handleTextMsg(MessageEvent<TextMessageContent> event);
	
	/**
	 * 圖片訊息
	 * @param event
	 */
	public void handleImageMsg(MessageEvent<ImageMessageContent> event);
	
	/**
	 * 影片訊息
	 * @param event
	 */
	public void handleVideoMsg(MessageEvent<VideoMessageContent> event);
	
	/**
	 * 地點訊息
	 * @param event
	 */
	public void handleLocationMsg(MessageEvent<LocationMessageContent> event);
	
	/**
	 * 回應予user之訊息
	 * @param replyToken
	 * @param message
	 */
	public void replyText(String replyToken, String message);
	
}
