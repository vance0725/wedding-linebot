package com.wedding.bot.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.wedding.bot.model.LineProfile;
import com.wedding.bot.model.Pic;
import com.wedding.bot.repo.PicRepo;
import com.wedding.bot.service.LineService;

import lombok.extern.slf4j.Slf4j;

@Service("LineService")
@Slf4j
public class LineServiceImpl implements LineService {
	
	@Value("${line.bot.accessToken}")
	private String accessToken;
	@Value("${line.profile.api}")
	private String profileApi;
	@Autowired
	private PicRepo picRepo;
	
	public LineProfile getLineUserProfile(String userId) {
		LineProfile lineProfile = new LineProfile();
		
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			HttpGet httpGet = new HttpGet(profileApi + userId);
			httpGet.setHeader("Authorization", ("Bearer " + accessToken));
			HttpResponse response = httpClient.execute(httpGet);
			
			if (response.getStatusLine().getStatusCode() == 200) {
				String jsonStr = showOutput(response);
				lineProfile = new Gson().fromJson(jsonStr, LineProfile.class);
			} else
				throw new Exception("http status code: " + response.getStatusLine().getStatusCode());
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}
		
		return lineProfile;
	}
	
	public InputStream getLineContent(String messageId) throws Exception {
		String url = "https://api.line.me/v2/bot/message/" + messageId + "/content";
		
		InputStream is = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("Authorization", ("Bearer " + accessToken));
		try {
			HttpResponse response = httpClient.execute(httpGet);
			if (response.getStatusLine().getStatusCode() == 200) {
				is = response.getEntity().getContent();
				return is;
			} else
				throw new Exception("http status code: " + response.getStatusLine().getStatusCode());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		
		return is;
	}
	
	public List<Pic> getUncheckedPics() {
		int limition = 10; // 限制回傳筆數
		List<Pic> picList = picRepo.findAllByIsValid(null);
		
		return picList.stream().filter(o -> o.getType().equals("image")).limit(limition).collect(Collectors.toList());
	}
	
	public void saveCheckedPic(List<String> validPics) {
		List<Pic> uncheckedPics = getUncheckedPics();
		List<Pic> checkedPics = new ArrayList<>();
		for (Pic pic : uncheckedPics) {
			boolean isValidMessageId = false;
			for (String validMessageId : validPics) {
				if (pic.getMessageId().equals(validMessageId)) {
					log.info("valid messageId: {}", validMessageId);
					isValidMessageId = true;
					pic.setIsValid(true);
					break;
				}
			}
			
			if (!isValidMessageId) {
				log.info("invalid messageId: {}", pic.getMessageId());
				pic.setIsValid(false);
			}
			
			checkedPics.add(pic);
		}
		picRepo.saveAll(checkedPics);
	}
	
	private String showOutput(HttpResponse response) throws ParseException, IOException {
		String str = EntityUtils.toString(response.getEntity(), "UTF-8");
		log.debug("output: {}", str);
		return str;
	}

}
