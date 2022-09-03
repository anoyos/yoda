package com.jsmart.yoda.shortener.core.test;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.jsmart.yoda.shortener.base.service.ShortnerService;
import com.jsmart.yoda.shortener.core.config.TestApplicationControllerConfiguration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import static org.hamcrest.Matchers.is;

/**
 * @author Nikolay Koretskyy
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestApplicationControllerConfiguration.class)
@TestPropertySource(properties = "spring.redis.database=1")
@WebAppConfiguration
@SpringBootApplication // I still don't understand why it's doesn't work without this annotation
public class ApplicationControllerIT {

	private MockMvc mockMvc;

	@Autowired
	private ShortnerService urlShortnerService;

	@Autowired
	private WebApplicationContext webApplicationContext;

	private final String serviceUri = "/service";

	/**
	 * Auxiliary method for creation object which return shortUrl. Throws
	 * AssertionError if something going wrong.
	 *
	 * @param longUrl
	 *            Unique string with long url to create.
	 *
	 * @return shortUrl Key of just created Url, unique short letter string.
	 *
	 * @throws Exception
	 */
	private String create(String longUrl) throws Exception {
		return urlShortnerService.create(longUrl).getShortUrl();
	}

	private void remove(String shortUrl) throws Exception {
		urlShortnerService.remove(shortUrl);
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// This will output classpathes of application (befor Spring Boot logo)
		/* 
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		URL[] urls = ((URLClassLoader)cl).getURLs();
		for(URL url : urls) {
			System.out.println(url.getFile());
		}
		*/
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetSuccess() throws Exception {

		String longUrl = "http://ua.fm";
		String shortUrl = create(longUrl);
		mockMvc.perform(get(serviceUri + "/" + shortUrl))
				.andExpect(status().isSeeOther())
				.andExpect(MockMvcResultMatchers.header().string("Location", is(longUrl)))
				.andExpect(content().string(""))
				;
		remove(shortUrl);
	}

	@Test
	public void testGetNotSuccess() throws Exception {
		mockMvc.perform(get(serviceUri + "/asdas"))
				.andExpect(status().isNotFound())
				.andExpect(content().string(""))
				;
	}
}
