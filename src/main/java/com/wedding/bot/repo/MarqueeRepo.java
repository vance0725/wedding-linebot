package com.wedding.bot.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wedding.bot.model.Marquee;

@Repository
public interface MarqueeRepo extends JpaRepository<Marquee, Integer> {
	
	List<Marquee> findAllByIsPriority(boolean isPriority);

}
