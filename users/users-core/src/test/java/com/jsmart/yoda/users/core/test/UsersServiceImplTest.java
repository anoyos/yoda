package com.jsmart.yoda.users.core.test;

import com.jsmart.yoda.users.base.UrlInfo;
import com.jsmart.yoda.users.base.User;
import com.jsmart.yoda.users.base.client.UrlInfoClient;
import com.jsmart.yoda.users.base.repository.UsersRepository;
import com.jsmart.yoda.users.core.service.UsersServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
// import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author Sergey Khomich
 *
 */
public class UsersServiceImplTest {

	private UsersServiceImpl usersService;
	private UsersRepository mockRepository;
	private UrlInfoClient mockUrlInfoClient;

	private User createTestUser(int id) {

		User user = new User();
		user.setId(id);
		user.setCreatedAt(new Date());
		user.setModifiedAt(new Date());
		user.setCreatedBy(null);
		user.setModifiedBy(null);
		user.setName("testUser");
		user.setEmail("testUser@email.com");
		user.setPassword("testPassword");
		user.setUrlInfos(createTestUrlInfos());

		return user;
	}

	private List<UrlInfo> createTestUrlInfos() {

		UrlInfo urlInfo = new UrlInfo();
		urlInfo.setShortUrl("abbac");
		urlInfo.setCreatedAt(new Date());
		urlInfo.setModifiedAt(new Date());
		urlInfo.setCreatedBy(null);
		urlInfo.setModifiedBy(null);
		urlInfo.setLongUrl("http://mail.ru");

		List<UrlInfo> urlInfos = new ArrayList<UrlInfo>();
		urlInfos.add(urlInfo);

		return urlInfos;
	}

	@Before
	public void setupUp() {
		usersService = new UsersServiceImpl();
		mockRepository = mock(UsersRepository.class);
		mockUrlInfoClient = mock(UrlInfoClient.class);
		usersService.setRepository(mockRepository);
		usersService.setUrlInfoClient(mockUrlInfoClient);
	}

	@Test
	public void testGetSuccess() throws Exception {

		User testUser = createTestUser(2);
		List<UrlInfo> testUrlInfos = createTestUrlInfos();

		when(mockRepository.findOne(testUser.getId())).thenReturn(testUser);
		when(mockUrlInfoClient.get(testUser.getId())).thenReturn(testUrlInfos);
		User resultUser = usersService.get(testUser.getId());

		InOrder order = inOrder(mockRepository, mockUrlInfoClient);
		order.verify(mockRepository).findOne(testUser.getId());
		order.verify(mockUrlInfoClient).get(testUser.getId());

		assertEquals(resultUser, testUser);
		assertEquals(resultUser.getUrlInfos(), testUrlInfos);
	}

	@Test
	public void testGetNotSuccess() throws Exception {

		User testUser = createTestUser(2);

		when(mockRepository.findOne(testUser.getId())).thenReturn(null);
		User resultUser = usersService.get(testUser.getId());

		verify(mockRepository).findOne(testUser.getId());
		verify(mockUrlInfoClient, never()).get(anyInt());
		assertEquals(resultUser, null);
	}

	@Test
	public void testGetAllSuccess() throws Exception {

		List<User> testUsers = new ArrayList<User>();
		testUsers.add(createTestUser(1));
		testUsers.add(createTestUser(2));
		// BeanUtils.copyProperties(user, secondUser);

		List<UrlInfo> testUrlInfos = createTestUrlInfos();

		when(mockRepository.findAll()).thenReturn(testUsers);
		when(mockUrlInfoClient.get(anyInt())).thenReturn(testUrlInfos);
		List<User> resultUsers = usersService.getAll();

		InOrder order = inOrder(mockRepository, mockUrlInfoClient);
		order.verify(mockRepository).findAll();
		order.verify(mockUrlInfoClient, times(2)).get(anyInt());
		assertEquals(resultUsers, testUsers);
		assertEquals(resultUsers.get(0).getUrlInfos(), testUrlInfos);
		assertEquals(resultUsers.get(1).getUrlInfos(), testUrlInfos);
	}

	@Test
	public void testGetAllNotSuccess() throws Exception {

		when(mockRepository.findAll()).thenReturn(new ArrayList<User>());
		List<User> resultUsers = usersService.getAll();

		verify(mockRepository).findAll();
		verify(mockUrlInfoClient, never()).get(anyInt());
		assertEquals(resultUsers.isEmpty(), true);
	}

	@Test
	public void testCreateSuccess() throws Exception {

		User testUser = createTestUser(2);

		when(mockRepository.exists(testUser.getId())).thenReturn(false);
		when(mockRepository.save(testUser)).thenReturn(testUser);
		User resultUser = usersService.create(testUser);

		InOrder order = inOrder(mockRepository);
		order.verify(mockRepository).exists(testUser.getId());
		order.verify(mockRepository).save(testUser);
		assertEquals(resultUser, testUser);
	}

	@Test
	public void testCreateNotSuccess() throws Exception {

		User testUser = createTestUser(2);

		when(mockRepository.exists(testUser.getId())).thenReturn(true);
		User resultUser = usersService.create(testUser);

		verify(mockRepository).exists(testUser.getId());
		verify(mockRepository, never()).save(testUser);
		assertEquals(resultUser, null);
	}

	@Test
	public void testUpdateSuccess() throws Exception {

		User testUser = createTestUser(2);
		List<UrlInfo> testUrlInfos = createTestUrlInfos();

		User testUserChanged = new User();
		testUserChanged.setId(2);
		testUserChanged.setCreatedAt(new Date());
		testUserChanged.setModifiedAt(new Date());
		testUserChanged.setName("testUserChanged");
		testUserChanged.setEmail("testUserChanged@email.com");
		testUserChanged.setPassword("passwordChanged");

		when(mockRepository.findOne(testUser.getId())).thenReturn(testUser);
		when(mockRepository.save(testUserChanged)).thenReturn(testUserChanged);
		when(mockUrlInfoClient.get(testUserChanged.getId())).thenReturn(testUrlInfos);

		assertEquals(usersService.update(testUser.getId(), testUserChanged), testUserChanged);

		InOrder order = inOrder(mockRepository, mockUrlInfoClient);
		order.verify(mockRepository).findOne(testUser.getId());
		order.verify(mockRepository).save(testUserChanged);
		order.verify(mockUrlInfoClient).get(testUserChanged.getId());
	}

	@Test
	public void testUpdateNotSuccess() throws Exception {

		User testUser = createTestUser(2);

		User testUserChanged = new User();
		testUserChanged.setId(2);
		testUserChanged.setCreatedAt(new Date());
		testUserChanged.setModifiedAt(new Date());
		testUserChanged.setName("testUserChanged");
		testUserChanged.setEmail("testUserChanged@email.com");
		testUserChanged.setPassword("passwordChanged");

		when(mockRepository.findOne(testUser.getId())).thenReturn(null);

		assertEquals(usersService.update(testUser.getId(), testUserChanged), null);

		verify(mockRepository).findOne(testUser.getId());
		verify(mockRepository, never()).save(testUser);
		verify(mockUrlInfoClient, never()).get(testUser.getId());
	}

	@Test
	public void testRemoveSuccess() throws Exception {

		User testUser = createTestUser(2);

		when(mockRepository.exists(testUser.getId())).thenReturn(true);
		when(mockUrlInfoClient.remove(testUser.getId())).thenReturn(true);

		assertEquals(usersService.remove(testUser.getId()), true);

		InOrder order = inOrder(mockRepository, mockUrlInfoClient);
		order.verify(mockRepository).exists(testUser.getId());
		order.verify(mockUrlInfoClient).remove(testUser.getId());
		order.verify(mockRepository).delete(testUser.getId());
	}

	@Test
	public void testRemoveNotSuccessNotExist() throws Exception {

		User testUser = createTestUser(2);

		when(mockRepository.exists(testUser.getId())).thenReturn(false);

		assertEquals(usersService.remove(testUser.getId()), false);

		verify(mockUrlInfoClient, never()).remove(testUser.getId());
		verify(mockRepository, never()).delete(testUser.getId());
	}

	@Test
	public void testRemoveNotSuccessUrlInfoNotRemoved() throws Exception {

		User testUser = createTestUser(2);

		when(mockRepository.exists(testUser.getId())).thenReturn(true);
		when(mockUrlInfoClient.remove(testUser.getId())).thenReturn(false);

		assertEquals(usersService.remove(testUser.getId()), false);

		verify(mockUrlInfoClient).remove(testUser.getId());
		verify(mockRepository, never()).delete(testUser.getId());
	}

	@Test
	public void testRemoveAllSuccess() throws Exception {

		when(mockUrlInfoClient.removeAll()).thenReturn(true);

		assertEquals(usersService.removeAll(), true);

		InOrder order = inOrder(mockUrlInfoClient, mockRepository);
		order.verify(mockUrlInfoClient).removeAll();
		order.verify(mockRepository).deleteAll();
	}

	@Test
	public void testRemoveAllNotSuccess() throws Exception {

		when(mockUrlInfoClient.removeAll()).thenReturn(false);

		assertEquals(usersService.removeAll(), false);

		verify(mockUrlInfoClient).removeAll();
 		verify(mockRepository, never()).deleteAll();
	}

}
