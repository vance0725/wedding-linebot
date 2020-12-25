package com.wedding.bot.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "button")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Button {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ai", nullable = false)
	private int ai;
	
	@Column(name = "button_text", nullable = true)
	private String buttonText;
	
	@Column(name = "user_id", nullable = false)
	private String userId;
	
	@Column(name = "user_name", nullable = true)
	private String userName;
	
	@Expose
	@Column(name = "user_image", nullable = true)
	private String userImage;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "click_time", nullable = false)
	private Date clickTime;
	

}
