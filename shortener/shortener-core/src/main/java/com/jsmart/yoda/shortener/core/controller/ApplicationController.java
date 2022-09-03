package com.jsmart.yoda.shortener.core.controller;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.jsmart.yoda.shortener.base.Url;
import com.jsmart.yoda.shortener.base.service.ShortnerService;

/**
 * @author Nick Koretskyy
 * 
 *         This class represents main product feature - User is redirected to long URL
 *         by calling short one.
 *
 */
@Controller
@RequestMapping(value = "/service")
public class ApplicationController {

	@Autowired
	private ShortnerService urlShortnerService;

	private static final Logger log = Logger.getLogger(ApplicationController.class);

	/**
	 * This is the main method which implements redirect from short to long URL
	 * feature base on 301 HTTP SEE_OTHER status.
	 * 
	 * @param shortUrl
	 *            unique short 5 letter combined with decimal String that's
	 *            equal to non - unique long url.
	 * 
	 * @return in success case HTTP response Entity with 301 see other status
	 *         and url redirect to, in other case Not found.
	 */
	@RequestMapping(value = "/{shortUrl}", method = RequestMethod.GET)
	public ResponseEntity<Void> getUrlForRedirect(@PathVariable("shortUrl") String shortUrl) {

		Url url = urlShortnerService.get(shortUrl);
		if (url == null) {
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}

		try {
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.setLocation(new URI(url.getLongUrl()));
			return new ResponseEntity<Void>(responseHeaders, HttpStatus.SEE_OTHER);
		} catch (URISyntaxException urise) {
			log.error("URISyntaxException: " + urise + " \n");
			return null;
		}
	}
}
