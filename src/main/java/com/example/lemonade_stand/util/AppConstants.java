package com.example.lemonade_stand.util;

import java.util.Arrays;
import java.util.List;

/**
 * A class to hold application-wide constants.
 */
public final class AppConstants {

    // Prevent instantiation
    private AppConstants() {
        throw new UnsupportedOperationException("Cannot instantiate a constants class.");
    }

    // Cost of a single lemonade
    public static final int LEMONADE_COST = 5;
    public static final List<Integer> BILL_DENOMINATIONS = Arrays.asList(5, 10, 20);

}
