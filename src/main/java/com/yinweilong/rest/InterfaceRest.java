package com.yinweilong.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yinweilong.entity.Interface;
import com.yinweilong.interceptor.AccessRequired;
import com.yinweilong.interceptor.SerializedField;
import com.yinweilong.json.BaseJson;
import com.yinweilong.repository.*;
import com.yinweilong.support.Tools;

/**
 * REST接口接口
 * 
 * @author yin.weilong
 *
 */
@RestController
@RequestMapping(value = "/interface")
public class InterfaceRest {

	@Autowired
	private InterfaceRepository interfaceRepository;

	/**
	 * 列表
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	@AccessRequired
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@SerializedField(includes = { "id", "createDate", "name", "projectId", "url", "methodType", "consumption", "parameters", "returnJson" })
	public BaseJson list(@CookieValue(value = "projectId") String projectId, @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
		return Tools.isBlank(projectId) ? new BaseJson(1, interfaceRepository.findAll(new PageRequest(pageNumber - 1, pageSize))) : new BaseJson(1, interfaceRepository.findByProjectId(projectId, new PageRequest(pageNumber - 1, pageSize)));
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
		interfaceRepository.deleteByIdIn(ids);
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
	@SerializedField(includes = { "id", "createDate", "name", "projectId", "url", "methodType", "consumption", "parameters", "returnJson" })
	public BaseJson info(@PathVariable String id) {
		return new BaseJson(1, interfaceRepository.findOne(id));
	}

	/**
	 * 创建
	 * 
	 * @param user
	 * @return
	 */
	@AccessRequired
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@SerializedField(includes = { "id", "createDate", "name", "projectId", "url", "methodType", "consumption", "parameters", "returnJson" })
	public BaseJson create(@RequestBody Interface i) {
		return new BaseJson(1, "创建成功", interfaceRepository.insert(i));
	}

	/**
	 * 修改
	 * 
	 * @param user
	 * @return
	 */
	@AccessRequired
	@RequestMapping(value = "/update", method = RequestMethod.PUT)
	@SerializedField(includes = { "id", "createDate", "name", "projectId", "url", "methodType", "consumption", "parameters", "returnJson" })
	public BaseJson update(@RequestBody Interface i) {
		return new BaseJson(1, "修改成功", interfaceRepository.save(i));
	}

}
