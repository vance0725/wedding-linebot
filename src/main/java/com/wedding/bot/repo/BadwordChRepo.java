package com.wedding.bot.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wedding.bot.model.BadwordCh;

@Repository
public interface BadwordChRepo extends JpaRepository<BadwordCh, Integer> {
	
}
