package com.jason.boot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jason.boot.annotation.CheckNull;
import com.jason.boot.entity.Result;
import com.jason.boot.entity.UserSen;
import com.jason.boot.service.UserSenService;
import com.jason.boot.utils.StringUtil;
/**
 * 处理有关用户本身的操作
 * @author 裘千仞
 *
 */
@RestController
@RequestMapping("users")
@Transactional
public class UserSenController {
	@Autowired
	UserSenService service;
	@CheckNull	//此处添加非空校验注解，aop会拦截该方法进行参数校验
	@RequestMapping(value="test",method=RequestMethod.POST)
	public Result<Object> test(String name,String age){
		System.out.println("进入了test");
		System.out.println(name+","+"age");
		return new Result<Object>(Result.SUCCESS_CODE,Result.SUCCESS_MSG);
	}
}
