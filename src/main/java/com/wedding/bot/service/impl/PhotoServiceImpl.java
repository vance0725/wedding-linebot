package com.wedding.bot.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.wedding.bot.NoLogging;
import com.wedding.bot.model.AssignedUser;
import com.wedding.bot.model.Pic;
import com.wedding.bot.repo.PicRepo;
import com.wedding.bot.service.AssignUserService;
import com.wedding.bot.service.GoogleService;
import com.wedding.bot.service.LineService;
import com.wedding.bot.service.PhotoService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("PhotoService")
public class PhotoServiceImpl implements PhotoService {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");

	@Value("${photo.original.path}")
	private String originalPath;
	@Value("${photo.compressed.path}")
	private String compressedPath;
	@Value("${photo.quality}")
	private Double photoQuality;
	@Value("${photo.public.url}")
	private String publicUrl;
	@Value("${photo.batch.size}")
	private int batchSize;
	@Autowired
	private PicRepo picRepo;
	@Autowired
	private LineService lineService;
	@Autowired
	private GoogleService googleService;
	@Autowired
	private AssignUserService assignUserService;

	public int uploadFile(List<Pic> picList, String batchStr) {
		int count = 0;
		if (picList.size() > 0) {
			for (Pic p : picList) {

				p = setPic(p, batchStr);
				
				String errorMsg = null;
				try {
					InputStream in = lineService.getLineContent(p.getMessageId());
					if (in != null) {
						if (p.isImg()) {
							// do upload to google photo (photos only)
							p = googleService.uploadMedia(batchStr, p, in);
							in.close();
						} else // 影片未上傳至google photo, 直接設為true
							p.setUploadStatus(true);
						
						// save photo or video to VM
						InputStream in1 = lineService.getLineContent(p.getMessageId());
						processPhoto(p, in1);
						in1.close();
					} else {
						errorMsg = "InputStream from line returns null";
					}
				} catch (Exception e) {
					log.error(e.getMessage(), e);
					errorMsg = e.getMessage();
				}
				
				if (errorMsg != null) {
					p.setUploadStatus(false);
					p.setErrorMsg(errorMsg);
				}

				picRepo.save(p);
				log.info(p.toString());

				if (++count >= batchSize)
					break;
			}
		}

		return count;
	}

	public void processPhoto(Pic p, InputStream in) throws IOException {
		// 存原始檔案
		FileUtils.writeByteArrayToFile(new File(originalPath + p.getFileName()), IOUtils.toByteArray(in));
		
		if (p.isImg()) { // 只有圖片需要壓縮
			compressImage(originalPath + p.getFileName(), compressedPath + p.getFileName(), photoQuality);
		}
	}
	
	public Pic getShowImage() {
		String type = "image"; // 只抓圖片
		Pic pic = null;
		// 先抓還沒露出過的，如果超過1個就抓第一個
		List<Pic> priorityList = picRepo.finaAllByIsPriorityAndType(true, type);
		if (priorityList.size() > 0) {
			pic = priorityList.get(0);
			pic.setIsPriority(false);
			pic.setPublicUrl(publicUrl + pic.getFileName());
			picRepo.save(pic); // 更新已優先露出的狀態
		} else { // 有出現過的，只從合法的圖片中，隨便抓一個出來展示
			List<Pic> shown = picRepo.findAllByIsValidAndUploadStatusAndIsPriorityAndType(true, true, false, type);
			pic = shown.get((int)(Math.random() * shown.size()));
			pic.setPublicUrl(publicUrl + pic.getFileName());
		}
		return pic;
	}
	
	@NoLogging
	public List<Pic> getLotteryImages(Integer size) {
		String type = "image"; // 只抓圖片
		List<Pic> allImages = picRepo.findAllByIsValidAndUploadStatus(true, true).stream().filter(o -> o.getType().equals(type)).filter(o -> o.isFromLine()).collect(Collectors.toList());
		Collections.shuffle(allImages);
		List<Pic> returnList = new ArrayList<>();
		if (size != null)
			size = size <= allImages.size() ? size : allImages.size();
		else 
			size = allImages.size();
		
		// 取得內定名單
		List<AssignedUser> assignedUsers = assignUserService.getValidUsers();
		
		Set<String> userSet = new HashSet<>(); // 已在回傳list內的user
		int count = 0; // 目前找的位置
		while (returnList.size() < size) {
			if (count >= allImages.size()) {
				log.debug("count: {}, allImages.size(): {}, returnList.size(): {}", count, allImages.size(), returnList.size());
				break;
			}
			
			Pic p = allImages.get(count++);
			// 尚未在回傳list中 && 有在內定名單
			if (!userSet.contains(p.getCreateUserName()) && assignedUsers.stream().filter(o -> o.getUsername().equals(p.getCreateUserName())).findFirst().orElse(null) != null) {
				userSet.add(p.getCreateUserName());
				p.setPublicUrl(publicUrl + p.getFileName());
				returnList.add(p);
			}
		}
		
		return returnList;
	}

	/**
	 * 壓縮圖片
	 * 
	 * @param srcImagePath
	 * @param newImagePath
	 * @param quality
	 * @return
	 */
	private boolean compressImage(String srcImagePath, String newImagePath, Double quality) {
		try {
			// original command
//			convert -quality 80 -resize x1080 original/test-0257.jpg test-0257-c.jpg
			
			// 組合command
			StringBuilder sb = new StringBuilder();
			sb.append(quality);
			sb.append(" ");
			sb.append("x1080");
			sb.append(" ");
			sb.append(srcImagePath);
			sb.append(" ");
			sb.append(newImagePath);
			log.debug("args: {}", sb.toString());
			Process proc = Runtime.getRuntime().exec("./compressPhoto.sh " + sb.toString());
			proc.waitFor();
			proc.getInputStream();
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String s;
			while ((s = stdInput.readLine()) != null) {
				log.info(s);
			}
			
			proc.destroy();

			log.info("image compressed successfully!");
			return true;
		} catch (Exception e) {
			log.info(e.getMessage(), e);
			return false;
		}
	}

	private Pic setPic(Pic p, String batchStr) {
		String dateTime = sdf.format(new Date());
		String fileName = p.getType() + "_" + dateTime + "_" + rand(100, 1000);

		if (p.getType().equals("image")) {
			fileName += ".jpg";
			p.setImg(true);
		} else {
			fileName += ".mp4";
			p.setImg(false);
		}
		p.setFileName(fileName);

		String userName = lineService.getLineUserProfile(p.getCreateUserId()).getDisplayName();
		log.info("fileName: {}, userName: {}", fileName, userName);
		p.setIsPriority(true); // 優先顯示
		p.setBatch(batchStr);
		p.setUpdateTime(new Date());

		return p;
	}

	private int rand(int min, int size) {
		int result = 0;
		do {
			result = (int) (Math.random() * size);
		} while (result < min);

		return result;
	}

}
