package com.jason.boot.filter;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import org.apache.ibatis.javassist.ClassPool;
import org.apache.ibatis.javassist.CtClass;
import org.apache.ibatis.javassist.CtMethod;
import org.apache.ibatis.javassist.NotFoundException;
import org.apache.ibatis.javassist.bytecode.CodeAttribute;
import org.apache.ibatis.javassist.bytecode.LocalVariableAttribute;
import org.apache.ibatis.javassist.bytecode.MethodInfo;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.jason.boot.annotation.CheckNullIgnore;
import com.jason.boot.entity.Result;

/**
 * 参数校验拦截aop
 * 
 * @author jason
 */
@Order(6) // 因为系统还有日志aop，这里的@Order用于指定aop的执行顺序，值越小，越早执行
@Aspect
@Component
public class CheckAop {
	private static final Logger log = LoggerFactory.getLogger(LogAop.class);

	@Pointcut("@annotation(com.jason.boot.annotation.CheckNull)")
	public void checkPointCut() {
	};

	@Around(value = "checkPointCut()")
	public Object doCheck(ProceedingJoinPoint joinPoint) {
		try {
			// 类名 com.jason.boot.controller.UserSenController
			Class<? extends Object> targetClazz = joinPoint.getTarget().getClass();
			// 方法名称
			String methodName = joinPoint.getSignature().getName();
			// 参数列表数组
			Object[] args = joinPoint.getArgs();
			// 通过反射机制 获取被切参数名以及参数值
			Map<String, Object> nameAndArgs = getFieldsName(this.getClass(), targetClazz.getName(), methodName, args);// 获取被切参数名称及参数值
			log.info("参数列表" + nameAndArgs);
			// 遍历参数列表进行校验
			for (Map.Entry<String, Object> entry : nameAndArgs.entrySet()) {
				// 判断是否为null
				if (null == entry.getValue()) {
					return new Result<Object>(Result.PARAMNULL_CODE, "【" + entry.getKey() + "】 is null");
				}
				// 如果是String类型字符串，继续判断是否为“”
				if (entry.getValue() instanceof String && "".equals(((String) entry.getValue()).trim())) {
					return new Result<Object>(Result.PARAMNULL_CODE, "【" + entry.getKey() + "】 is null");
				}
			}
			return joinPoint.proceed();
		} catch (Throwable e) {
			e.printStackTrace();
			return new Result<Object>(Result.ERROR_CODE, Result.ERROR_MSG);
		}

	}

	/**
	 * 通过反射机制 获取被切参数名以及参数值
	 * 
	 * @param cls	当前类Class
	 * @param clazzName	目标方法所在类名称
	 * @param methodName	目标方法名称
	 * @param args	aop拦截到的参数列表
	 * @return
	 * @throws NotFoundException
	 * @throws ClassNotFoundException
	 */
	private Map<String, Object> getFieldsName(Class<? extends CheckAop> cls, String clazzName, String methodName,
			Object[] args) throws NotFoundException, ClassNotFoundException {
		Map<String, Object> map = new HashMap<String, Object>();
		//这里用Javassist动态类库，它类似于jdk的反射功能，但比反射功能更强大。此处表现在：
		//当Java虚拟机加载.class文件后，会将类方法“去名称化”，即丢弃掉方法形参的参数名，
		//而是用形参的序列号来传递参数。如果要通过Java反射获取参数的参数名，则必须在编辑时
		//指定“保留参数名称”。Javassist则不存在这个问题，对于任意方法，都能正确的获取其参数的参数名。
		ClassPool pool = ClassPool.getDefault();
		//获取目标方法所在类
		CtClass cc = pool.get(clazzName);
		//获取目标方法
		CtMethod cm = cc.getDeclaredMethod(methodName);
		//判断方法上是否被@CheckNullIgnore注解，如果有，获取该注解的值，也就是要忽略非空校验的参数名数组
		CheckNullIgnore ignore = (CheckNullIgnore) cm.getAnnotation(CheckNullIgnore.class);
		String[] ignoreNames = null == ignore ? null : ignore.value();
		//解析该方法的参数
		MethodInfo methodInfo = cm.getMethodInfo();
		CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
		LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
		if (attr == null) {
			throw new RuntimeException("解析参数异常");
		}
		int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
		//循环获取参数列表的参数名，并与忽略校验的参数名（ignoreNames[]）进行比对，若不忽略，则添加到结果集中返回
		for (int i = 0; i < cm.getParameterTypes().length; i++) {
			boolean flag = true;
			String name = attr.variableName(i + pos);
			if (null != ignoreNames && ignoreNames.length > 0) {
				for (int j = 0; j < ignoreNames.length; j++) {
					if (ignoreNames[j].equals(name)) {
						flag = false;
					}
				}
			}
			if (flag) {
				map.put(name, args[i]);
			}
		}
		return map;
	}

}
