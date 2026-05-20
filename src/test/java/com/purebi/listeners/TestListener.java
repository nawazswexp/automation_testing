package com.purebi.listeners;

import org.testng.ISuite;
import org.testng.ISuiteListener;
import com.purebi.utils.EmailUtil;

public class TestListener implements ISuiteListener {

    @Override
    public void onFinish(ISuite suite) {
        System.out.println("📧 ISuiteListener onFinish() called - attempting to send report");
        try {
            // Do not send reports directly from the listener; the Maven exec plugin
            // handles sending at the end of the build lifecycle to ensure reports
            // are fully written and available on disk.
            System.out.println("📧 Test suite finished; report send is handled by the build lifecycle");
        } catch (Exception e) {
            System.err.println("❌ Failed to send report from TestListener: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
