package com.fivault.fivault.service.aspect;


import com.fivault.fivault.service.exception.CustomException;
import com.fivault.fivault.service.exception.ErrorCode;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.*;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class DatabaseExceptionHandlingAspect {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseExceptionHandlingAspect.class);

    @Around("execution(* com.fivault.fivault.service..*(..))")
    public Object handleDatabaseExceptions(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();

        try {
            return joinPoint.proceed();

        } catch (DuplicateKeyException e) {
            logger.warn("Duplicate key violation in {}: {}", methodName, e.getMessage());
            throw new CustomException(ErrorCode.DB_DUPLICATE_KEY, e);

        } catch (DataIntegrityViolationException e) {
            logger.error("Data integrity violation in {}: {}", methodName, e.getMessage());
            throw new CustomException(ErrorCode.DB_INTEGRITY_VIOLATION, e);

        } catch (QueryTimeoutException e) {
            logger.error("Database timeout in {}: {}", methodName, e.getMessage());
            throw new CustomException(ErrorCode.DB_TIMEOUT, e);

        } catch (DataAccessResourceFailureException e) {
            logger.error("Database connection error in {}: {}", methodName, e.getMessage(), e);
            throw new CustomException(ErrorCode.DB_CONNECTION_ERROR, e);

        } catch (DataAccessException e) {
            logger.error("Database error in {}: {}", methodName, e.getMessage(), e);
            throw new CustomException(ErrorCode.DB_OPERATION_FAILED, e);
        }
    }
}