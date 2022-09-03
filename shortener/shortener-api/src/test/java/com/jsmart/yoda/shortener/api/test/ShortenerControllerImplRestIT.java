package com.jsmart.yoda.shortener.api.test;

import java.nio.charset.Charset;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.jsmart.yoda.shortener.api.config.TestApiConfiguration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

/**
 * @author Nikolay Koretskyy
 *
 */
// @RunWith(SpringJUnit4ClassRunner.class)
@RunWith(JUnitParamsRunner.class)
@SpringApplicationConfiguration(classes = TestApiConfiguration.class)
// @TestPropertySource(locations="classpath:test.properties")
@TestPropertySource(properties = "spring.redis.database=1")
@WebAppConfiguration
@SpringBootApplication // I still don't understand why it's doesn't work without this annotation
public class ShortenerControllerImplRestIT {

	private MediaType responseContentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	private final String preUri = "http://localhost";
	private final String apiUri = "/api";

	/**
	 * SpringJUnit4ClassRunner.class allows to load Spring context but disallows
	 * to run test methods with parameters. So we will use
	 * JUnitParamsRunner.class for run test methods with parameters and fields
	 * SCR & springMethodRule to load Spring context.
	 *
	 * @see org.springframework.test.context.junit4.rules.SpringMethodRule
	 *
	 */
	@ClassRule
	public static final SpringClassRule SCR = new SpringClassRule();

	@Rule
	public final SpringMethodRule springMethodRule = new SpringMethodRule();

	/**
	 * This method generates data for parametrized method TestCreateSuccess. For
	 * naming - name conventions were used.
	 *
	 * @see https://github.com/Pragmatists/JUnitParams
	 *
	 * @return parameters as iterable Object
	 */
	private Object[] parametersForTestCreateSuccess() {
		return new Object[] {
				new Object[] {"ua.fm", "http://ua.fm"},
				new Object[] {"http://ua.fm", "http://ua.fm"},
				new Object[] {"https://ua.fm", "https://ua.fm"}
		};
	}

	/**
	 * Auxiliary method for creation object which return shortUrl. Throws
	 * AssertionError if something going wrong.
	 *
	 * @param longUrl
	 *            Unique string with long url to create.
	 *
	 * @see com.jsmart.cloud.service.shortener.base.Url.
	 *
	 * @return shortUrl Key of just created Url, unique short letter string.
	 *
	 * @throws Exception
	 */
	private String create(String longUrl) throws Exception {

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(apiUri)
				.contentType(MediaType.TEXT_PLAIN_VALUE)
				.content(longUrl);

		String location = mockMvc.perform(request)
				.andReturn().getResponse().getHeader("Location");

		String shortUrl = "";
		try {
			shortUrl = location.substring(location.lastIndexOf('/') + 1);
		} catch (Exception ex) {
			Assert.fail("Wrong shortUrl in Location header");
		}
		return shortUrl;
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

		mockMvc.perform(delete(apiUri));
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetSuccess() throws Exception {

		String longUrl = "http://ua.fm";
		String shortUrl = create(longUrl);
		mockMvc.perform(get(apiUri + "/" + shortUrl))
				.andExpect(status().isOk())
				.andExpect(content().contentType(responseContentType))
				.andExpect(jsonPath("$.shortUrl", is(shortUrl)))
				.andExpect(jsonPath("$.longUrl", is(longUrl)))
				;
	}

	@Test
	public void testGetNotSuccess() throws Exception {
		mockMvc.perform(get(apiUri + "/asdas"))
				.andExpect(status().isNotFound())
				.andExpect(content().string(""))
				;
	}

	@Test
	public void testGetAllSuccess() throws Exception {

		create("http://ua.fm");
		create("http://google.com");
		create("http://yandex.ua");
		create("http://yahoo.com");

		mockMvc.perform(get(apiUri))
				.andExpect(status().isOk())
				.andExpect(content().contentType(responseContentType))
				.andExpect(jsonPath("$", hasSize(4)))
				;
	}

	@Test
	public void testGetAllEmptySuccess() throws Exception {
		mockMvc.perform(get(apiUri))
				.andExpect(status().isOk())
				.andExpect(content().string("[]"))
				;
	}

	@Test
	@Parameters
	public void testCreateSuccess(String postLongUrl, String respondLongUrl) throws Exception {
		mockMvc.perform(post(apiUri)
				.contentType(MediaType.TEXT_PLAIN_VALUE)
				.content(postLongUrl))
				.andExpect(status().isCreated())
				.andExpect(MockMvcResultMatchers.header().string("Location", Matchers.startsWith(preUri + apiUri)))
				.andExpect(content().contentType(responseContentType))
				.andExpect(jsonPath("$.shortUrl", notNullValue()))
				.andExpect(jsonPath("$.longUrl", is(respondLongUrl)))
				;

		mockMvc.perform(get(apiUri))
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].shortUrl", notNullValue()))
				.andExpect(jsonPath("$[0].longUrl", is(respondLongUrl)))
				;
	}

/*
  	@Test
	public void testCreateSuccess() throws Exception {
		mockMvc.perform(post(apiUri)
				.contentType(MediaType.TEXT_PLAIN_VALUE)
				.content("ua.fm"))
				.andExpect(status().isCreated())
				.andExpect(MockMvcResultMatchers.header().string("Location", Matchers.startsWith(preUri + apiUri)))
				.andExpect(content().contentType(responseContentType))
				.andExpect(jsonPath("$.shortUrl", notNullValue()))
				.andExpect(jsonPath("$.longUrl", is("http://ua.fm")))
				;

		mockMvc.perform(get(apiUri))
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].shortUrl", notNullValue()))
				.andExpect(jsonPath("$[0].longUrl", is("http://ua.fm")))
				;
	}

	@Test
	public void testCreateWithHttpSuccess() throws Exception {
		mockMvc.perform(post(apiUri)
				.contentType(MediaType.TEXT_PLAIN_VALUE)
				.content("http://ua.fm"))
				.andExpect(status().isCreated())
				.andExpect(MockMvcResultMatchers.header().string("Location", Matchers.startsWith(preUri + apiUri)))
				.andExpect(content().contentType(responseContentType))
				.andExpect(jsonPath("$.shortUrl", notNullValue()))
				.andExpect(jsonPath("$.longUrl", is("http://ua.fm")))
				;

		mockMvc.perform(get(apiUri))
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].shortUrl", notNullValue()))
				.andExpect(jsonPath("$[0].longUrl", is("http://ua.fm")))
				;
	}

	@Test
	public void testCreateWithHttpsSuccess() throws Exception {
		mockMvc.perform(post(apiUri)
				.contentType(MediaType.TEXT_PLAIN_VALUE)
				.content("https://ua.fm"))
				.andExpect(status().isCreated())
				.andExpect(MockMvcResultMatchers.header().string("Location", Matchers.startsWith(preUri + apiUri)))
				.andExpect(content().contentType(responseContentType))
				.andExpect(jsonPath("$.shortUrl", notNullValue()))
				.andExpect(jsonPath("$.longUrl", is("https://ua.fm")))
				;

		mockMvc.perform(get(apiUri))
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].shortUrl", notNullValue()))
				.andExpect(jsonPath("$[0].longUrl", is("https://ua.fm")))
				;
	}
*/
	@Test
	public void testCreateNotSuccessBadRequest() throws Exception {
		mockMvc.perform(post(apiUri)
				.contentType(MediaType.TEXT_PLAIN_VALUE)
				.content(""))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(""))
				;
	}

	@Test
	public void testUpdateSuccess() throws Exception {
		String shortUrl = create("ua.fm");
		mockMvc.perform(put(apiUri + "/" + shortUrl)
				.contentType(MediaType.TEXT_PLAIN_VALUE)
				.content("google.com"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(responseContentType))
				.andExpect(jsonPath("$.shortUrl", is(shortUrl)))
				.andExpect(jsonPath("$.longUrl", is("http://google.com")))
				;

		mockMvc.perform(get(apiUri + "/" + shortUrl))
				.andExpect(jsonPath("$.shortUrl", is(shortUrl)))
				.andExpect(jsonPath("$.longUrl", is("http://google.com")))
				;
	}

	@Test
	public void testUpdateNotSuccessNotFound() throws Exception {
		mockMvc.perform(put(apiUri + "/asdas")
				.contentType(MediaType.TEXT_PLAIN_VALUE)
				.content("google.com"))
				.andExpect(status().isNotFound())
				.andExpect(content().string(""))
				;
	}

	@Test
	public void testUpdateNotSuccessBadRequest() throws Exception {
		String shortUrl = create("ua.fm");
		mockMvc.perform(put(apiUri + "/" + shortUrl)
				.contentType(MediaType.TEXT_PLAIN_VALUE)
				.content(""))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(""))
				;
	}

	@Test
	public void testDeleteSuccess() throws Exception {
		String shortUrl = create("ua.fm");
		mockMvc.perform(delete(apiUri + "/" + shortUrl))
				.andExpect(status().isNoContent())
				.andExpect(content().string(""))
				;

		mockMvc.perform(get(apiUri + "/" + shortUrl))
				.andExpect(status().isNotFound())
				.andExpect(content().string(""))
				;
	}

	@Test
	public void testDeleteNotSuccess() throws Exception {
		mockMvc.perform(delete(apiUri + "/asdas"))
				.andExpect(status().isNotFound())
				.andExpect(content().string(""))
				;
	}

	@Test
	public void testDeleteAllSuccess() throws Exception {

		create("http://ua.fm");
		create("http://google.com");
		create("http://yandex.ua");
		create("http://yahoo.com");

		mockMvc.perform(delete(apiUri))
				.andExpect(status().isNoContent())
				.andExpect(content().string(""))
				;

		mockMvc.perform(get(apiUri))
				.andExpect(status().isOk())
				.andExpect(content().string("[]"))
				;
	}

}
