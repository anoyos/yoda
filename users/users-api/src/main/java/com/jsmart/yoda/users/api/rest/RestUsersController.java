package com.jsmart.yoda.users.api.rest;

import com.jsmart.yoda.users.base.User;
import com.jsmart.yoda.users.base.service.UsersService;
import com.jsmart.yoda.users.api.UsersController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Sergey Khomich
 *
 *         This class covers REST API for Users operations routine.
 */
@RestController
@RequestMapping(value = "/api")
public class RestUsersController implements UsersController {

	@Autowired
	private UsersService usersService;

	/**
	 * This method creates JSON response on http GET /api/{id} request with
	 * Users id in the path and return User.
	 *
	 * @param id
	 *            Int id representation.
	 *
	 * @return JSON HTTP ResponseEntity with stored User in the body and
	 *         HttpStatus OK status in successes case. In other case return JSON
	 *         HTTP ResponseEntity with stored User id in the body and HttpStatus
	 *         NOT_FOUND.
	 */
	@Override
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<User> get(@PathVariable("id") int id) {
		User user = usersService.get(id);
		if (Optional.ofNullable(user).isPresent()) {
			return new ResponseEntity<User>(user, new HttpHeaders(), HttpStatus.OK);
		} else {
			return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * This method creates JSON response on http GET /api request and return all
	 * stored Users.
	 *
	 * @return JSON HTTP ResponseEntity with list of all stored Users and
	 *         HttpStatus OK status in successes case. In case of empty database
	 *         return HttpStatus NOT_FOUND.
	 */
	@Override
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<List<User>> getAll() {
		List<User> users = new ArrayList<User>();
		users = usersService.getAll();
		if (Optional.ofNullable(users).isPresent()) {
			return new ResponseEntity<List<User>>(users, new HttpHeaders(), HttpStatus.OK);
		} else {
			return new ResponseEntity<List<User>>(HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * This method creates JSON response on http POST /api request
	 * with User object in the body.
	 *
	 * @param User
	 *            User with completed Name, Email, Password String fields.
	 *
	 * @return JSON HTTP ResponseEntity with new instance of User in the body and
	 *         HttpStatus CREATED in successes case. In other case return
	 *         HttpStatus INTERNAL_SERVER_ERROR / BAD_REQUEST.
	 */
	@Override
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<User> create(@RequestBody User user, UriComponentsBuilder ucBuilder) {

		if (Optional.ofNullable(user).isPresent()) {
			User fullUser = usersService.create(user);
			HttpHeaders headers = new HttpHeaders();
			if (Optional.ofNullable(fullUser).isPresent())  {
				headers.setLocation(ucBuilder.path("/api/{id}").buildAndExpand(fullUser.getId()).toUri());
				return new ResponseEntity<User>(fullUser, headers, HttpStatus.CREATED);
			} else {
				return new ResponseEntity<User>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
		}

	}

	/**
	 * This method creates JSON response on http Put /api/{id} request with
	 * User object in the body and try to update it base on id field.
	 *
	 * @param User
	 *            Complete User representation.
	 *
	 * @return JSON HTTP ResponseEntity with updated User in the body and
	 *         HttpStatus OK in successes case. In other case return JSON HTTP
	 *         ResponseEntity with User in the body and HttpStatus NOT_FOUND or
	 *         HttpStatus BAD_REQUEST in case of empty longUrl or shortUrl.
	 */
	@Override
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<User> update(@PathVariable("id") int id, @RequestBody User  user) {
		if ((Optional.ofNullable(user).isPresent()) && (id > 0) && (user.getId() == id)) {
			User upDatedUser = usersService.update(id, user);
			if (Optional.ofNullable(upDatedUser).isPresent()) {
				return new ResponseEntity<User>(upDatedUser, new HttpHeaders(), HttpStatus.OK);
			} else {
				return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
			}
		} else {
			return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * This method creates response on http Delete /api/{id} request with
	 * User id in the path and try to delete User object with same User's id.
	 *
	 * @param id
	 *            int User id.
	 *
	 * @return HTTP ResponseEntity with HttpStatus NO_CONTENT in successes case.
	 *         In other case return HttpStatus NOT_FOUND.
	 */
	@Override
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> remove(@PathVariable("id") int id) {
		if (!usersService.remove(id)) {
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		}
	}

	/**
	 * This method creates response on http Delete /api/ request and try to
	 * delete stored Users.
	 *
	 * @return HTTP ResponseEntity with HttpStatus OK in successes case. In
	 *         other case return HttpStatus NO_CONTENT.
	 */
	@Override
	@RequestMapping(method = RequestMethod.DELETE)
	public ResponseEntity<Void> removeAll() {
		if (!usersService.removeAll()) {
			return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		}
	}

}
