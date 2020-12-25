package com.wedding.bot.service;

import java.util.List;

import com.wedding.bot.model.Marquee;

public interface MarqueeService {
	
	public void addMarquee(String userId, String messageId, String message, boolean isValid);
	public Marquee getMarquee();
	public List<Marquee> getLotteryMarquees(Integer size);
	public boolean inputCheck(String input);

}
