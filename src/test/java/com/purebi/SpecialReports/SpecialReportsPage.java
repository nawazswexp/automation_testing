package com.purebi.SpecialReports;

import com.purebi.utils.Locators;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class SpecialReportsPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public SpecialReportsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    private void scrollToElement(WebElement element) {
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "let el = arguments[0];" +
                    "let parent = el;" +
                    "while (parent && parent !== document.body) {" +
                    "  const style = window.getComputedStyle(parent);" +
                    "  const overflowY = style.overflowY;" +
                    "  if (overflowY === 'auto' || overflowY === 'scroll') {" +
                    "    parent.scrollTop = el.offsetTop - parent.offsetTop - 100;" +
                    "    break;" +
                    "  }" +
                    "  parent = parent.parentElement;" +
                    "}",
                    element
            );
        } catch (Exception ignored) {}
    }

    public void safeClick(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Step 1: Wait for toast to disappear BEFORE finding element
        waitForToastToDisappear();
        waitForOverlayToDisappear();

        // Step 2: Wait for element clickable
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));

        // Step 3: Scroll to element
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});", element);

        // Step 4: Try normal click with retry
        int attempts = 0;
        while (attempts < 3) {
            try {
                element.click();
                return;
            } catch (Exception e) {
                waitForToastToDisappear();
                waitForOverlayToDisappear();
                attempts++;
                element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            }
        }

        // Step 5: JS fallback (final)
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();", element
        );
    }

    public void safeClick(WebElement element) {
        waitForToastToDisappear();
        waitForOverlayToDisappear();
        wait.until(ExpectedConditions.visibilityOf(element));
        wait.until(ExpectedConditions.elementToBeClickable(element));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", element);

        try {
            element.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].click();", element);
        }
    }

    private void pressEsc() {
        new Actions(driver).sendKeys(Keys.ESCAPE).perform();
    }

    private void clickBody() {
        try {
            safeClick(By.xpath("//body"));
        } catch (Exception ignored) {}
    }

    public void waitForOverlayToDisappear() {
        waitForToastToDisappear();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.xpath("//div[contains(@class,'p-treeselect-panel')]")
        ));

        // Handle global blockers that intercept clicks during Angular renders/transitions.
        wait.until(d -> {
            List<WebElement> blockers = d.findElements(By.cssSelector(".p-component-overlay, .loader, .block-ui"));
            for (WebElement blocker : blockers) {
                try {
                    if (blocker.isDisplayed()) {
                        return false;
                    }
                } catch (StaleElementReferenceException ignored) {
                    // If DOM re-rendered while checking, continue and re-evaluate on next poll.
                }
            }
            return true;
        });
    }

    public void waitForToastToDisappear() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.cssSelector(".p-toast-message")
        ));

        // Optional but recommended: ensure the toast container is fully gone.
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.cssSelector(".p-toast")
        ));
    }

    private void waitForOverlayInvisible() {
        try {
            waitForOverlayToDisappear();
            wait.until(ExpectedConditions.invisibilityOfElementLocated(Locators.P_OVERLAY));
        } catch (Exception ignored) {}
    }

    // ---------- Create flow helpers ----------
    public void openSpecialReports() {
        safeClick(Locators.SPECIAL_REPORTS_LINK);
        wait.until(ExpectedConditions.visibilityOfElementLocated(Locators.SPECIAL_REPORTS_ADD_BUTTON));
    }

    public void clickAddReport() {
        safeClick(Locators.SPECIAL_REPORTS_ADD_BUTTON);
        wait.until(ExpectedConditions.visibilityOfElementLocated(Locators.DASHBOARD_NAME_INPUT));
    }

    public void setDashboardName(String name) {
        WebElement e = wait.until(ExpectedConditions.visibilityOfElementLocated(Locators.DASHBOARD_NAME_INPUT));
        scrollToElement(e);
        e.clear();
        e.sendKeys(name);
        String v = e.getAttribute("value");
        if (v == null || !v.equals(name)) throw new RuntimeException("Dashboard name not set: " + name);
    }

    /**
     * Select an option from a PrimeNG `p-select` dropdown.
     * Steps:
     *  1) Click the dropdown element.
     *  2) Wait for the PrimeNG overlay panel to appear.
     *  3) Click the option inside the overlay panel.
     *  4) Wait for the overlay to disappear (handles animation).
     *
     * This method uses WebDriverWait and ExpectedConditions. It does not use Thread.sleep
     * or the Selenium `Select` class. It is safe against stale/intercepted clicks via
     * a JS fallback.
     */
    public void selectFromPrimeDropdown(By dropdownLocator, String visibleText) {
        safeClick(dropdownLocator);

        // Flexible overlay locator to support multiple PrimeNG versions
        By overlayLocator = By.xpath("//*[contains(@class,'p-select-overlay') or contains(@class,'p-dropdown-panel') or @role='listbox']");

        // Wait for an overlay to appear
        wait.until(ExpectedConditions.visibilityOfElementLocated(overlayLocator));

        // Try to click the option inside any visible overlay element
        boolean clicked = false;
        List<WebElement> overlays = driver.findElements(overlayLocator);
        for (WebElement ov : overlays) {
            try {
                List<WebElement> opts = ov.findElements(By.xpath(".//span[normalize-space()='" + visibleText + "']"));
                if (opts.size() > 0) {
                    WebElement opt = opts.get(0);
                    safeClick(opt);
                    clicked = true;
                    break;
                }
            } catch (StaleElementReferenceException ignored) { }
        }

        // Fallback: try a broader search inside any overlay panels if direct nested search failed
        if (!clicked) {
            By fallbackOption = By.xpath("//*[contains(@class,'p-select-overlay') or contains(@class,'p-dropdown-panel') or @role='listbox']//span[normalize-space()='" + visibleText + "']");
            try {
                safeClick(fallbackOption);
                clicked = true;
            } catch (Exception e) {
                // final fallback: scan all visible spans and match text
                List<WebElement> all = driver.findElements(By.xpath("//*[contains(@class,'p-select-overlay') or contains(@class,'p-dropdown-panel') or @role='listbox']//span"));
                for (WebElement o : all) {
                    try {
                        if (visibleText.equals(o.getText().trim())) { safeClick(o); clicked = true; break; }
                    } catch (StaleElementReferenceException ignored) {}
                }
            }
        }

        if (!clicked) throw new RuntimeException("Option not found in PrimeNG dropdown: " + visibleText);

        // Wait for overlay disappearance (handles animation)
        try { wait.until(ExpectedConditions.invisibilityOfElementLocated(overlayLocator)); } catch (Exception ignored) {}
    }

    public void selectReportCategoryProcess() {
        selectFromPrimeDropdown(Locators.REPORT_TYPE_FILTER_SELECT, "PROCESS");
        // verify selection visible
        try {
            By selected = By.xpath("//p-select[@id='reportTypeFilter']//span[normalize-space()='PROCESS']");
            wait.until(ExpectedConditions.visibilityOfElementLocated(selected));
        } catch (Exception e) {
            throw new RuntimeException("REPORT_TYPE not reflected as PROCESS");
        }
        clickBody();
        waitForOverlayInvisible();
    }

    public void setDescription(String text) {
        WebElement e = wait.until(ExpectedConditions.visibilityOfElementLocated(Locators.DESCRIPTION_TEXTAREA));
        e.clear();
        e.sendKeys(text);
        String v = e.getAttribute("value");
        if (v == null || !v.equals(text)) throw new RuntimeException("Description not set");
    }

    public void selectSingleLayout() {
        By layout10 = By.xpath(
                "//button[@title='Layout 10' and .//div[contains(@class,'rows-1') and contains(@class,'cols-1')]]"
        );

        waitForOverlayToDisappear();

        safeClick(layout10);

        // confirm selection against exact target layout to avoid false matches
        By selectedLayout = By.xpath(
            "//button[contains(@class,'selected') " +
            "and @title='Layout 10' " +
            "and .//div[contains(@class,'rows-1') and contains(@class,'cols-1')]]"
        );

        wait.until(ExpectedConditions.visibilityOfElementLocated(selectedLayout));

        // scroll to bottom after selection
        ((JavascriptExecutor) driver).executeScript(
                "window.scrollTo(0, document.body.scrollHeight);"
        );
    }

    public void expandReportSection1() {
        By headerLocator = By.xpath(
                "//p-accordion-header[contains(@class,'p-accordionheader')]" +
                "[.//span[normalize-space()='Report 1']]"
        );

        WebElement header = wait.until(
                ExpectedConditions.elementToBeClickable(headerLocator)
        );

        String aria = header.getAttribute("aria-expanded");

        if (!"true".equals(aria)) {
            safeClick(header);

            // IMPORTANT: pass WebElement, not By
            wait.until(ExpectedConditions.attributeToBe(
                    header,
                    "aria-expanded",
                    "true"
            ));

            // wait for UI stabilization
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting after report section expand", e);
            }

            // scroll to bottom
            ((JavascriptExecutor) driver).executeScript(
                    "window.scrollTo(0, document.body.scrollHeight);"
            );

            // small wait after scroll
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting after report section scroll", e);
            }
        }

        // Wait until content is visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("reportTitle")
        ));
    }

    /**
     * Expand an accordion pane by its visible title in a PrimeNG-safe way.
     * Supports headers rendered as role="button" or elements with class containing p-accordion-header.
     */
    public void expandAccordionByTitle(String title) {
        By roleButton = By.xpath("//*[@role='button'][.//*[normalize-space()='" + title + "']]");
        By headerDiv = By.xpath("//*[contains(@class,'p-accordion-header')][.//*[normalize-space()='" + title + "']]");

        final By chosen = driver.findElements(roleButton).size() > 0 ? roleButton : headerDiv;

        try {
            WebElement header = wait.until(ExpectedConditions.visibilityOfElementLocated(chosen));
            String aria = header.getAttribute("aria-expanded");
            if (aria == null) {
                safeClick(header);
                wait.until(d -> {
                    try {
                        WebElement h = d.findElement(chosen);
                        String a = h.getAttribute("aria-expanded");
                        if ("true".equals(a)) return true;
                        String cls = h.getAttribute("class");
                        return cls != null && (cls.contains("active") || cls.contains("expanded") || cls.contains("p-highlight"));
                    } catch (Exception e) { return false; }
                });
            } else if (!"true".equals(aria)) {
                safeClick(header);
                WebElement headerAfter = wait.until(ExpectedConditions.visibilityOfElementLocated(chosen));
                wait.until(ExpectedConditions.attributeToBe(headerAfter, "aria-expanded", "true"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to expand accordion '" + title + "': " + e.getMessage(), e);
        }
    }

    public void setReportTitle(String title) {
        WebElement e = wait.until(ExpectedConditions.visibilityOfElementLocated(Locators.REPORT_TITLE_INPUT));
        e.clear();
        e.sendKeys(title);
        String v = e.getAttribute("value");
        if (v == null || !v.equals(title)) throw new RuntimeException("Report title not set");
    }

    public void selectReportSectionType(String reportType) {
        selectFromPrimeDropdown(Locators.REPORT_SECTION_TYPE_SELECT, reportType);
        // verify visible
        By visible = By.xpath("//p-select[@placeholder='Select Report Type']//span[normalize-space()='" + reportType + "']");
        wait.until(ExpectedConditions.visibilityOfElementLocated(visible));
        clickBody();
        waitForOverlayInvisible();
    }

    public void selectParameters() {

        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//div[contains(@class,'p-treeselect-label-container')]")));
        safeClick(dropdown);

        WebElement processLabel = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//span[normalize-space()='Process']")));

        WebElement processToggle = processLabel.findElement(By.xpath(
            "./preceding::button[contains(@class,'p-tree-node-toggle-button')][1]"));
        if (!"true".equals(processToggle.getAttribute("aria-expanded"))) {
            safeClick(processToggle);
        }

        WebElement completeLabel = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//span[normalize-space()='Complete System']")));

        WebElement completeToggle = completeLabel.findElement(By.xpath(
            "./preceding::button[contains(@class,'p-tree-node-toggle-button')][1]"));
        if (!"true".equals(completeToggle.getAttribute("aria-expanded"))) {
            safeClick(completeToggle);
        }

        WebElement p1 = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//span[contains(text(),'ETP Permeate Flow')]")));

        WebElement p2 = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//span[contains(text(),'ETP RO I/L Flow')]")));

        WebElement p3 = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//span[contains(text(),'ETP RO Recovery')]")));

        safeClick(p1);
        safeClick(p2);
        safeClick(p3);

        // close TreeSelect overlay before continuing to Add Section/Save Dashboard
        safeClick(By.xpath("//body"));
        waitForOverlayToDisappear();
    }

    public void selectParametersTree(List<String> nodes) {
        selectParameters();
    }

    public void addSection() {
        // Ensure no overlay blocks Add Section click.
        clickBody();
        waitForOverlayInvisible();

        // Detect if currently creating a Text section by checking selected Report Type
        By textSelected = By.xpath("//p-select[@placeholder='Select Report Type']//span[normalize-space()='Text']");
        boolean isText = driver.findElements(textSelected).size() > 0;

        if (isText) {
            WebElement addSection = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("//span[normalize-space()='Add Section']"))
            );

            safeClick(addSection);

            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//p-accordion-header[contains(.,'Report')]")
            ));
        } else {
            safeClick(Locators.ADD_SECTION_SPAN);
            // wait for a new section to appear (best-effort)
            By flexAccordion = By.xpath("//*[@role='button'][.//*[contains(normalize-space(),'Report')]] | //div[contains(@class,'p-accordion-header')][.//*[contains(normalize-space(),'Report')]] | //button[contains(@class,'p-accordion-header')][.//*[contains(normalize-space(),'Report')]]");
            wait.until(ExpectedConditions.presenceOfElementLocated(flexAccordion));
        }
    }

    public void saveDashboard() {
        // Ensure no overlay blocks Save Dashboard click.
        clickBody();
        waitForOverlayInvisible();

        // Only apply extended wait/click for Text report type
        By textSelected = By.xpath("//p-select[@placeholder='Select Report Type']//span[normalize-space()='Text']");
        boolean isText = driver.findElements(textSelected).size() > 0;

        if (isText) {
            WebElement saveBtn = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("//span[normalize-space()='Save Dashboard']"))
            );

            safeClick(saveBtn);

            // wait for confirm dialog popup
            WebElement popup = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'p-confirmdialog')]")
            ));

            // click "No" button
            WebElement noBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//span[normalize-space()='No']/ancestor::button")
            ));

            ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", noBtn
            );

            noBtn.click();
            waitForToastToDisappear();

            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@title='Notifications']")));
        } else {
            safeClick(Locators.SAVE_DASHBOARD_SPAN);

            // wait for confirm dialog popup
            WebElement popup = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'p-confirmdialog')]")
            ));

            // click "No" button
            WebElement noBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//span[normalize-space()='No']/ancestor::button")
            ));

            ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", noBtn
            );

            noBtn.click();

            waitForOverlayInvisible();
            waitForToastToDisappear();
        }
    }

    // Notifications-based creation check
    public boolean waitForCreationNotification(String reportType, int maxMinutes) {
        waitForToastToDisappear();
        safeClick(Locators.NOTIFICATIONS_BUTTON);

        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofMinutes(maxMinutes));
        By notif = By.xpath("//div[contains(@class,'notification-item')][.//h5[normalize-space()='Created'] and .//p[contains(normalize-space(), '" + reportType + " — created')]]//span[contains(@class,'badge-success') and normalize-space()='success']");
        try {
            longWait.until(ExpectedConditions.visibilityOfElementLocated(notif));
            waitForToastToDisappear();
            // Before closing notifications, try clearing all notifications first
            try {
                WebElement clearAll = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[@title='Clear all notifications']")
                ));
                safeClick(clearAll);
                waitForToastToDisappear();
            } catch (Exception ignored) {}

            try {
                safeClick(By.xpath("//button[@title='Close']"));
                waitForToastToDisappear();
            } catch (Exception ignored) {}

            clickBody();
            return true;
        } catch (Exception e) {
            // Attempt to tidy notifications panel even on failure: clear then close
            try {
                WebElement clearAll = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[@title='Clear all notifications']")
                ));
                safeClick(clearAll);
                waitForToastToDisappear();
            } catch (Exception ignored) {}

            try {
                safeClick(By.xpath("//button[@title='Close']"));
                waitForToastToDisappear();
            } catch (Exception ignored) {}

            clickBody();
            return false;
        }
    }

    // View report: UI validations + optional backend check (best-effort)
    public boolean viewReportUIValidation(String reportType) {
        // Click home then Special Reports
        safeClick(By.xpath("//img[@class='logo']"));
        openSpecialReports();

        By viewIcon = By.xpath("//tr[.//div[normalize-space()='" + reportType + "']]//i[@ptooltip='View Dashboard Group']");
        safeClick(viewIcon);

        // ================= TABLE VALIDATION =================
        if (reportType.equalsIgnoreCase("Table")) {
            By tableLocator = By.xpath("//table");

            // Wait until table is present
            wait.until(ExpectedConditions.presenceOfElementLocated(tableLocator));

            // Now validate non-empty rows
            List<WebElement> rows = driver.findElements(
                    By.xpath("//table//tbody//tr[td[normalize-space()!='' and normalize-space()!='N/A']]")
            );

            if (rows.size() > 0) {
                System.out.println("✅ Table Report is NOT Empty – PASS");
            } else {
                throw new RuntimeException("UI validation failed for Table – Table is Empty");
            }

            // Table-specific validation complete
            return true;
        }

        // Text report: wait for the text container to render and ensure non-empty content
        if ("Text".equals(reportType)) {
            By textContent = By.xpath(
                "//div[contains(@class,'text-content-body')]//p[normalize-space()!='']"
            );

            wait.until(ExpectedConditions.visibilityOfElementLocated(textContent));

            String text = driver.findElement(textContent).getText();

            if (text == null || text.trim().isEmpty()) {
                throw new AssertionError("Text report is empty - No content displayed");
            }

            System.out.println("Text Report Content Found: " + text);

            return true;
        }

        // UI validation depending on type
        boolean uiOk = false;
        try {
            // charts
            By chartCanvas = By.xpath("//div[contains(@class,'chart-canvas')]//canvas");
            By tableRow = By.xpath("//table//tbody//tr[td[normalize-space()!='' and normalize-space()!='N/A']]");
            By textContent = By.xpath("//div[contains(@class,'overflow-auto')]//*[normalize-space()!='']");

            if (wait.until(d -> d.findElements(chartCanvas).size() > 0)) uiOk = true;
            else if (wait.until(d -> d.findElements(tableRow).size() > 0)) uiOk = true;
            else if (wait.until(d -> d.findElements(textContent).size() > 0)) uiOk = true;
        } catch (Exception ignored) {}

        return uiOk;
    }

    public void deleteReportByName(String reportType) {
        safeClick(By.xpath("//img[@class='logo']"));
        openSpecialReports();

        By delIcon = By.xpath("//tr[.//div[normalize-space()='" + reportType + "']]//i[@ptooltip='Delete Dashboard Group']");
        safeClick(delIcon);
        By confirm = By.xpath("//span[normalize-space()='Delete']");
        safeClick(confirm);
        // optional: wait until report row gone
        By row = By.xpath("//tr[.//div[normalize-space()='" + reportType + "']]");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(row));
    }

    /**
     * Enter content into PrimeNG/Quill text editor. Use the actual editable `.ql-editor`.
     * This is intended to be used only for Text report sections.
     */
    public void enterTextReportContent(String text) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(10));

        By editorLocator = By.xpath(
            "//div[contains(@class,'ql-editor') and @contenteditable='true']"
        );

        WebElement editor = shortWait.until(
            ExpectedConditions.visibilityOfElementLocated(editorLocator)
        );

        try {
            safeClick(editor);
            editor.sendKeys(Keys.CONTROL + "a");
            editor.sendKeys(Keys.DELETE);
            editor.sendKeys(text);
        } catch (Exception e) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript(
                "arguments[0].innerText = arguments[1];",
                editor,
                text
            );
        }

        // After entering text, wait for the PrimeNG editor container to render and
        // for the content to be reflected in the DOM. This avoids racing with
        // Angular bindings / PrimeNG animations before Add Section / Save.
        By editorContainer = By.xpath("//div[contains(@class,'p-editor-content')]");
        wait.until(ExpectedConditions.visibilityOfElementLocated(editorContainer));

        // Wait until the editor reflects non-empty content before continuing.
        wait.until(d -> {
            try {
                WebElement current = d.findElement(editorLocator);
                String currentText = current.getText();
                return currentText != null && !currentText.trim().isEmpty();
            } catch (Exception ex) {
                return false;
            }
        });
    }
}
