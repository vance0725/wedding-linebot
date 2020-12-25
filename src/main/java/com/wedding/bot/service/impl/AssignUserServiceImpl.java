package com.wedding.bot.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wedding.bot.model.AssignedUser;
import com.wedding.bot.repo.AssignedUserRepo;
import com.wedding.bot.repo.PicRepo;
import com.wedding.bot.service.AssignUserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("AssignUserService")
public class AssignUserServiceImpl implements AssignUserService {
	
	@Autowired
	private AssignedUserRepo assignedUserRepo;
	@Autowired
	private PicRepo picRepo;

	public List<AssignedUser> getAllUsers() {
		List<AssignedUser> users = assignedUserRepo.findAll(); // 先前已選過的user
		List<Object[]> usersFromPic = picRepo.findAllCreators(); // 全部user
		
		for (Object[] obj : usersFromPic) {
			String username = obj[0].toString();
			AssignedUser user = users.stream().filter(o -> o.getUsername().equals(username)).findFirst().orElse(null);
			if (user == null) {
				user = new AssignedUser();
				user.setAuth(false);
				user.setUsername(username);
				users.add(user);
			}
		}
		
		return users;
	}
	
	public void saveCheckedUsers(List<String> validUsers) {
		List<AssignedUser> allUsers = getAllUsers();
		for (AssignedUser user : allUsers) {
			boolean isValidUser = false;
			for (String validUser : validUsers) {
				if (user.getUsername().equals(validUser)) {
					isValidUser = true;
					log.info("valid user: {}", validUser);
					break;
				}
			}
			
			user.setAuth(isValidUser);
			user.setUpdateTime(new Date());
			assignedUserRepo.save(user);
		}
	}
	
	public List<AssignedUser> getValidUsers() {
		return assignedUserRepo.findAllByIsAuth(true);
	}
	
	public boolean unAssignUser(String userName) {
		AssignedUser assignedUser = assignedUserRepo.findAll().stream().filter(o -> o.getUsername().equals(userName)).findFirst().orElse(null);
		if (assignedUser != null) {
			assignedUser.setAuth(false);
			assignedUser.setUpdateTime(new Date());
			assignedUserRepo.save(assignedUser);
			
			return true;
		} else {
			log.error("undefined user! input userName: {}", userName);
			return false;
		}
	}

}
