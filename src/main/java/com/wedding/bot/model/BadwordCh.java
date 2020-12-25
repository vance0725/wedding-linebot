package com.wedding.bot.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "badword_ch")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BadwordCh {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ai", nullable = false)
	private int ai;
	
	@Column(name = "word", nullable = false)
	private String word;
	
}
