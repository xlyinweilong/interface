package com.yinweilong.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.yinweilong.entity.Role;

public interface RoleRepository extends MongoRepository<Role, String> {

	Page<Role> findAll(Pageable pageable);
}
