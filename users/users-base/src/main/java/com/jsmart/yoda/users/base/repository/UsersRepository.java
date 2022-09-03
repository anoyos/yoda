package com.jsmart.yoda.users.base.repository;

import com.jsmart.yoda.users.base.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Sergey Khomich
 *
 * This interface represents core for MySQL database operations
 * trough bean implementation injection.
 * Methods findOne(User), findAll(), delete(User), removeAll().
 *
 */
@Repository
public interface UsersRepository extends JpaRepository<User, Integer> {
}
