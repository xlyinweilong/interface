package com.yinweilong.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yinweilong.entity.Project;
import com.yinweilong.interceptor.AccessRequired;
import com.yinweilong.interceptor.SerializedField;
import com.yinweilong.json.BaseJson;
import com.yinweilong.repository.*;

/**
 * REST项目接口
 * 
 * @author yin.weilong
 *
 */
@RestController
@RequestMapping(value = "/project")
public class ProjectRest {

	@Autowired
	private ProjectRepository projectRepository;

	/**
	 * 列表
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	@AccessRequired
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@SerializedField(includes = { "id", "createDate", "name", "baseUrl", "baseUrlDemo" })
	public BaseJson list(@RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
		return new BaseJson(1, projectRepository.findAll(new PageRequest(pageNumber - 1, pageSize)));
	}

	/**
	 * 批量删除
	 * 
	 * @param ids
	 * @return
	 */
	@AccessRequired
	@RequestMapping(value = "/delete", method = RequestMethod.PUT)
	public BaseJson deleted(@RequestBody String[] ids) {
		projectRepository.deleteByIdIn(ids);
		return new BaseJson(1);
	}

	/**
	 * 查看某个
	 * 
	 * @param id
	 * @return
	 */
	@AccessRequired
	@RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
	@SerializedField(includes = { "id", "createDate", "name", "baseUrl", "baseUrlDemo" })
	public BaseJson info(@PathVariable String id) {
		return new BaseJson(1, projectRepository.findOne(id));
	}

	/**
	 * 创建
	 * 
	 * @param project
	 * @return
	 */
	@AccessRequired
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@SerializedField(includes = { "id", "createDate", "name", "baseUrl", "baseUrlDemo" })
	public BaseJson create(@RequestBody Project project) {
		return new BaseJson(1, "创建成功", projectRepository.insert(project));
	}

	/**
	 * 修改
	 * 
	 * @param project
	 * @return
	 */
	@AccessRequired
	@RequestMapping(value = "/update", method = RequestMethod.PUT)
	@SerializedField(includes = { "id", "createDate", "name", "baseUrl", "baseUrlDemo" })
	public BaseJson update(@RequestBody Project project) {
		return new BaseJson(1, "修改成功", projectRepository.save(project));
	}

}
