package com.yinweilong.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.yinweilong.entity.Interface;

public interface InterfaceRepository extends MongoRepository<Interface, String> {

	Page<Interface> findByProjectId(String projectId, Pageable pageable);

	void deleteByIdIn(String[] ids);

}
