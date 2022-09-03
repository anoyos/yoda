package com.jsmart.yoda.shortener.core.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jsmart.yoda.shortener.base.Url;
import com.jsmart.yoda.shortener.base.dao.ShortnerDao;
import com.jsmart.yoda.shortener.base.service.ShortnerService;
import com.jsmart.yoda.shortener.core.model.ShortUrlGeneratorSettings;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.validator.routines.UrlValidator;

/**
 * @author Nick Koretskyy
 * 
 *         This class covers Url operations routine.
 */
@Service
public class ShortnerServiceImpl implements ShortnerService {

	@Autowired
	private ShortnerDao urlShortnerDao;

	@Autowired
	private ShortUrlGeneratorSettings shortUrlGeneratorSettings;

	private static final Logger log = Logger.getLogger(ShortnerServiceImpl.class);

	public ShortnerDao getUrlShortnerDao() {
		return urlShortnerDao;
	}

	public void setUrlShortnerDao(ShortnerDao urlShortnerDao) {
		this.urlShortnerDao = urlShortnerDao;
	}

	public void setShortUrlGeneratorConfig(ShortUrlGeneratorSettings shortUrlGeneratorConfig) {
		this.shortUrlGeneratorSettings = shortUrlGeneratorConfig;
	}

	/**
	 * This method fetches stored Url - base on key (short url) from DAO.
	 * 
	 * @param shortUrl
	 *            Unique short letter string.
	 * 
	 * @see com.jsmart.yoda.shortener.base.Url.
	 * 
	 * @return stored Url or null if not found.
	 */
	@Override
	public Url get(String shortUrl) {
		log.info("Fetching Url with shortUrl " + shortUrl);
		return urlShortnerDao.get(shortUrl);
	}

	/**
	 * This method fetches all stored Urls.
	 * 
	 * @see com.jsmart.yoda.shortener.base.Url.
	 * 
	 * @return list of stored Urls.
	 */
	@Override
	public List<Url> getAll() {
		log.info("Fetching list of all Urls");
		return urlShortnerDao.getAll();
	}

	/**
	 * This method creates new instance of Url for new long url with unique key.
	 * 
	 * @param longUrl
	 *            String url representation.
	 * 
	 * @see com.jsmart.yoda.shortener.base.Url.
	 * 
	 * @return new instance of Url.
	 */
	@Override
	public Url create(String longUrl) {

		log.info("Creating Url for " + longUrl);
		
		longUrl = fixLongUrl(longUrl);

		Url url = new Url();
		url.setLongUrl(longUrl);

		String uuid = UUID.randomUUID().toString();
		url.setShortUrl(uuid.substring(1, shortUrlGeneratorSettings.getShortUrlLenght() + 1));

		if (urlShortnerDao.create(url)) {
			log.info("Url created");
		} else {
			url = null;
			log.info("Url create fail");
		}
		return url;
	}

	/**
	 * This method updates stored Url - base on key (short url) value through
	 * DAO.
	 * 
	 * @param shortUrl
	 *            Key to be updated.
	 * 
	 * @param longUrl
	 *            Value to be updated.
	 * 
	 * @see com.jsmart.yoda.shortener.base.Url.
	 * 
	 * @return updated Url or null if not found
	 */
	@Override
	public Url update(String shortUrl, String longUrl) {

		log.info("Fetching Url with shortUrl " + shortUrl);
		
		Url url = urlShortnerDao.get(shortUrl);
		if (Optional.ofNullable(url).isPresent()) {
			longUrl = fixLongUrl(longUrl);
			Url updatedUrl = urlShortnerDao.update(shortUrl, longUrl);
			log.info("Url with shortUrl " + shortUrl + " updated");
			return updatedUrl;
		} else {
			log.info("Unable to update. Url with shortUrl " + shortUrl + " not found");
			return null;
		}
	}

	/**
	 * This method removes stored Url trough DAO.
	 * 
	 * @param shortUrl
	 *            Unique string with short url to delete.
	 * 
	 * @return boolean true in successes case or false, if url wasn't found or
	 *         DAO layer unable to delete.
	 */
	@Override
	public boolean remove(String shortUrl) {

		log.info("Fetching and deleting Url with shortUrl " + shortUrl);

		Url url = urlShortnerDao.get(shortUrl);
		if (Optional.ofNullable(url).isPresent()) {
			urlShortnerDao.remove(shortUrl);
			log.info("Url removed");
			return true;
		} else {
			log.info("Unable to delete. Url with shortUrl " + shortUrl + " not found");
			return false;
		}
	}

	/**
	 * This method removes all stored Urls.
	 * 
	 * @return true in successes case or false if database is empty.
	 */
	@Override
	public boolean removeAll() {
		log.info("Removing all Urls");
		if (urlShortnerDao.removeAll()) {
			log.info("All Urls removed");
			return true;
		} else {
			log.info("Unable to delete. Something went wrong");
			return false;
		}
	}

	/**
	 * This method validates long url. Long url should consists http or https
	 * part for correct redirect.
	 * 
	 * @param longUrl
	 *            Url string representation.
	 * 
	 * @return longUrl validated and fixed string with url.
	 */
	public String fixLongUrl(String longUrl) {

		String[] schemes = {"http", "https"};
		UrlValidator urlValidator = new UrlValidator(schemes);

		if (!(urlValidator.isValid(longUrl))) {
			log.info("Incoming Url " + longUrl + " is invalid and will concat with http://");
			longUrl = "http://".concat(longUrl);
		}
		return longUrl;
	}

}
