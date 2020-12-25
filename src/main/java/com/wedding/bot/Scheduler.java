package com.wedding.bot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.wedding.bot.model.Pic;
import com.wedding.bot.repo.PicRepo;
import com.wedding.bot.service.PhotoService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Scheduler {
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
	@Autowired
	private PicRepo picRepo;
	@Autowired
	private PhotoService photoService;
	
	@Scheduled(fixedRateString = "${google.upload.frequency}")
	public void uploadJob() {
		log.info("Scheduler uploadJob START");
		long sTime = System.currentTimeMillis();
		String batchStr = "batch_" + sdf.format(new Date());
		
		// 只處理isValid=true & status=false & batch is null 的筆數
		List<Pic> tmpPicList = picRepo.findAllByIsValidAndUploadStatus(true, false);
		List<Pic> picList = tmpPicList.stream().filter(p -> StringUtils.isEmpty(p.getBatch())).collect(Collectors.toList());
		log.info("size of picList: {}", picList.size());
		
		int count = photoService.uploadFile(picList, batchStr);
		
		long eTime = System.currentTimeMillis();
		log.info("Scheduler uploadJob END, processed {} files in {} ms", count, (eTime - sTime));
	}
	
	@Scheduled(fixedDelayString = "${google.upload.rerunFrequency}")
	public void rerunUploadJob() {
		log.info("Scheduler rerunUploadJob START");
		long sTime = System.currentTimeMillis();
		String batchStr = "batch_" + sdf.format(new Date());
		
		// 只處理isValid=true & uploadStatus=false & errorMsg is null 的筆數
		List<Pic> tmpPicList = picRepo.findAllByIsValidAndUploadStatus(true, false);
		List<Pic> picList = tmpPicList.stream().filter(p -> StringUtils.isEmpty(p.getErrorMsg())).collect(Collectors.toList());
		log.info("size of picList: {}", picList.size());
		
		int count = photoService.uploadFile(picList, batchStr);
		
		long eTime = System.currentTimeMillis();
		log.info("Scheduler rerunUploadJob END, processed {} files in {} ms", count, (eTime - sTime));
	}
	
}
