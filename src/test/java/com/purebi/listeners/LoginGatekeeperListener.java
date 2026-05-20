package com.purebi.listeners;

import com.purebi.utils.TestExecutionControl;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.Reporter;

public class LoginGatekeeperListener implements IInvokedMethodListener, ITestListener {

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        // If login has been determined to have failed, skip any non-login methods
        try {
            Boolean loginPassed = TestExecutionControl.getLoginPassed();
            if (loginPassed != null && loginPassed.equals(Boolean.FALSE)) {
                // Allow the login method to run (it's already failed), skip everything else
                String className = method.getTestMethod().getRealClass().getName();
                if (!className.equals("com.purebi.homepage.LoginTest")) {
                    throw new SkipException("Skipping because login failed");
                }
            }
        } catch (SkipException se) {
            Reporter.log("Skipping test due to login failure: " + method.getTestMethod().getMethodName(), true);
            throw se;
        } catch (Exception ignored) {
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        // no-op
    }

    @Override
    public void onTestStart(ITestResult result) {
        // no-op
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        // If login passed, set the flag
        try {
            String className = result.getMethod().getRealClass().getName();
            if (className.equals("com.purebi.homepage.LoginTest")) {
                TestExecutionControl.setLoginPassed(true);
                Reporter.log("Login succeeded — tests will run.", true);
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        try {
            String className = result.getMethod().getRealClass().getName();
            if (className.equals("com.purebi.homepage.LoginTest")) {
                TestExecutionControl.setLoginPassed(false);
                Reporter.log("Login failed — all tests will be skipped.", true);
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        // no-op
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        // no-op
    }

    @Override
    public void onStart(ITestContext context) {
        if (TestExecutionControl.getLoginPassed() == null) {
            TestExecutionControl.loginPassed = null;
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        // no-op
    }
}
