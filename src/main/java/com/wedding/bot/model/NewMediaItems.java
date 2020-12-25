package com.wedding.bot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewMediaItems {
	
	private String description;
	private SimpleMediaItem simpleMediaItem;

}
