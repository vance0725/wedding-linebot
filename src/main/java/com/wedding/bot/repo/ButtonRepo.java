package com.wedding.bot.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wedding.bot.model.Button;

@Repository
public interface ButtonRepo extends JpaRepository<Button, Integer> {
	
}
