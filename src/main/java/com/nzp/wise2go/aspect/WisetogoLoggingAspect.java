package com.nzp.wise2go.aspect;

import java.util.logging.Logger;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class WisetogoLoggingAspect {

	// setup logger
	private Logger myLogger = Logger.getLogger(getClass().getName());
	

	@Pointcut("execution(* com.nzp.wise2go.web.BillingSummaryController.savePayment(..))")
	private void forBillingSummary() {
	
	}
	 

	@Pointcut("execution(* com.nzp.wise2go.web.ExpenseController.saveExpense(..))")
	private void forExpense() {
		
	}
	 

	@Pointcut("execution(* com.nzp.wise2go.web.ExpenseController.saveCustomer(..))")
	private void forCustomer() {
		System.err.println("------------> Ras");
	}
	
	 
	@Pointcut("forBillingSummary() || forExpense() || forCustomer()")
	private void forAppFlow() {}
	
	// add @Before advice
	@Before("forAppFlow()")
	public void before(JoinPoint theJoinPoint) {
		
		// display method we are calling
		String theMethod = theJoinPoint.getSignature().toShortString();
		myLogger.info("=====>> in @Before: calling method: " + theMethod);
		
		// display the arguments to the method
		
		// get the arguments
		Object[] args = theJoinPoint.getArgs();
		
		// loop thru and display args
		for (Object tempArg : args) {
			myLogger.info("=====>> argument: " + tempArg);
		}
		
	}
	
	
	// add @AfterReturning advice
	@AfterReturning(
			pointcut="forAppFlow()",
			returning="theResult"
			)
	public void afterReturning(JoinPoint theJoinPoint, Object theResult) {
	
		// display method we are returning from
		String theMethod = theJoinPoint.getSignature().toShortString();
		myLogger.info("=====>> in @AfterReturning: from method: " + theMethod);
				
		// display data returned
		myLogger.info("=====>> result: " + theResult);
	
	}
	

	
}
