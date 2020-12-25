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
@Table(name = "marquee")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Marquee {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Expose
	@Column(name = "ai", nullable = false)
	private int ai;
	
	@Column(name = "message_id", nullable = false)
	private String messageId;
	
	@Expose
	@Column(name = "message", nullable = false)
	private String message;
	
	@Column(name="is_valid", nullable = true)
	private Boolean isValid; // 是否合法
	
	@Column(name="is_from_line", nullable = false)
	private boolean isFromLine;
	
	@Column(name = "is_priority", nullable = false)
	private boolean isPriority; // 是否已經優先露出過
	
	@Column(name = "create_user_id", nullable = true)
	private String createUserId;
	
	@Expose
	@Column(name = "create_user_name", nullable = true)
	private String createUserName;
	
	@Expose
	@Column(name = "create_user_image", nullable = true)
	private String createUserImage;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time", nullable = false)
	private Date createTime;
	

}
