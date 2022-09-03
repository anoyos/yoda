package com.jsmart.yoda.shortener.core.test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import com.jsmart.yoda.shortener.base.Url;
import com.jsmart.yoda.shortener.base.dao.ShortnerDao;
import com.jsmart.yoda.shortener.core.model.ShortUrlGeneratorSettings;
import com.jsmart.yoda.shortener.core.service.ShortnerServiceImpl;

/**
 * @author Nick Koretskyy
 *
 */
public class ShortnerServiceImplTest {

	private ShortnerServiceImpl urlShortnerServiceImpl;
	private ShortnerDao mockUrlShortnerDao;
	private ShortUrlGeneratorSettings shortUrlGeneratorSettings;

	@Before
	public void setupUp() {
		urlShortnerServiceImpl = new ShortnerServiceImpl();

		mockUrlShortnerDao = mock(ShortnerDao.class);
		urlShortnerServiceImpl.setUrlShortnerDao(mockUrlShortnerDao);

		shortUrlGeneratorSettings = new ShortUrlGeneratorSettings();
		shortUrlGeneratorSettings.setShortUrlLenght(5);
		urlShortnerServiceImpl.setShortUrlGeneratorConfig(shortUrlGeneratorSettings);
	}

	@Test
	public void testGetSuccess() throws Exception {

		String shortUrl = "asdas";
		String longUrl = "google.com";
		Url testUrl = new Url(shortUrl, longUrl);

		when(mockUrlShortnerDao.get(shortUrl)).thenReturn(testUrl);

		Url url = urlShortnerServiceImpl.get(shortUrl);

		verify(mockUrlShortnerDao).get(shortUrl);
		assertEquals(url.getShortUrl(), shortUrl);
		assertEquals(url.getLongUrl(), longUrl);
	}

	@Test
	public void testGetNotSuccess() throws Exception {

		String shortUrl2 = "zxczx";

		when(mockUrlShortnerDao.get(shortUrl2)).thenReturn(null);

		Url url = urlShortnerServiceImpl.get(shortUrl2);

		verify(mockUrlShortnerDao).get(shortUrl2);
		assertEquals(url, null);
	}

	@Test
	public void testGetAllSuccess() throws Exception {

		List<Url> urls = new ArrayList<>();
		urls.add(new Url("asdas", "facebook.com"));
		urls.add(new Url("rtyty", "twitter.com"));
		urls.add(new Url("vbnnm", "instagram.com"));

		when(mockUrlShortnerDao.getAll()).thenReturn(urls);

		List<Url> receivedUrls = urlShortnerServiceImpl.getAll();

		verify(mockUrlShortnerDao).getAll();
		assertEquals(receivedUrls.get(0).getShortUrl(), "asdas");
		assertEquals(receivedUrls.get(0).getLongUrl(), "facebook.com");
		assertEquals(receivedUrls.get(1).getShortUrl(), "rtyty");
		assertEquals(receivedUrls.get(1).getLongUrl(), "twitter.com");
		assertEquals(receivedUrls.get(2).getShortUrl(), "vbnnm");
		assertEquals(receivedUrls.get(2).getLongUrl(), "instagram.com");
	}

	@Test
	public void testGetAllNotSuccess() throws Exception {

		when(mockUrlShortnerDao.getAll()).thenReturn(null);

		List<Url> receivedUrls = urlShortnerServiceImpl.getAll();

		verify(mockUrlShortnerDao).getAll();
		assertEquals(receivedUrls, null);
	}

	@Test
	public void testCreateSuccess() throws Exception {

		String longUrl = "https://google.com";

		when(mockUrlShortnerDao.create(anyObject())).thenReturn(true);

		Url url = urlShortnerServiceImpl.create(longUrl);

		verify(mockUrlShortnerDao).create(anyObject());
		assertThat(url.getShortUrl(), instanceOf(java.lang.String.class));
		assertEquals(url.getShortUrl().length(), shortUrlGeneratorSettings.getShortUrlLenght());
		assertEquals(url.getLongUrl(), longUrl);
	}

	@Test
	public void testCreateNotSuccess() throws Exception {

		String longUrl = "https://google.com";

		when(mockUrlShortnerDao.create(anyObject())).thenReturn(false);

		Url url = urlShortnerServiceImpl.create(longUrl);

		verify(mockUrlShortnerDao).create(anyObject());
		assertEquals(url, null);
	}

	@Test
	public void testUpdateSuccess() throws Exception {

		String shortUrl = "asdas";
		String longUrl = "google.com";
		Url testUrl = new Url(shortUrl, longUrl);

		String longUrlChanged = "yahoo.com";
		Url testUrlChanged = new Url(shortUrl, longUrlChanged);

		longUrlChanged = urlShortnerServiceImpl.fixLongUrl(longUrlChanged);

		when(mockUrlShortnerDao.get(shortUrl)).thenReturn(testUrl);
		when(mockUrlShortnerDao.update(shortUrl, longUrlChanged)).thenReturn(testUrlChanged);

		assertEquals(urlShortnerServiceImpl.update(shortUrl, longUrlChanged), testUrlChanged);

		InOrder order = inOrder(mockUrlShortnerDao);
		order.verify(mockUrlShortnerDao).get(shortUrl);
		order.verify(mockUrlShortnerDao).update(shortUrl, longUrlChanged);
	}

	@Test
	public void testUpdateNotSuccess() throws Exception {

		String shortUrl = "asdas";

		String longUrlChanged = "yahoo.com";

		when(mockUrlShortnerDao.get(shortUrl)).thenReturn(null);

		assertEquals(urlShortnerServiceImpl.update(shortUrl, longUrlChanged), null);

		verify(mockUrlShortnerDao).get(shortUrl);
		verify(mockUrlShortnerDao, never()).update(shortUrl, longUrlChanged);
	}

	@Test
	public void testRemoveSuccess() throws Exception {

		String shortUrl = "asdas";
		String longUrl = "google.com";
		Url testUrl = new Url(shortUrl, longUrl);

		when(mockUrlShortnerDao.get(shortUrl)).thenReturn(testUrl);

		assertEquals(urlShortnerServiceImpl.remove(shortUrl), true);

		InOrder order = inOrder(mockUrlShortnerDao);
		order.verify(mockUrlShortnerDao).get(shortUrl);
		order.verify(mockUrlShortnerDao).remove(shortUrl);
	}

	@Test
	public void testRemoveNotSuccess() throws Exception {

		String shortUrl = "zxczx";

		when(mockUrlShortnerDao.get(shortUrl)).thenReturn(null);

		assertEquals(urlShortnerServiceImpl.remove(shortUrl), false);

		verify(mockUrlShortnerDao).get(shortUrl);
		verify(mockUrlShortnerDao, never()).remove(shortUrl);
	}

	@Test
	public void testRemoveAllSuccess() throws Exception {

		when(mockUrlShortnerDao.removeAll()).thenReturn(true);

		assertEquals(urlShortnerServiceImpl.removeAll(), true);
		verify(mockUrlShortnerDao).removeAll();

	}

	@Test
	public void testRemoveAllNotSuccess() throws Exception {

		when(mockUrlShortnerDao.removeAll()).thenReturn(false);

		assertEquals(urlShortnerServiceImpl.removeAll(), false);
		verify(mockUrlShortnerDao).removeAll();

	}

	@Test
	public void testFixLongUrl() throws Exception {

		String longUrl = "http://mail.ru";
		assertEquals(urlShortnerServiceImpl.fixLongUrl(longUrl), longUrl);

		longUrl = "https://mail.ru";
		assertEquals(urlShortnerServiceImpl.fixLongUrl(longUrl), longUrl);

		longUrl = "mail.ru";
		assertEquals(urlShortnerServiceImpl.fixLongUrl(longUrl), "http://".concat(longUrl));
	}

}
