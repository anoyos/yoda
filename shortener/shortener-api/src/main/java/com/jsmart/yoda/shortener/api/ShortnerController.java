package com.jsmart.yoda.shortener.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import com.jsmart.yoda.shortener.base.Url;

/**
 * @author Nick Koretskyy
 *
 */
public interface ShortnerController {

	public ResponseEntity<Url> get(String shortUrl);

	public ResponseEntity<List<Url>> getAll();

	public ResponseEntity<Url> create(String longUrl, UriComponentsBuilder ucBuilder);

	public ResponseEntity<Url> update(String shortUrl, String longUrl);

	public ResponseEntity<Void> remove(String shortUrl);

	public ResponseEntity<Void> removeAll();

}
