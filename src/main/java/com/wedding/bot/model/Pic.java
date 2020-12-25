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
import javax.persistence.Transient;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pic")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pic {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Expose
	@Column(name = "ai", nullable = false)
	private int ai;
	
	@Column(name = "message_id", nullable = false)
	private String messageId;
	
	@Column(name = "type", nullable = false)
	private String type;
	
	@Column(name = "file_name", nullable = true)
	private String fileName;
	
	@Column(name = "is_from_line", nullable = false)
	private boolean isFromLine;
	
	@Column(name="is_valid", nullable = true)
	private Boolean isValid;
	
	@Column(name="is_priority", nullable = true)
	private Boolean isPriority;
	
	@Column(name = "upload_status", nullable = false)
	private boolean uploadStatus;
	
	@Column(name = "media_item_id", nullable = true)
	private String mediaItemId;
	
	@Column(name = "base_url", nullable = true)
	private String baseUrl;
	
	@Column(name = "batch", nullable = true)
	private String batch;
	
	@Column(name = "error_msg", nullable = true)
	private String errorMsg;
	
	@Column(name = "create_user_id", nullable = true)
	private String createUserId;
	
	@Expose
	@Column(name = "create_user_name", nullable = true)
	private String createUserName;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time", nullable = false)
	private Date createTime;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "update_time", nullable = true)
	private Date updateTime;
	
	@Transient
	private boolean isImg;
	
	@Expose
	@Transient
	private String publicUrl;
	
}
