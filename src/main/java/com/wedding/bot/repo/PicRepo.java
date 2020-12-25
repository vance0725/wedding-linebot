package com.wedding.bot.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.wedding.bot.model.Pic;

@Repository
public interface PicRepo extends JpaRepository<Pic, Integer> {
	
	List<Pic> findAllByIsValid(Boolean isValid);
	
	@Query(value = "select * from pic where is_valid = ?1 and upload_status = ?2 ;", nativeQuery = true)
	List<Pic> findAllByIsValidAndUploadStatus(Boolean isValid, boolean uploadStatus);
	
	@Query(value = "select * from pic where is_priority = ?1 and type = ?2 ;", nativeQuery = true)
	public List<Pic> finaAllByIsPriorityAndType(Boolean isPriority, String type);
	
	@Query(value = "select * from pic where is_valid = ?1 and upload_status = ?2 and is_priority = ?3 and type = ?4 ;", nativeQuery = true)
	public List<Pic> findAllByIsValidAndUploadStatusAndIsPriorityAndType(Boolean isValid, boolean uploadStatus, Boolean isPriority, String type);

	@Query(value = "SELECT create_user_name from pic group by create_user_name ;", nativeQuery = true)
	public List<Object[]> findAllCreators();
}
