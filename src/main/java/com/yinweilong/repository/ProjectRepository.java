package com.yinweilong.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.yinweilong.entity.Project;

public interface ProjectRepository extends MongoRepository<Project, String> {

	List<Project> findByName(String name);

	void deleteByIdIn(String[] ids);

	Page<Project> findAll(Pageable pageable);

}
