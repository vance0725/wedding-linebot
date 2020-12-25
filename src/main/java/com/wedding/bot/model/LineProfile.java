package com.wedding.bot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LineProfile {
	
	private String userId;
	private String displayName;
	private String pictureUrl;

}
