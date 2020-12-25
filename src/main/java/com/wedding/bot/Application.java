package com.wedding.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.ImageMessageContent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.message.VideoMessageContent;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import com.wedding.bot.service.MessageService;

@SpringBootApplication
@LineMessageHandler
@EnableScheduling
public class Application {
	
	@Autowired
	private MessageService service;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@EventMapping
	public void handleDefaultMessageEvent(Event event) {
		service.handleDefaultMsg(event);
	}
	
	@EventMapping
	public void handleTextMessageEvent(MessageEvent<TextMessageContent> event) {
		service.handleTextMsg(event);
	}

	@EventMapping
	public void handleImageMessageEvent(MessageEvent<ImageMessageContent> event) {
		service.handleImageMsg(event);
	}
	
	@EventMapping
	public void handleVideoMessageEvent(MessageEvent<VideoMessageContent> event) {
		service.handleVideoMsg(event);
	}
	
	@EventMapping
	public void handleLocationMessageEvent(MessageEvent<LocationMessageContent> event) {
		service.handleLocationMsg(event);
	}

}
