package com.wedding.bot.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wedding.bot.model.DefaultMessage;

@Repository
public interface DefaultMessageRepo extends JpaRepository<DefaultMessage, Integer> {
	
}
