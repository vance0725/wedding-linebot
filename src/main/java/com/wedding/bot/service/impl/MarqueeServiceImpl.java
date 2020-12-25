package com.wedding.bot.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.wedding.bot.model.LineProfile;
import com.wedding.bot.model.Marquee;
import com.wedding.bot.repo.MarqueeRepo;
import com.wedding.bot.service.LineService;
import com.wedding.bot.service.MarqueeService;

import lombok.extern.slf4j.Slf4j;

@Service("MarqueeService")
@Slf4j
public class MarqueeServiceImpl implements MarqueeService {
	
	@Value("${textfile.path}")
	private String textFilePath;
	@Value("${marquee.message.length}")
	private int marqueeLength;
	@Value("${marquee.default.icon}")
	private String marqueeDefaultIcon;
	@Autowired
	private LineService lineService;
	@Autowired
	private MarqueeRepo marqueeRepo;
	
	public void addMarquee(String userId, String messageId, String message, boolean isValid) {
		LineProfile lineProfile = lineService.getLineUserProfile(userId);
		
		Marquee marquee = new Marquee();
		marquee.setMessageId(messageId);
		marquee.setMessage(message);
		marquee.setIsValid(isValid);
		marquee.setFromLine(true);
		marquee.setPriority(true);
		marquee.setCreateUserId(lineProfile.getUserId());
		marquee.setCreateUserName(lineProfile.getDisplayName());
		marquee.setCreateUserImage(StringUtils.isEmpty(lineProfile.getPictureUrl()) ? marqueeDefaultIcon : lineProfile.getPictureUrl());
		marquee.setCreateTime(new Date());
		marqueeRepo.save(marquee);
	}
	
	public Marquee getMarquee() {
		// 先抓還沒露出過的，如果超過1個就抓第一個
		List<Marquee> yetShow = marqueeRepo.findAllByIsPriority(true);
		if (yetShow.size() > 0) {
			Marquee m = yetShow.get(0);
			if (!m.getIsValid()) // 隨機抓賀詞
				m.setMessage(getDefaultMessage());
			m.setPriority(false);
			marqueeRepo.save(m); // 更新已優先露出的狀態
			
			return m;
		} else { // 有出現過的，只從合法的文字中，隨便抓一個出來玩
			List<Marquee> shown = marqueeRepo.findAllByIsPriority(false);
			return shown.get((int)(Math.random() * shown.size()));
		}
	}
	
	public List<Marquee> getLotteryMarquees(Integer size) {
		List<Marquee> marqueeList = marqueeRepo.findAll().stream().filter(o -> o.getIsValid()).filter(o -> o.isFromLine()).collect(Collectors.toList());
		Collections.shuffle(marqueeList);
		List<Marquee> list = new ArrayList<>();
		if (size != null)
			size = size <= marqueeList.size() ? size : marqueeList.size();
		else 
			size = marqueeList.size();
		
		Set<String> userSet = new HashSet<>(); // 已在回傳list內的user
		int count = 0; // 目前找的位置
		while (list.size() < size) {
			if (count >= marqueeList.size()) {
				log.debug("count: {}, marqueeList.size(): {}, list.size(): {}", count, marqueeList.size(), list.size());
				break;
			}
			
			Marquee m = marqueeList.get(count++);
			if (!userSet.contains(m.getCreateUserName())) {
				userSet.add(m.getCreateUserName());
				list.add(m);
			}
		}
		
		return list;
	}
	
	public boolean inputCheck(String input) {
		if (input.length() > marqueeLength) {
			log.info("exceed message length! length: " + input.length());
			return false;
		} else if (input.matches(".*?\\(.*?\\).*?")) { // 文字+line表情貼
			log.info("found line sticker! input: ", input);
			return false;
		}
		
		try {
			List<String> chList = Files.lines(Paths.get(textFilePath + "badwords_zh_TW.txt")).collect(Collectors.toList());
			List<String> enList = Files.lines(Paths.get(textFilePath + "badwords_en_US.txt")).collect(Collectors.toList());
			
			boolean isValid = true;
			log.info("doing input: {}", input);
			for (String ch : chList) {
				if (input.contains(ch)) {
					log.info("found CH bad word!!!, input: {}", input);
					isValid = false;
					break;
				}
			}
			
			if (isValid) {
				for (String en : enList) {
					String filtered = input.replaceAll("[^ \\w]", "").toLowerCase();
					en = en.toLowerCase();
					if (filtered.contains(en)) {
						if (filtered.startsWith(en) || filtered.endsWith(en)) {
							log.info("found EN bad word!!!, input: {}", input);
							isValid = false;
							break;
						} else {
							String[] arr = filtered.split(" ");
							for (String str : arr) {
								if (filtered.equals(str)) {
									log.info("found EN bad word!!!, input: {}", input);
									isValid = false;
									break;
								}
							}
						}
					}
				}
			}
			return isValid;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			return false;
		}
	}
	
	private String getDefaultMessage() {
		try {
			List<String> messages = Files.lines(Paths.get(textFilePath + "defaultMessage.txt")).collect(Collectors.toList());
			return messages.get((int)(Math.random() * messages.size()));
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			return "恭喜老爺～賀喜夫人❤️";
		}
	}

}
