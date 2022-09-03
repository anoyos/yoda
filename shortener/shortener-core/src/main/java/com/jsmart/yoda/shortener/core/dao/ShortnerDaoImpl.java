package com.jsmart.yoda.shortener.core.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.jsmart.yoda.shortener.base.Url;
import com.jsmart.yoda.shortener.base.dao.ShortnerDao;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Nick Koretskyy
 *
 *         This class implements Data Access Object interface to simplify Redis
 *         accesses.
 */
@Repository
public class ShortnerDaoImpl implements ShortnerDao {

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	/**
	 * This method fetches stored Url - base on key (short url) from redis base.
	 *
	 * @param shortUrl
	 *            Unique string with short url to get.
	 *
	 * @see com.jsmart.yoda.shortener.base.Url.
	 *
	 * @return stored Url or null if not found.
	 */
	@Override
	public Url get(String shortUrl) {
		String longUrl = redisTemplate.opsForValue().get(shortUrl);
		if (Optional.ofNullable(longUrl).isPresent()) {
			return new Url(shortUrl, longUrl);
		}
		return null;
	}

	/**
	 * This method fetches all stored Urls in redis base.
	 *
	 * @see com.jsmart.yoda.shortener.base.Url.
	 *
	 * @return list of stored Urls.
	 */
	@Override
	public List<Url> getAll() {// @formatter:off
		Set<String> keys = redisTemplate.keys("*");
		List<Url> urls = keys.stream()
				.map((key) -> new Url(key, get(key).getLongUrl()))
				.collect(Collectors.toList());
		return urls;
	}// @formatter:on

	/**
	 * This method creates new key (short url) - value (long url) record in redis
	 * base on new Url fields.
	 *
	 * @param com.jsmart.yoda.shortener.base.Url
	 *            Instance of Url.
	 *
	 * @see com.jsmart.yoda.shortener.base.Url.
	 *
	 * @return boolean, in success case - when record created return true else
	 *         false.
	 */
	@Override
	public boolean create(Url url) {
		return redisTemplate.opsForValue().setIfAbsent(url.getShortUrl(), url.getLongUrl());
	}

	/**
	 * This method updates stored Url - key (short url) / value (long url) record
	 * in redis base on Url fields.
	 *
	 * @param shortUrl
	 *            Unique string with short url to update.
	 *
	 * @param longUrl
	 *            Unique string with new long url to update.
	 *
	 * @see com.jsmart.yoda.shortener.base.Url.
	 *
	 * @return updated Url.
	 */
	@Override
	public Url update(String shortUrl, String longUrl) {
		redisTemplate.opsForValue().set(shortUrl, longUrl);
		return get(shortUrl);
	}

	/**
	 * This method removes stored Url - key (short url) / value (long url) record
	 * in redis base on Url fields.
	 *
	 * @param shortUrl
	 *            Unique string with short url to delete.
	 */
	@Override
	public void remove(String shortUrl) {
		redisTemplate.delete(shortUrl);
	}

	/**
	 * This method removes stored Urls in Redis base on getAll list.
	 *
	 * @return boolean, in successes case - when all records removed true else
	 *         false.
	 */
	@Override
	public boolean removeAll() {
		List<Url> urls = this.getAll();
		if (!urls.isEmpty()) {
			urls.forEach((url) -> redisTemplate.delete(url.getShortUrl()));
			return true;
		}
		return false;
	}

}
