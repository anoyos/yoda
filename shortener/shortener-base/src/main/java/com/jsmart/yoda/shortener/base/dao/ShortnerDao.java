package com.jsmart.yoda.shortener.base.dao;

import java.util.List;

import com.jsmart.yoda.shortener.base.Url;

/**
 * @author Nick Koretskyy
 *
 */
public interface ShortnerDao {

	public Url get(String shortUrl);

	public List<Url> getAll();

	public boolean create(Url url);

	public Url update(String shortUrl, String longUrl);

	public void remove(String shortUrl);

	public boolean removeAll();

}
