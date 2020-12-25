package com.wedding.bot.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.wedding.bot.model.MediaItem;
import com.wedding.bot.model.NewMediaItems;
import com.wedding.bot.model.Pic;
import com.wedding.bot.model.SimpleMediaItem;
import com.wedding.bot.repo.PicRepo;
import com.wedding.bot.service.GoogleService;

import lombok.extern.slf4j.Slf4j;

@Service("GoogleService")
@Slf4j
public class GoogleServiceImpl implements GoogleService {

	private static final String GRANT_TYPE = "refresh_token";

	@Value("${google.oauth.api}")
	private String oauthApi;
	@Value("${google.photo.clientId}")
	private String clientId;
	@Value("${google.photo.clientSecret}")
	private String clientSecret;
	@Value("${google.photo.refreshToken}")
	private String refreshToken;
	@Value("${google.photo.upload.api}")
	private String photoUploadApi;
	@Value("${google.album.id}")
	private String albumId;
	@Value("${google.photo.mediaItem.api}")
	private String mediaItemApi;
	@Value("${google.photo.mediaItem.detail.api}")
	private String mediaItemDetailApi;

	@Autowired
	private PicRepo picRepo;

	public String refreshAccessToken() {
		String accessToken = null;

		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			HttpPost httpPost = new HttpPost(oauthApi);
			httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair("client_id", clientId));
			params.add(new BasicNameValuePair("client_secret", clientSecret));
			params.add(new BasicNameValuePair("grant_type", GRANT_TYPE));
			params.add(new BasicNameValuePair("refresh_token", refreshToken));
			httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			HttpResponse response = httpClient.execute(httpPost);
			String jsonStr = showOutput(response);

			JSONObject jsonObj = new JSONObject(jsonStr);
			if (response.getStatusLine().getStatusCode() == 200) {
				accessToken = jsonObj.get("access_token").toString();
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

		return accessToken;
	}

	public String uploadingBytes(String accessToken, InputStream in, boolean isImg) {
		log.info("uploadingBytes START");
		String uploadToken = null;

		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			HttpPost httpPost = new HttpPost(photoUploadApi);
			httpPost.setHeader("Authorization", ("Bearer " + accessToken));
			httpPost.setHeader("Content-Type", "application/octet-stream");
			if (isImg)
				httpPost.setHeader("X-Goog-Upload-Content-Type", "image/jpeg");
			else
				httpPost.setHeader("X-Goog-Upload-Content-Type", "video/mp4");
			httpPost.setHeader("X-Goog-Upload-Protocol", "raw");
			httpPost.setEntity(new ByteArrayEntity(IOUtils.toByteArray(in)));
			HttpResponse response = httpClient.execute(httpPost);
			uploadToken = showOutput(response);

			if (response.getStatusLine().getStatusCode() != 200)
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

		log.info("uploadingBytes END");
		return uploadToken;
	}

	public Map<String, Object> createMediaItems(String accessToken, String uploadToken, String fileName, String userName) {
		log.info("createMediaItems START");
		
		Map<String, Object> map = new HashMap<>();
		map.put("status", false); // 先放入預設值
		
		String description = "uploaded by [" + userName + "]";
		NewMediaItems[] n = { new NewMediaItems(description, new SimpleMediaItem(fileName, uploadToken)) };
		MediaItem mi = new MediaItem();
		mi.setAlbumId(albumId);
		mi.setNewMediaItems(n);
		log.debug("JSON: {}", new Gson().toJson(mi));

		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			HttpPost httpPost = new HttpPost(mediaItemApi);
			httpPost.setHeader("Content-Type", "application/json");
			httpPost.setHeader("Authorization", ("Bearer " + accessToken));
			httpPost.setEntity(new StringEntity(new Gson().toJson(mi), "UTF-8"));
			HttpResponse response = httpClient.execute(httpPost);
			String jsonStr = showOutput(response);

			JSONObject jsonObj = new JSONObject(jsonStr);
			if (response.getStatusLine().getStatusCode() == 200) {
				String message = new JSONObject(jsonObj.getJSONArray("newMediaItemResults").get(0).toString()).getJSONObject("status").get("message").toString();
				boolean status = (message.equals("Success") || message.equals("OK"));
				map.put("status", status);
				
				String mediaItemId = new JSONObject(jsonObj.getJSONArray("newMediaItemResults").get(0).toString()).getJSONObject("mediaItem").getString("id");
				map.put("mediaItemId", mediaItemId);
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

		log.info("createMediaItems END");
		return map;
	}

	public Pic uploadMedia(String batchStr, Pic p, InputStream in) {
		String accessToken = refreshAccessToken();
		log.info("new accessToken: {}", accessToken);
		
		String uploadToken = uploadingBytes(accessToken, in, p.isImg());
		Map<String, Object> mediaItemsMap = createMediaItems(accessToken, uploadToken, p.getFileName(), p.getCreateUserName());
		p.setUploadStatus(Boolean.parseBoolean(mediaItemsMap.get("status").toString()));
		
		String baseUrl = getBaseUrl(accessToken, mediaItemsMap.get("mediaItemId").toString());
		p.setMediaItemId(mediaItemsMap.get("mediaItemId").toString());
		p.setBaseUrl(baseUrl);
		
		return p;
	}
	
	public List<Pic> refreshBaseUrl(String accessToken) {
		String type = "image";
		
		List<Pic> all = picRepo.findAll();
		List<Pic> refreshedPic = new ArrayList<>();
		for (Pic pic : all) {
			if (pic.getType().equals(type) && pic.getUpdateTime().before(minsBefore(30))) {
				pic.setBaseUrl(getBaseUrl(accessToken, pic.getMediaItemId()));
				pic.setUpdateTime(new Date());
				refreshedPic.add(pic);
			}
		}
		picRepo.saveAll(refreshedPic);
		
		return refreshedPic;
	}
	
	public String getBaseUrl(String accessToken, String mediaItemId) {
		String url = mediaItemDetailApi + mediaItemId;
		
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("Content-Type", "application/json");
			httpGet.setHeader("Authorization", ("Bearer " + accessToken));
			HttpResponse response = httpClient.execute(httpGet);
			
			if (response.getStatusLine().getStatusCode() == 200) {
				String jsonStr = showOutput(response);
				JSONObject jsonObj = new JSONObject(jsonStr);
				String baseUrl = jsonObj.getString("baseUrl");
				
				JSONObject mediaMetadata = jsonObj.getJSONObject("mediaMetadata");
				String mimeType = jsonObj.getString("mimeType");
				if (mimeType.startsWith("image")) {
					String widthStr = mediaMetadata.getString("width");
					String heightStr = mediaMetadata.getString("height");
					if (Integer.valueOf(widthStr) > Integer.valueOf(heightStr)) {
						baseUrl += "=w1920";
					} else {
						baseUrl += "=h1080";
					}
				}
				
				log.info(baseUrl);
				return baseUrl;
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
		
		return "";
	}
	
	private String showOutput(HttpResponse response) throws ParseException, IOException {
		String str = EntityUtils.toString(response.getEntity(), "UTF-8");
		log.debug("output: {}", str);
		return str;
	}

	private Date minsBefore(int mins) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, (mins * -1));
		return cal.getTime();
	}
	
}
