package com.jsmart.yoda.users.base;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.util.Date;

/**
 * @author Sergey Khomich
 *
 */
@Data
public class UrlInfo {

	private String shortUrl;
	private Date createdAt;
	private Date modifiedAt;
	@JsonSerialize(using = BySerializer.class)
	private User createdBy;
	@JsonSerialize(using = BySerializer.class)
	private User modifiedBy;
	private String longUrl;
}
