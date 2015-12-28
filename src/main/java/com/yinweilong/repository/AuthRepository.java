package com.yinweilong.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.yinweilong.entity.Auth;

public interface AuthRepository extends MongoRepository<Auth, String> {

	Page<Auth> findAll(Pageable pageable);
}
