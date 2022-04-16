/**
 *
 */
package com.ipfilterservice.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class IPAddressConstraintValidator implements ConstraintValidator<IPAddress, String> {
    private static final Pattern addressPattern = Pattern.compile("(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})");

    @Override
    public boolean isValid(String ip, ConstraintValidatorContext context) {
        try {
            validateIp(ip);
        } catch (IllegalArgumentException iae) {
            return false;
        }
        return true;
    }

    private static int validateIp(String address) {
        Matcher matcher = addressPattern.matcher(address);
        if (matcher.matches()) {
            return matchAddress(matcher);
        } else {
            throw new IllegalArgumentException(String.format("Could not parse [%s]", address));
        }
    }

    private static int matchAddress(Matcher matcher) {
        int addr = 0;

        for (int i = 1; i <= 4; ++i) {
            int n = rangeCheck(Integer.parseInt(matcher.group(i)), 0, 255);
            addr |= (n & 255) << 8 * (4 - i);
        }

        return addr;
    }

    private static int rangeCheck(int value, int begin, int end) {
        if (value >= begin && value <= end) {
            return value;
        } else {
            throw new IllegalArgumentException("Value [" + value + "] not in range [" + begin + "," + end + "]");
        }
    }

}
