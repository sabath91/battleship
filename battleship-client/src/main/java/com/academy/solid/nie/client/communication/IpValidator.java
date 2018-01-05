package com.academy.solid.nie.client.communication;


/**
 * Implementation of Validator interface.
 * Checks whether the given IP is in the correct format.
 *
 * @since 1.0.1
 */

public class IpValidator implements Validator {
    /**
     * Determines whether the given IP is in the correct format and does not contain invalid characters.
     * Works for IPv4.
     *
     * @param ip
     * @return true if IP is in correct format
     */
    @Override
    public boolean validate(String ip) {
        String[] splitted = ip.split("\\.");
        if (splitted.length != 4)
            return false;
        for (String s : splitted)
            if (!(s.matches("[1-9]\\d*") && Integer.parseInt(s) < 256))
                return false;
        return true;
    }
}