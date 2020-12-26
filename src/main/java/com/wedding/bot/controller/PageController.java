package com.wedding.bot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/page")
public class PageController {
	
	@GetMapping("/imageShow.html")
	public String imageShow() {
		return "pages/imageShow";
	}
	
	@GetMapping("/randMarquee.html")
	public String randMarquee() {
		return "pages/randMarquee";
	}
	
	@GetMapping("/randPic.html")
	public String randPic() {
		return "pages/randPic";
	}

}
