package com.jsmart.yoda.users.api;

import com.jsmart.yoda.users.base.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * @author Sergey Khomich
 *
 */
public interface UsersController {

	public ResponseEntity<User> get(int id);

	public ResponseEntity<List<User>> getAll();

	public ResponseEntity<User> create(User user, UriComponentsBuilder ucBuilder);

	public ResponseEntity<User> update(int id, User user);

	public ResponseEntity<Void> remove(int id);

	public ResponseEntity<Void> removeAll();

}
