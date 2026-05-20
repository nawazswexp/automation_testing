package com.purebi.utils;

import org.openqa.selenium.By;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Centralized repository of commonly used locators.
 * Add new public constants here and the mapping will be used
 * to replace raw selector strings in email reports with friendly names.
 */
public final class Locators {

    private Locators() { }

    // Commonly used locators (public so tests can reference them)
    public static final By PLANT_SETTINGS = By.xpath("//a[normalize-space()='PLANT SETTINGS']");
    public static final By USER_LINK = By.xpath("//a[normalize-space()='USER']");
    public static final By SPECIAL_REPORTS_LINK = By.xpath("//a[normalize-space()='SPECIAL REPORTS']");
    public static final By LOGIN_BUTTON = By.xpath("//button[normalize-space()='Log In']");
    public static final By ERROR_MESSAGE = By.xpath("//p[contains(@class,'error-message')]");
    public static final By LOGO = By.xpath("//img[@class='logo']");

    public static final By REFRESH_BUTTON = By.cssSelector("img[title='Refresh'], img[src*='refresh.png']");
    public static final By REFRESH_BUTTON_IMG = By.xpath("//img[@title='Refresh']");
    public static final By NOTIFICATIONS_BUTTON = By.xpath("//a[@title='Notifications']");
    public static final By YES_BUTTON = By.xpath("//button[normalize-space()='Yes']");
    public static final By NOTIFICATIONS_LIST_TITLE = By.cssSelector("h5.notification-item-title");
    public static final By NOTIFICATIONS_SUCCESS_BADGE = By.cssSelector("span.badge.badge-success");
    public static final By CLOSE_PANEL_BUTTON = By.cssSelector("button[title='Close'], button.btn-close-panel");

    public static final By ADD_SITE_USER = By.xpath("//a[normalize-space()='+ Add Site User']");
    public static final By FIRST_NAME_INPUT = By.xpath("//input[@placeholder='Enter First Name']");
    public static final By LAST_NAME_INPUT = By.xpath("//input[@placeholder='Enter Last Name']");
    public static final By USER_NAME_INPUT = By.xpath("//input[@placeholder='Enter User Name']");
    public static final By EMAIL_INPUT = By.xpath("//input[@placeholder='Enter Email-ID']");
    public static final By PASSWORD_INPUT = By.xpath("//input[@placeholder='Enter Password']");
    public static final By ROLE_SELECT = By.xpath("//select[@name='role']");
    public static final By SAVE_BUTTON = By.xpath("//button[normalize-space()='Save']");
    public static final By UPDATE_BUTTON = By.xpath("//button[normalize-space()='Update']");
    public static final By SIDEBAR_CLOSE_SPAN = By.xpath("//span[contains(@class,'p-sidebar-close-icon') and contains(@class,'pi-times')] | //button[@class='p-ripple p-button p-component p-button-icon-only p-button-secondary p-button-rounded p-button-text']");

    public static final By DROPDOWN_SPAN = By.xpath("//span[contains(@class,'p-dropdown-trigger-icon') and contains(@class,'pi-chevron-down')]");
    public static final By DROPDOWN_DIV = By.xpath("//div[@aria-label='dropdown trigger']");
    public static final By P_OVERLAY = By.xpath("//p-overlay[contains(@class,'p-element')]");

    // Process main dashboard graph (gause)
    public static final By PROCESS_MAIN_DASHBOARD_GRAPH_GAUSE = By.xpath("//div[@class='col-md-7']//div[@id='echart']//div//canvas");

    // Special Reports locators
    public static final By SPECIAL_REPORTS_ADD_BUTTON = By.xpath("//span[@class='p-button-label ng-star-inserted']");
    public static final By DASHBOARD_NAME_INPUT = By.xpath("//input[@id='dashboardName']");
    public static final By REPORT_TYPE_FILTER_SELECT = By.xpath("//p-select[@id='reportTypeFilter']");
    public static final By DESCRIPTION_TEXTAREA = By.xpath("//textarea[@id='description']");
    public static final By SINGLE_LAYOUT = By.xpath("//div[@class='layout-preview rows-1 cols-1']//span[@class='ng-star-inserted']");
    // Accordion header locator (old p-accordion-header tag removed for PrimeNG compatibility)
    public static final By REPORT_TITLE_INPUT = By.xpath("//input[@id='reportTitle']");
    // Use stable attributes instead of dynamic pn_id_* ids
    public static final By REPORT_SECTION_TYPE_SELECT = By.xpath("//p-select[@placeholder='Select Report Type']");
    public static final By TREESELECT_CONTAINER = By.xpath("//div[contains(@class,'p-treeselect-label-container')]");
    public static final By TREE_NODE_PROCESS = By.xpath("//li[@aria-label='Process']//span[@class='p-tree-node-label']");
    public static final By TREE_NODE_COMPLETE_SYSTEM = By.xpath("//span[contains(text(),'Complete System')]");
    public static final By PARAM_ETP_PERMEATE = By.xpath("//span[contains(text(),'ETP permeate Flow(S)(004)')]");
    public static final By PARAM_ETP_RO_IL = By.xpath("//span[contains(text(),'ETP RO I/L Flow(S)(003)')]");
    public static final By PARAM_ETP_RO_RECOVERY = By.xpath("//span[contains(text(),'ETP RO Recovery(S)(0016)')]");
    public static final By OPTION_PROCESS = By.xpath("//li[normalize-space()='PROCESS']");
    public static final By OPTION_BAR = By.xpath("//li[normalize-space()='Bar']");
    public static final By OPTION_LINE = By.xpath("//li[normalize-space()='Line']");
    public static final By OPTION_DOT = By.xpath("//li[normalize-space()='Dot']");
    public static final By OPTION_BAR_LABEL = By.xpath("//li[normalize-space()='Bar Label']");
    public static final By OPTION_LINE_LABEL = By.xpath("//li[normalize-space()='Line Label']");
    public static final By OPTION_TABLE = By.xpath("//li[normalize-space()='Table']");
    public static final By OPTION_NUMERIC = By.xpath("//li[normalize-space()='Numeric']");
    public static final By OPTION_GAUGE = By.xpath("//li[normalize-space()='Gauge']");
    public static final By OPTION_PIE = By.xpath("//li[normalize-space()='Pie']");
    public static final By OPTION_DONUT = By.xpath("//li[normalize-space()='Donut']");
    public static final By OPTION_BOX = By.xpath("//li[normalize-space()='Box']");
    public static final By OPTION_TEXT = By.xpath("//li[normalize-space()='Text']");
    public static final By ADD_SECTION_SPAN = By.xpath("//span[normalize-space()='Add Section']");
    public static final By SAVE_DASHBOARD_SPAN = By.xpath("//span[normalize-space()='Save Dashboard']");
    // NOTE: `NOTIFICATIONS_BUTTON` and `YES_BUTTON` are defined earlier in this file
    // to keep common locators grouped near the top. Avoid redeclaring them here.
    public static final By DELETED_TOAST = By.xpath("//div[contains(@class,'p-toast-detail') and normalize-space()='User deleted successfully']");
    public static final By UPDATED_TOAST = By.xpath("//div[@role='alert']//div[contains(@class,'p-toast-detail') and contains(.,'updated successfully')]");
    public static final By TOAST_DETAIL = By.cssSelector("div.p-toast-detail");

    // Flexible/variant locators used in edit/create flows
    public static final By FIRST_NAME_VARIANTS = By.xpath("//input[contains(@placeholder,'First') or @placeholder='First Name' or contains(@placeholder,'Enter First')]");
    public static final By FIRST_NAME_EXACT = By.xpath("//input[@placeholder='First Name']");
    public static final By LAST_NAME_EXACT = By.xpath("//input[@placeholder='Last Name']");
    public static final By USER_NAME_EXACT = By.xpath("//input[@placeholder='User Name']");
    public static final By PASSWORD_EXACT = By.xpath("//input[@placeholder='Password']");
    public static final By IMAGE_UPLOAD_INPUT = By.xpath("//div[@class='form-group']//input[@id='imageUpload']");

    // Mapping of common selector strings -> friendly names used in reports
    private static final Map<String, String> SELECTOR_TO_NAME;

    static {
        Map<String, String> m = new HashMap<>();
        m.put("//a[normalize-space()='PLANT SETTINGS']", "PLANT_SETTINGS");
        m.put("//a[normalize-space()='USER']", "USER_LINK");
        m.put("//a[normalize-space()='SPECIAL REPORTS']", "SPECIAL_REPORTS_LINK");
        m.put("//button[normalize-space()='Log In']", "LOGIN_BUTTON");
        m.put("//p[contains(@class,'error-message')]", "ERROR_MESSAGE");
        m.put("//img[@class='logo']", "LOGO");
        m.put("img[title='Refresh'], img[src*='refresh.png']", "REFRESH_BUTTON");
        m.put("//img[@title='Refresh']", "REFRESH_BUTTON_IMG");
        m.put("//a[@title='Notifications']", "NOTIFICATIONS_BUTTON");
        m.put("h5.notification-item-title", "NOTIFICATIONS_LIST_TITLE");
        m.put("span.badge.badge-success", "NOTIFICATIONS_SUCCESS_BADGE");
        m.put("button[title='Close'], button.btn-close-panel", "CLOSE_PANEL_BUTTON");
        m.put("//a[normalize-space()='+ Add Site User']", "ADD_SITE_USER");
        m.put("//input[@placeholder='Enter First Name']", "FIRST_NAME_INPUT");
        m.put("//input[@placeholder='Enter Last Name']", "LAST_NAME_INPUT");
        m.put("//input[@placeholder='Enter User Name']", "USER_NAME_INPUT");
        m.put("//input[@placeholder='Enter Email-ID']", "EMAIL_INPUT");
        m.put("//input[@placeholder='Enter Password']", "PASSWORD_INPUT");
        m.put("//select[@name='role']", "ROLE_SELECT");
        m.put("//button[normalize-space()='Save']", "SAVE_BUTTON");
        m.put("//button[normalize-space()='Update']", "UPDATE_BUTTON");
        m.put("//span[contains(@class,'p-sidebar-close-icon') and contains(@class,'pi-times')]", "SIDEBAR_CLOSE_SPAN");
        m.put("//button[@class='p-ripple p-button p-component p-button-icon-only p-button-secondary p-button-rounded p-button-text']", "SIDEBAR_CLOSE_SPAN");
        m.put("//span[contains(@class,'p-dropdown-trigger-icon') and contains(@class,'pi-chevron-down')]", "DROPDOWN_SPAN");
        m.put("//div[@aria-label='dropdown trigger']", "DROPDOWN_DIV");
        m.put("//p-overlay[contains(@class,'p-element')]", "P_OVERLAY");
        m.put("//div[@class='col-md-7']//div[@id='echart']//div//canvas", "PROCESS_MAIN_DASHBOARD_GRAPH_GAUSE");
        m.put("//button[normalize-space()='Yes']", "YES_BUTTON");
        m.put("//div[contains(@class,'p-toast-detail') and normalize-space()='User deleted successfully']", "DELETED_TOAST");
        m.put("//div[@role='alert']//div[contains(@class,'p-toast-detail') and contains(.,'updated successfully')]", "UPDATED_TOAST");

        SELECTOR_TO_NAME = Collections.unmodifiableMap(m);
    }

    /**
     * Replace any known selector strings in the provided HTML with the friendly name.
     * This helps email reports show meaningful names instead of raw selector text.
     */
    public static String replaceSelectorsWithNames(String html) {
        if (html == null || html.isEmpty()) return html;
        String out = html;
        for (Map.Entry<String, String> e : SELECTOR_TO_NAME.entrySet()) {
            String selector = e.getKey();
            String name = e.getValue();
            // Replace both raw occurrences and occurrences that appear after "By.xpath: " or "By.cssSelector: "
            out = out.replace(selector, name + " (" + selector + ")");
            out = out.replace("By.xpath: " + selector, "By.name: " + name);
            out = out.replace("By.cssSelector: " + selector, "By.name: " + name);
        }
        return out;
    }
}
