package com.wedding.bot.service.impl;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.ImageMessageContent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.message.VideoMessageContent;
import com.wedding.bot.model.Button;
import com.wedding.bot.model.LineProfile;
import com.wedding.bot.model.Pic;
import com.wedding.bot.repo.ButtonRepo;
import com.wedding.bot.repo.PicRepo;
import com.wedding.bot.service.LineService;
import com.wedding.bot.service.MarqueeService;
import com.wedding.bot.service.MessageService;

import lombok.extern.slf4j.Slf4j;

@Service("MessageService")
@Slf4j
public class MessageServiceImpl implements MessageService {
	
	private static final String CHARSET = "UTF-8";
	private static final String BR = "\\n";
	private static final String IMAGE_TYPE = "image";
	private static final String VIDEO_TYPE = "video";
	
	@Value("${line.bot.button}")
	private String[] buttons;
	@Value("${line.bot.accessToken}")
	private String accessToken;
	@Value("${line.bot.replyUrl}")
	private String replyUrl;
	@Value("${wedding.loaction}")
	private String weddingLocation;
	@Autowired
	private LineService lineService;
	@Autowired
	private MarqueeService marqueeService;
	@Autowired
	private PicRepo picRepo;
	@Autowired
	private ButtonRepo buttonRepo;
	
	public void handleDefaultMsg(Event event) {
		event.getSource().getUserId();
	}
	
	public void handleTextMsg(MessageEvent<TextMessageContent> event) {
		String input = event.getMessage().getText();
		boolean reply = true;
		for (String s : buttons) {
			if (s.equals(input)) {
				reply = false;
				addButtonLog(input, event.getSource().getUserId());
				break;
			}
		}
		
		if (reply) {
			String replyText = "由於您輸入的文字可能太長，或是含有系統無法接受的字詞，將以隨機賀詞代替～～";
			try {
				if (input.contains("\n")) {
					log.info("input contains newLine, replaceAll");
					input = input.replaceAll("\n", " ");
				}
				boolean validMsg = marqueeService.inputCheck(input);
				marqueeService.addMarquee(event.getSource().getUserId(), event.getMessage().getId(), input, validMsg);
				
				if (validMsg) 
					replyText = "已將以下文字加到螢幕上囉：" + input;
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				replyText = "由於您輸入的文字可能太長，或是含有系統無法接受的字詞，將以隨機賀詞代替～～";
			}
			this.replyText(event.getReplyToken(), replyText);
		}
	}
	
	public void handleImageMsg(MessageEvent<ImageMessageContent> event) {
		this.handleMediaMsg(event.getSource().getUserId(), event.getMessage().getId(), IMAGE_TYPE);
	}
	
	public void handleVideoMsg(MessageEvent<VideoMessageContent> event) {
		this.handleMediaMsg(event.getSource().getUserId(), event.getMessage().getId(), VIDEO_TYPE);
	}
	
	private void handleMediaMsg(String userId, String messageId, String type) {
		String userName = lineService.getLineUserProfile(userId).getDisplayName();
		Pic p = new Pic();
		p.setMessageId(messageId);
		p.setFromLine(true);
		p.setType(type);
		p.setIsValid(true); // 全部相信
		p.setUploadStatus(false);
		p.setCreateUserId(userId);
		p.setCreateUserName(userName);
		p.setCreateTime(new Date());
		picRepo.save(p);
		log.info(p.toString());
	}
	
	public void handleLocationMsg(MessageEvent<LocationMessageContent> event) {
		String inputAddr = event.getMessage().getAddress();
		double inputLatitude = event.getMessage().getLatitude();
		double inputLongitude = event.getMessage().getLongitude();
		log.info("https://www.google.com/maps/dir/{}/{}", inputAddr, weddingLocation);
		
		StringBuilder sb = new StringBuilder();
		sb.append("以下是您傳送地點至喜宴會場的路徑規劃～");
		sb.append(BR);
		sb.append("https://www.google.com/maps/dir/");
		sb.append(inputLatitude);
		sb.append(",");
		sb.append(inputLongitude);
		sb.append("/");
		sb.append(weddingLocation);
		log.info("sb.toString(): {}", sb.toString());
		this.replyText(event.getReplyToken(), sb.toString());
	}
	
	public void replyText(String replyToken, String message) {
		try {
			message = "{\"replyToken\":\"" + replyToken + "\",\"messages\":[{\"type\":\"text\",\"text\":\"" + message
					+ "\"}]}"; // 回傳的json格式訊息
			URL myurl = new URL(replyUrl); // 回傳的網址
			HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection(); // 使用HttpsURLConnection建立https連線
			con.setRequestMethod("POST");// 設定post方法
			con.setRequestProperty("Content-Type", "application/json; charset=" + CHARSET); // 設定Content-Type為json
			con.setRequestProperty("Authorization", "Bearer " + this.accessToken); // 設定Authorization
			con.setDoOutput(true);
			con.setDoInput(true);
			DataOutputStream output = new DataOutputStream(con.getOutputStream()); // 開啟HttpsURLConnection的連線
			output.write(message.getBytes(Charset.forName(CHARSET))); // 回傳訊息編碼為utf-8
			output.close();
			log.info("Resp Code: {}; Resp Message: {}",con.getResponseCode(), con.getResponseMessage()); // 顯示回傳的結果，若code為200代表回傳成功
		} catch (MalformedURLException e) {
			log.error("Message: {}", e.getMessage(), e);
		} catch (IOException e) {
			log.error("Message: {}", e.getMessage(), e);
		}
	}
	
	private void addButtonLog(String text, String userId) {
		LineProfile profile = lineService.getLineUserProfile(userId);
		
		Button b = new Button();
		b.setButtonText(text);
		b.setUserId(userId);
		b.setUserName(profile.getDisplayName());
		b.setUserImage(profile.getPictureUrl());
		b.setClickTime(new Date());
		buttonRepo.save(b);
	}
	
}
