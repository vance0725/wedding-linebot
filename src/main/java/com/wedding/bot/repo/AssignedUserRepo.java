package com.wedding.bot.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wedding.bot.model.AssignedUser;

@Repository
public interface AssignedUserRepo extends JpaRepository<AssignedUser, Integer> {
	
	public List<AssignedUser> findAllByIsAuth(boolean isAuth);
	
}
