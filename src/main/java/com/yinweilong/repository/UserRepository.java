package com.yinweilong.repository;

import java.util.List;

import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.yinweilong.entity.User;

public interface UserRepository extends MongoRepository<User, String> {

	List<User> findByName(String name);

	User findByAccount(String account);

	User findByAccessToken(String accessToken);

	@Query("{ deleted:false }")
	Page<User> findAll(Pageable pageable);

}
