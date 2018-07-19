package com.jason.boot.filter;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import org.apache.ibatis.javassist.ClassClassPath;
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
import com.jason.boot.entity.Result;

/**
 * 参数校验aop
 * @author jason
 */
@Order(6)	//因为系统还有日志aop，这里的@Order用于指定aop的执行顺序，值越小，越早执行
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
			// 类名  com.jason.boot.controller.UserSenController
			String className = joinPoint.getTarget().getClass().getName();
			// 方法名称
			String methodName = joinPoint.getSignature().getName();
			// 参数值数组
			Object[] args = joinPoint.getArgs();
			//通过反射机制 获取被切参数名以及参数值
			Map<Object, Object> nameAndArgs = getFieldsName(this.getClass(), className, methodName, args);// 获取被切参数名称及参数值
			log.info("参数列表" + nameAndArgs);
			for (Map.Entry<Object, Object> entry : nameAndArgs.entrySet()) {
				//判断是否为null
				if (null == entry.getValue()) {
					return new Result<Object>(Result.PARAMNULL_CODE, "【" + entry.getKey() + "】 is null");
				}
				//如果是String类型字符串，继续判断是否为“”
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
	 * @param cls
	 * @param clazzName
	 * @param methodName
	 * @param args
	 * @return
	 * @throws NotFoundException
	 */
	private Map<Object, Object> getFieldsName(Class cls, String clazzName, String methodName, Object[] args)
			throws NotFoundException {
		Map<Object, Object> map = new HashMap<Object, Object>();
		ClassPool pool = ClassPool.getDefault();
		ClassClassPath classPath = new ClassClassPath(cls);
		pool.insertClassPath(classPath);
		CtClass cc = pool.get(clazzName);
		CtMethod cm = cc.getDeclaredMethod(methodName);
		MethodInfo methodInfo = cm.getMethodInfo();
		CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
		LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
		if (attr == null) {
			throw new RuntimeException("解析参数异常");
		}
		int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
		for (int i = 0; i < cm.getParameterTypes().length; i++) {
			map.put(attr.variableName(i + pos), args[i]);// paramNames即参数名
		}
		return map;
	}
}
