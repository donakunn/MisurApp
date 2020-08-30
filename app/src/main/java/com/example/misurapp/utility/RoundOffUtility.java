package com.example.misurapp.utility;

/**
 * class that provides a utility that truncates decimal places to the second digit
 */
public class RoundOffUtility {

    /**
     * utility that truncates decimal places to the second digit
     * @param measure initial float number
     * @return truncated number
     */
    public static double roundOffNumber(float measure) {
        return Math.round(measure * 100.0) / 100.0;
    }
}
