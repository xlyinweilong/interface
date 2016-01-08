package com.yinweilong.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.yinweilong.entity.Auth;

public interface AuthRepository extends MongoRepository<Auth, String> {

	List<Auth> findByTypeIn(List<String> types);

	void deleteByIdIn(String[] ids);

	Auth findByClassNameAndMethodName(String className, String methodName);

}
