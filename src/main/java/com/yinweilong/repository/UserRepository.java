package com.yinweilong.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.yinweilong.entity.User;

public interface UserRepository extends MongoRepository<User, String> {

	List<User> findByName(String name);

	long countByroleIdIn(String[] roleIds);

	User findByAccount(String account);

	User findByAccessToken(String accessToken);
	
	void deleteByIdIn(String[] ids);

	@Query("{'type' : 'USER'}")
	Page<User> findAll(Pageable pageable);

}
