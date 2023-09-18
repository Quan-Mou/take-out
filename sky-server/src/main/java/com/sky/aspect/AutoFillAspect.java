package com.sky.aspect;


import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自动填充-对insert、update的sql填充createtime、updatetime、createUser、updateUser的值
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect { // 切面：= 切点+通知
    @Pointcut("execution(public * com.sky.mapper..*(..))") //  切点
    public void insertAndUpdateMethod(){};

    @Before("insertAndUpdateMethod()") // 通知：要增强的代码
    public void autoFill(JoinPoint joinPoint) {

//     获取方法、只获取方法上标注了AutoFill注解的方法，然后根据注解里面的值判断如何填充（注解值为INSERT、UPDATE）。
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//        判断方法是否存在AutoFill注解
        if (!signature.getMethod().isAnnotationPresent(AutoFill.class)) {
            return;
        }
        AutoFill annotation = signature.getMethod().getAnnotation(AutoFill.class);
        String value = annotation.value(); // 获取到注解的值
        Object arg = joinPoint.getArgs()[0];// 约定好第一个参数为实体类
        LocalDateTime currentDate = LocalDateTime.now();
        Long user = BaseContext.getCurrentId();
        if("UPDATE".equals(value)) {
            try {
//              获取方法声明：第一个参数是方法名，第二个参数是方法参数类型
                Method setUpdateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, currentDate.getClass());
                Method setUpdateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, user.getClass());
//              调用方法:第一个参数是调用谁的方法，第二个参数是方法的参数值
                setUpdateTime.invoke(arg,currentDate);
                setUpdateUser.invoke(arg,user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if("INSERT".equals(value)) {
            try {
//              获取方法声明：第一个参数是方法名，第二个参数是方法参数类型
                Method setUpdateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, currentDate.getClass());
                Method setUpdateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, user.getClass());
                Method setCreateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, currentDate.getClass());
                Method setCreateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, user.getClass());
//              调用方法:第一个参数是调用谁的方法，第二个参数是方法的参数值
                setUpdateTime.invoke(arg,currentDate);
                setUpdateUser.invoke(arg,user);
                setCreateTime.invoke(arg,currentDate);
                setCreateUser.invoke(arg,user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



}
