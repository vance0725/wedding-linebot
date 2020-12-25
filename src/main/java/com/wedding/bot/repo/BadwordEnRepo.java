package com.wedding.bot.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wedding.bot.model.BadwordEn;

@Repository
public interface BadwordEnRepo extends JpaRepository<BadwordEn, Integer> {
	
}
