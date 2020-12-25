package com.wedding.bot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaItem {
	
	private String albumId;
	private NewMediaItems[] newMediaItems;

}
