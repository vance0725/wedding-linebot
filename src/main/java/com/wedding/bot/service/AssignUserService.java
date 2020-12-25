package com.wedding.bot.service;

import java.util.List;

import com.wedding.bot.model.AssignedUser;

public interface AssignUserService {
	
	public List<AssignedUser> getAllUsers();
	public void saveCheckedUsers(List<String> validUsers);
	public List<AssignedUser> getValidUsers();
	public boolean unAssignUser(String userName);

}
