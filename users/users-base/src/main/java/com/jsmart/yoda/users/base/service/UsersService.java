package com.jsmart.yoda.users.base.service;

import com.jsmart.yoda.users.base.User;

import java.util.List;

/**
 * @author Sergey Khomich
 *
 */
public interface UsersService {

	public User get(int id);

	public List<User> getAll();

	public User create(User user);

	public User update(int id, User user);

	public boolean remove(int id);

	public boolean removeAll();

}
