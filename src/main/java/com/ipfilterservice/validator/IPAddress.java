/**
 * 
 */
package com.ipfilterservice.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author vaibhav.singh
 */
@Documented
@Retention(RUNTIME)
@Target({ FIELD, LOCAL_VARIABLE })
@Constraint(validatedBy = IPAddressConstraintValidator.class)
public @interface IPAddress
{
	String message() default "Must be of the format IPv4";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
