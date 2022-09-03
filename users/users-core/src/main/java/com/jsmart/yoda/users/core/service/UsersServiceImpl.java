package com.jsmart.yoda.users.core.service;

import com.jsmart.yoda.users.base.UrlInfo;
import com.jsmart.yoda.users.base.User;
import com.jsmart.yoda.users.base.client.UrlInfoClient;
import com.jsmart.yoda.users.base.repository.UsersRepository;
import com.jsmart.yoda.users.base.service.UsersService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Sergey Khomich
 *
 *          This class covers Users operations routine.
 *
 */
@Service
public class UsersServiceImpl implements UsersService {

	@Autowired
	private UsersRepository repository;

	//TODO Update after urlInfoClient implementation
	//@Autowired
	//private UrlInfoClient urlInfoClient;
	UrlInfoClient urlInfoClient = new UrlInfoClient() {
	public List<UrlInfo> get(int id) {

			UrlInfo urlInfo = new UrlInfo();
			urlInfo.setShortUrl("abbac");
			urlInfo.setCreatedAt(new Date());
			urlInfo.setModifiedAt(new Date());
			urlInfo.setCreatedBy(repository.getOne(id));
			urlInfo.setModifiedBy(repository.getOne(id));
			urlInfo.setLongUrl("http://mail.ru");

			List<UrlInfo> urlInfoList = new ArrayList<UrlInfo>();
			urlInfoList.add(urlInfo);

			return urlInfoList;
		}

		public boolean remove(int id) {
			return true;
		}

		public boolean removeAll() {
			return true;
		}

	};

	private static final Logger log = Logger.getLogger(UsersServiceImpl.class);

	public void setRepository(UsersRepository repository) {
		this.repository = repository;
	}

	public void setUrlInfoClient(UrlInfoClient urlInfoClient) {
		this.urlInfoClient = urlInfoClient;
	}

	/**
	 * This method fetches stored User - base on User ID from repository and fill his urlInfo field.
	 *
	 * @param com.jsmart.yoda.users.base.User.
	 *
	 * @see com.jsmart.yoda.users.base.User.
	 *
	 * @return full User's info.
	 */
	@Override
	public User get(int id) {

		log.info("Fetching user with id " + id);

		User user = repository.findOne(id);
		if (Optional.ofNullable(user).isPresent()) {
			user.setUrlInfos(urlInfoClient.get(id));
			return user;
		} else {
			log.info("User with id " + id + " was not found");
			return null;

		}
	}

	/**
	 * This method fetches all stored Users and fill theirs UrlInfo fields.
	 *
	 * @see com.jsmart.yoda.users.base.User.
	 *
	 * @return stored Users list with full User's info.
	 */
	@Override
	public List<User> getAll() {

		log.info("Fetching all users");

		List<User> users = new ArrayList<User>();
		users = repository.findAll();
		users.forEach((u) -> u.setUrlInfos(urlInfoClient.get(u.getId())));
		return users;
	}

	/**
	 * This method creates new User record in MySql DB.
	 *
	 * @param com.jsmart.yoda.users.base.User.
	 *
	 * @see com.jsmart.yoda.users.base.User.
	 *
	 * @return complete User instance.
	 */
	@Override
	public User create(User user) {

		log.info("Creating user " + user.getName());

		if (repository.exists(user.getId())) {
			log.info("User with id " + user.getId() + " already exist");
			return null;
		}

		user.setCreatedAt(new Date());
		// TODO when authentication will be done
		// user.setCreatedBy(new User());
		User newUser = repository.save(user);
		log.info("User " + newUser.getName() + " was created");
		return newUser;
	}

	/**
	 * This method updates stored User in MySql DB on ID field.
	 *
	 * @param com.jsmart.yoda.users.base.User.
	 *
	 * @see com.jsmart.yoda.users.base.User.
	 *
	 * @return updated User or null if user not found.
	 */
	@Override
	public User update(int id, User user) {

		log.info("Updating user with id " + id);

		User userOld = repository.findOne(id);

		if (Optional.ofNullable(userOld).isPresent()) {

			user.setCreatedAt(userOld.getCreatedAt());
			user.setCreatedBy(userOld.getCreatedBy());
			user.setModifiedAt(new Date());
			// TODO when authentication will be done
			// user.setModifiedBy(new User());

			// TODO Make refactoring with Optional
			if (user.getName() == null) {
				user.setName(userOld.getName());
			}
			if (user.getEmail() == null) {
				user.setEmail(userOld.getEmail());
			}
			if (user.getPassword() == null) {
				user.setPassword(userOld.getPassword());
			}
			/////////////////////////////////////

			User updatedUser = repository.save(user);
			log.info("User with id " + id + " was updated");

			updatedUser.setUrlInfos(urlInfoClient.get(id));

			return updatedUser;
		} else {
			log.info("User with id " + id + " was not found");
			return null;
		}
	}

	/**
	 * This method removes stored User and their UrlInfo from DB.
	 *
	 * @return boolean, in successes case - when all records from USER and URLINFO DB tables removed true,
	 *          else false.
	 */
	@Override
	public boolean remove(int id) {

		log.info("Fetching user with id " + id);
		if (repository.exists(id)) {

			if (urlInfoClient.remove(id)) {
				repository.delete(id);
				log.info("User with id " + id + " was deleted");
				return true;
			} else {
				log.info("Unable to delete. Some problems with remove urlInfos for User with id " + id);
				return false;
			}

		} else {
			log.info("Unable to delete. User with id " + id + " not found");
			return false;
		}
	}

	/**
	 * This method removes all stored Users and theirs UrlInfo from DB.
	 *
	 * @return boolean, in successes case - when all records from USER and URLINFO DB tables removed true,
	 *          else false.
	 */
	@Override
	public boolean removeAll() {

		log.info("Deleting all users and urlInfos");

		if (urlInfoClient.removeAll()) {
			repository.deleteAll();
			log.info("All users were deleted");
			return true;
		} else {
			log.info("Unable to delete. Some problems with remove all urlInfos");
			return false;
		}

	}

}
