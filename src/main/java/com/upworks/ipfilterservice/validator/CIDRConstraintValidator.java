/**
 *
 */
package com.upworks.ipfilterservice.validator;

import org.apache.commons.net.util.SubnetUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class CIDRConstraintValidator implements ConstraintValidator<CIDR, String> {

    @Override
    public boolean isValid(String cidr, ConstraintValidatorContext context) {
        try {
            new SubnetUtils(cidr);
        } catch (IllegalArgumentException iae) {
            return false;
        }

        return true;
    }
}
