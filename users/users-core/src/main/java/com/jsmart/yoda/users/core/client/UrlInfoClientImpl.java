package com.jsmart.yoda.users.core.client;

import com.jsmart.yoda.users.base.UrlInfo;

import java.util.List;

import com.jsmart.yoda.users.base.client.UrlInfoClient;
import org.apache.log4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * @author Sergey Khomich
 *
 */
@Deprecated
public class UrlInfoClientImpl implements UrlInfoClient {

	private static final String URLINFO_SERVICE_URI = "http://localhost/urlinfoservice/";
	private static final Logger log = Logger.getLogger(UrlInfoClientImpl.class);

	public List<UrlInfo> get(int id) {

		log.info("Fetching user " + id + " urlinfo");

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<List<UrlInfo>> urlInfoResponse =
				restTemplate.exchange(URLINFO_SERVICE_URI + id, HttpMethod.GET,
						null, new ParameterizedTypeReference<List<UrlInfo>>(){});
		return urlInfoResponse.getBody();
	}

	public boolean remove(int id) {
		RestTemplate restTemplate = new RestTemplate();
		try {
			restTemplate.delete(URLINFO_SERVICE_URI + id);
			log.info("Request to delete user " + id + " urlinfo succeeded");
			return true;
		} catch (RestClientException msg) {
			log.info("Request to delete user " + id + " urlinfo unsucceeded");
			return false;
		}
	}

	public boolean removeAll() {
		RestTemplate restTemplate = new RestTemplate();
		try {
			restTemplate.delete(URLINFO_SERVICE_URI);
			log.info("Request to delete all urlinfo succeeded");
			return true;
		} catch (RestClientException msg) {
			log.info("Request to delete all urlinfo unsucceeded. Something went wrong - " + msg.toString());
			return false;
		}
	}
}
