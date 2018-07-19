package com.jason.boot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jason.boot.annotation.CheckNull;
import com.jason.boot.annotation.CheckNullIgnore;
import com.jason.boot.entity.Result;
import com.jason.boot.entity.UserSen;
import com.jason.boot.service.UserSenService;
import com.jason.boot.utils.StringUtil;
/**
 * 非空校验测试
 * @author jason
 *
 */
@RestController
@RequestMapping("users")

public class UserSenController {
	/**
	 * 测试非空校验aop
	 * @param name
	 * @param age
	 * @return
	 */
	@CheckNull//该方法入参要做非空校验
	@CheckNullIgnore({"age"})//其中，参数【age】不做校验
	@RequestMapping(value="test",method=RequestMethod.POST)
	public Result<Object> test(String name,String age){
		System.out.println("进入了test");
		System.out.println(name+","+"age");
		return new Result<Object>(Result.SUCCESS_CODE,Result.SUCCESS_MSG);
	}
}
