package com.wedding.bot;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class Aspects {
	
	@Pointcut("@annotation(com.wedding.bot.NoLogging)")
	public void pointcutNoLogging() {

	}
	
	@Before("pointcutNoLogging()")
	public void beforeNoLogging(JoinPoint joinPoint) {
		log.info("{} start", getMethodName(joinPoint));
	}

	@Pointcut("execution(* com.wedding.bot.controller..*(..)) && !@annotation(com.wedding.bot.NoLogging)")
	public void pointcutController() {

	}
	
	@Before("pointcutController()")
	public void beforeController(JoinPoint joinPoint) {
		String methodName = getMethodName(joinPoint);
		log.info("{} start, args: {}", methodName, Arrays.asList(joinPoint.getArgs()));
	}
	
	@Around(value = "pointcutController() || pointcutNoLogging()")
    public Object aroundMethodController(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    	String methodName = getMethodName(proceedingJoinPoint);
    	StopWatch stopWatch = new StopWatch();
    	stopWatch.start(); 
        Object obj = proceedingJoinPoint.proceed();
        stopWatch.stop();
        
        log.info("{} done, duration: {} ms", methodName, stopWatch.getTotalTimeMillis());
        return obj;
    }
	
	@Pointcut("execution(* com.wedding.bot.service.impl..*(..)) && !@annotation(com.wedding.bot.NoLogging)")
	public void pointcutService() {

	}
	
	@Before("pointcutService()")
	public void beforeService(JoinPoint joinPoint) {
		String methodName = getMethodName(joinPoint);
		log.info("{} start, args: {}", methodName, Arrays.asList(joinPoint.getArgs()));
	}
	
    @AfterThrowing(value = "pointcutService()", throwing = "e")
    public void afterThrowingService(JoinPoint joinPoint, Exception e) {
    	String methodName = getMethodName(joinPoint);
    	log.info("{} fail, errorMsg: {}", methodName, e.getMessage());
    }
    
    @Around(value = "pointcutService()")
    public Object aroundMethodService(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    	String methodName = getMethodName(proceedingJoinPoint);
    	StopWatch stopWatch = new StopWatch();
    	stopWatch.start(); 
        Object obj = proceedingJoinPoint.proceed();
        stopWatch.stop();
        
        if (obj == null) // 如果回傳空的結果，就不印下來了
        	log.info("{} done, duration: {} ms", methodName, stopWatch.getTotalTimeMillis());
        else
        	log.info("{} done, duration: {} ms, return value: {}", methodName, stopWatch.getTotalTimeMillis(), obj.toString());
        
        return obj;
    }
	
	private static String getMethodName(JoinPoint joinPoint) {
		return joinPoint.getSignature().toShortString();
	}

}
