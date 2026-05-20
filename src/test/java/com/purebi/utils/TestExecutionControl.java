package com.purebi.utils;

public class TestExecutionControl {
    // null = unknown (before login runs), true = login passed, false = login failed
    public static volatile Boolean loginPassed = null;

    // null = unknown (before user create runs), true = user create passed, false = user create failed
    public static volatile Boolean userCreatePassed = null;
    
    // null = unknown (before user edit runs), true = user edit passed, false = user edit failed
    public static volatile Boolean userEditPassed = null;

    public static synchronized void setLoginPassed(boolean passed) {
        loginPassed = passed;
    }

    public static synchronized Boolean getLoginPassed() {
        return loginPassed;
    }

    public static synchronized void setUserCreatePassed(boolean passed) {
        userCreatePassed = passed;
    }

    public static synchronized Boolean getUserCreatePassed() {
        return userCreatePassed;
    }

    public static synchronized void setUserEditPassed(boolean passed) {
        userEditPassed = passed;
    }

    public static synchronized Boolean getUserEditPassed() {
        return userEditPassed;
    }
}
