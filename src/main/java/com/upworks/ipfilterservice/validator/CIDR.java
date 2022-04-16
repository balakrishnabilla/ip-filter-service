/**
 *
 */
package com.upworks.ipfilterservice.validator;

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
@Target({FIELD, LOCAL_VARIABLE})
@Constraint(validatedBy = CIDRConstraintValidator.class)
public @interface CIDR {
    String message() default "Must be of the format IPV4 CIDR block";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
