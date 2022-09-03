package com.jsmart.yoda.shortener.base.service;

import java.util.List;

import com.jsmart.yoda.shortener.base.Url;

/**
 * @author Nick Koretskyy
 *
 */
public interface ShortnerService {

	public Url get(String shortUrl);

	public List<Url> getAll();

	public Url create(String longUrl);

	public Url update(String shortUrl, String longUrl);

	public boolean remove(String shortUrl);

	public boolean removeAll();

}
