package com.purebi.utils;

public class Config {
    public static String getBaseUrl() {
        return System.getProperty("baseUrl", "https://test.pure.bi");
    }
}
