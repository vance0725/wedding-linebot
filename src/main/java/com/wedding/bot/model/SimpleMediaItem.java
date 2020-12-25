package com.wedding.bot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleMediaItem {
	
	private String fileName;
	private String uploadToken;

}
