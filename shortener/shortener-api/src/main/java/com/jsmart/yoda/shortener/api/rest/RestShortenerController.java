package com.jsmart.yoda.shortener.api.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.UriComponentsBuilder;

import com.jsmart.yoda.shortener.api.ShortnerController;
import com.jsmart.yoda.shortener.base.Url;
import com.jsmart.yoda.shortener.base.service.ShortnerService;

/**
 * @author Nick Koretskyy
 *
 *         This class covers REST API for Url operations routine.
 */
@RestController
@RequestMapping(value = "/api")
public class RestShortenerController implements ShortnerController {

	@Autowired
	private ShortnerService urlShortnerService;

	/**
	 * This method creates JSON response on http GET /api/{shortUrl} request with
	 * short url in the path and return stored Url.
	 *
	 * @param shortUrl
	 *            String url representation.
	 *
	 * @return JSON HTTP ResponseEntity with stored Url in the body and
	 *         HttpStatus OK status in successes case. In other case return JSON
	 *         HTTP ResponseEntity with stored Url in the body and HttpStatus
	 *         NOT_FOUND.
	 */
	@Override
	@RequestMapping(value = "/{shortUrl}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Url> get(@PathVariable("shortUrl") String shortUrl) {
		Url url = urlShortnerService.get(shortUrl);
		if (Optional.ofNullable(url).isPresent()) {
			return new ResponseEntity<Url>(url, new HttpHeaders(), HttpStatus.OK);
		} else {
			return new ResponseEntity<Url>(HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * This method creates JSON response on http GET /api request and return all
	 * stored Urls.
	 *
	 * @return JSON HTTP ResponseEntity with list of all stored Urls and
	 *         HttpStatus OK status in successes case. In case of empty database
	 *         return HttpStatus NOT_FOUND.
	 */
	@Override
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<List<Url>> getAll() {
		List<Url> urls = new ArrayList<Url>();
		urls = urlShortnerService.getAll();
		if (Optional.ofNullable(urls).isPresent()) {
			return new ResponseEntity<List<Url>>(urls, new HttpHeaders(), HttpStatus.OK);
		} else {
			return new ResponseEntity<List<Url>>(HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * This method creates JSON response on http POST /api request with long url
	 * in the text body to be paired with short one.
	 *
	 * @param longUrl
	 *            String url representation.
	 *
	 * @return JSON HTTP ResponseEntity with new instance of Url in the body and
	 *         HttpStatus CREATED in successes case. In other case return
	 *         HttpStatus INTERNAL_SERVER_ERROR / BAD_REQUEST.
	 */
	@Override
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Url> create(@RequestBody String longUrl, UriComponentsBuilder ucBuilder) {

		if (longUrl != "") {
			Url url = urlShortnerService.create(longUrl);
			HttpHeaders headers = new HttpHeaders();
			if (Optional.ofNullable(url).isPresent())  {
				headers.setLocation(ucBuilder.path("/api/{shortUrl}").buildAndExpand(url.getShortUrl()).toUri());
				return new ResponseEntity<Url>(url, headers, HttpStatus.CREATED);
			} else {
				return new ResponseEntity<Url>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			return new ResponseEntity<Url>(HttpStatus.BAD_REQUEST);
		}

	}

	/**
	 * This method creates JSON response on http Put /api/{shortUrl} request with
	 * short url in the path, long url in text body and try to update Url object
	 * with same short url field.
	 *
	 * @param shortUrl
	 *            String url representation.
	 *
	 * @param longUrl,
	 *            String url representation.
	 *
	 * @return JSON HTTP ResponseEntity with updated Url in the body and
	 *         HttpStatus OK in successes case. In other case return JSON HTTP
	 *         ResponseEntity with Url in the body and HttpStatus NOT_FOUND or
	 *         HttpStatus BAD_REQUEST in case of empty longUrl or shortUrl.
	 */
	@Override
	@RequestMapping(value = "/{shortUrl}", method = RequestMethod.PUT, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Url> update(@PathVariable("shortUrl") String shortUrl, @RequestBody String longUrl) {
		if (longUrl != "") {
			Url url = urlShortnerService.update(shortUrl, longUrl);
			if (Optional.ofNullable(url).isPresent()) {
				return new ResponseEntity<Url>(url, new HttpHeaders(), HttpStatus.OK);
			} else {
				return new ResponseEntity<Url>(HttpStatus.NOT_FOUND);
			}
		} else {
			return new ResponseEntity<Url>(HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * This method creates response on http Delete /api/{shortUrl} request with
	 * short url in the path and try to delete Url object with same short url
	 * field.
	 *
	 * @param shortUrl
	 *            String url representation.
	 *
	 * @return HTTP ResponseEntity with HttpStatus NO_CONTENT in successes case.
	 *         In other case return HttpStatus NOT_FOUND or HttpStatus
	 *         BAD_REQUEST in case of empty shortUrl.
	 */
	@Override
	@RequestMapping(value = "/{shortUrl}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> remove(@PathVariable("shortUrl") String shortUrl) {
		if (!urlShortnerService.remove(shortUrl)) {
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		}
	}

	/**
	 * This method creates response on http Delete /api/ request and try to
	 * delete stored Urls base on getAll list.
	 *
	 * @return HTTP ResponseEntity with HttpStatus OK in successes case. In
	 *         other case return HttpStatus NO_CONTENT (for example if database
	 *         is empty).
	 */
	@Override
	@RequestMapping(method = RequestMethod.DELETE)
	public ResponseEntity<Void> removeAll() {
		if (!urlShortnerService.removeAll()) {
			return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		}
	}

}
