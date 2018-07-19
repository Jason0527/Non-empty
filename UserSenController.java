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
	/**
	 * 获取用户列表
	 * @return
	 */
	@RequestMapping(method=RequestMethod.GET)
	public Result<List<UserSen>> getUserList(){
		Result<List<UserSen>> result = new Result<List<UserSen>>(Result.SUCCESS_CODE,Result.SUCCESS_MSG,service.getUserList(new UserSen()));
		return result;
	}
	/**
	 * 增加用户（注册）
	 * @param user
	 * @return
	 */
	@RequestMapping(method=RequestMethod.POST)
	public Result<Object> postUser(UserSen user){
		user.setId(StringUtil.createUUID());
		service.postUser(user);
		return new Result<Object>(Result.SUCCESS_CODE,Result.SUCCESS_MSG);
	}
	/**
	 * 根据用户编号得到某个用户
	 * @param userNo
	 * @return
	 */
	@RequestMapping(value="/{userNo}",method=RequestMethod.GET)
	public Result<List<UserSen>> getUser(@PathVariable("userNo") String userNo){
		UserSen user = new UserSen();
		user.setUserNo(userNo);
		List<UserSen> list = service.getUserList(user);
		return new Result<List<UserSen>>(Result.SUCCESS_CODE,Result.SUCCESS_MSG,list);
	}
	/**
	 * 根据用户编号修改用户
	 * 这里请求提交方式为put，spring无法接收put传参，但提供了一个httpPutFormContentFilter过滤器,由于springboot启动时会自动加载这个过滤器，
	 * 所以专门配置过滤器（若是springmvc 则需在web.xml中配置该过滤器），需要将前台表单的提交方式设置为 x-www-form-urlencoded，才能获取都参数
	 * @param userNo
	 * @param user
	 * @return
	 */
	@RequestMapping(value="/{userNo}",method=RequestMethod.PUT)
	public Result<Object> putUser(@PathVariable("userNo") String userNo, UserSen user){
		UserSen us  = new UserSen();
		us.setUserNo(userNo);
		UserSen u = service.getUserList(us).get(0);
		u.setName(user.getName());
		u.setAge(user.getAge());
		u.setPassword(user.getPassword());
		service.putUser(userNo,u);
		return new Result<Object>(Result.SUCCESS_CODE,Result.SUCCESS_MSG);
	}
	/**
	 * 删除某用户（物理删除）
	 * @param userNo
	 * @return
	 */
	@RequestMapping(value="/{userNo}",method=RequestMethod.DELETE)
	public Result<Object> deleteUser(@PathVariable("userNo") String userNo){
		service.deleteUser(userNo);
		return new Result<Object>(Result.SUCCESS_CODE,Result.SUCCESS_MSG);
	}
	
	/**
	 * 测试非空校验aop
	 * @param name
	 * @param age
	 * @return
	 */
	@CheckNull
	@RequestMapping(value="test",method=RequestMethod.POST)
	public Result<Object> test(String name,String age){
		System.out.println("进入了test");
		System.out.println(name+","+"age");
		return new Result<Object>(Result.SUCCESS_CODE,Result.SUCCESS_MSG);
	}
}
