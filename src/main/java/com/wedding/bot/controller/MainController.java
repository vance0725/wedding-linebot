package com.wedding.bot.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.GsonBuilder;
import com.wedding.bot.model.AssignedUser;
import com.wedding.bot.model.Pic;
import com.wedding.bot.service.AssignUserService;
import com.wedding.bot.service.LineService;
import com.wedding.bot.service.MarqueeService;
import com.wedding.bot.service.PhotoService;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/")
@Slf4j
public class MainController {
	

	@Value("${line.bot.accessToken}")
	private String accessToken;
	@Value("${loading.url}")
	private String loadingUrl;
	@Autowired
	private LineService lineService;
	@Autowired
	private MarqueeService marqueeService;
	@Autowired
	private PhotoService photoService;
	@Autowired
	private AssignUserService assignUserService;
	@Autowired
	ResourceLoader resourceLoader;

	@GetMapping("")
	@ResponseBody
	public String index() {
		return "hi line";
	}
	
	@GetMapping("/loading")
	public String loading(Model model) {
		model.addAttribute("url", loadingUrl);
		return "loading";
	}
	
	@GetMapping(value = "/showpic/{messageId}", produces = MediaType.IMAGE_JPEG_VALUE)
	public void showPic(@PathVariable String messageId, HttpServletResponse response) {
		try {
			response.setContentType(MediaType.IMAGE_JPEG_VALUE);
			StreamUtils.copy(lineService.getLineContent(messageId), response.getOutputStream());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	@GetMapping("/select-pic")
	public String selectPic(Model model) {
		List<Pic> uncheckedPics = lineService.getUncheckedPics();
		model.addAttribute("list", uncheckedPics);
		model.addAttribute("actionUrl", "/select-pic/result");
		
		return "select";
	}
	
	@PostMapping("/select-pic/result")
	public String selectPicResult(@RequestParam("itemSelect[]") List<String> validPics, Model model) {
		lineService.saveCheckedPic(validPics);
		model.addAttribute("actionUrl", "/select-pic");
		return "success";
	}
	
	@GetMapping("/select-user")
	public String selectUser(Model model) {
		List<AssignedUser> users = assignUserService.getAllUsers();
		model.addAttribute("list", users);
		model.addAttribute("actionUrl", "/select-user/result");
		
		return "selectUser";
	}
	
	@PostMapping("/select-user/result")
	public String selectUserResult(@RequestParam("itemSelect[]") List<String> validUsers, Model model) {
		assignUserService.saveCheckedUsers(validUsers);
		model.addAttribute("actionUrl", "/select-user");
		return "success";
	}
	
	@GetMapping("/getMarquee")
	@ResponseBody
	public String getMarquee(HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(marqueeService.getMarquee());
	}
	
	@GetMapping(value = {"/getLotteryMarquees", "/getLotteryMarquees/{size}"})
	@ResponseBody
	public String getLotteryMarquees(@PathVariable(required = false) Integer size, HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(marqueeService.getLotteryMarquees(size));
	}
	
	@GetMapping("/getImage")
	@ResponseBody
	public String getImage(HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(photoService.getShowImage());
	}
	
	@GetMapping(value = {"/getLotteryImages/{size}", "/getLotteryImages"})
	@ResponseBody
	public String getLotteryImages(@PathVariable(required = false) Integer size, HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(photoService.getLotteryImages(size));
	}
	
	@CrossOrigin("*")
	@PutMapping(value = "/unAssignUser/{userName}")
	@ResponseBody
	public ResponseEntity<?> unAssignUser(@PathVariable String userName, HttpServletResponse response) {
		boolean result = assignUserService.unAssignUser(userName);
		log.info("unAssignUser result: {}", result);
		if (result)
			return new ResponseEntity<>(HttpStatus.OK);
		else
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}

}
