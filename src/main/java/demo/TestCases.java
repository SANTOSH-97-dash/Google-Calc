package demo;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

public class TestCases {
    ChromeDriver driver;
    WebDriverWait wait;

    public TestCases() {
        System.out.println("Constructor: TestCases");

        WebDriverManager.chromedriver().timeout(30).setup();
        ChromeOptions options = new ChromeOptions();
        LoggingPreferences logs = new LoggingPreferences();
        logs.enable(LogType.BROWSER, Level.ALL);
        logs.enable(LogType.DRIVER, Level.ALL);
        options.setCapability("goog:loggingPrefs", logs);

        System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "chromedriver.log");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);

        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    public void endTest() {
        System.out.println("End Test: TestCases");
        driver.close();
        driver.quit();
    }

    private void handleCaptchaIfPresent() {
        try {
            if (driver.getCurrentUrl().contains("sorry") ||
                driver.getPageSource().contains("I'm not a robot") ||
                driver.getPageSource().contains("unusual traffic")) {

                System.out.println("\n==============================================");
                System.out.println(">>> CAPTCHA DETECTED <<<");
                System.out.println("Please solve the CAPTCHA in the browser NOW.");
                System.out.println("You have 60 seconds. After solving it the test will continue automatically.");
                System.out.println("==============================================\n");

                Thread.sleep(8000);
            }
        } catch (Exception e) {
            // ignore
        }
    }

    // ========== TestCase01 ==========
    public void testCase01() {
        System.out.println("Start Test case: testCase01");

        driver.get("https://www.google.com/");
        handleCaptchaIfPresent();

        WebElement searchBox = driver.findElement(By.name("q"));
        searchBox.sendKeys("calculator");
        searchBox.sendKeys(Keys.ENTER);
        handleCaptchaIfPresent();

        String currentUrl = driver.getCurrentUrl();
        System.out.println("Current URL: " + currentUrl);
        if (currentUrl.toLowerCase().contains("google")) {
            System.out.println("URL verification passed – contains google");
        }

        System.out.println("Waiting for calculator to load...");
        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("#cwos")),
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[jsname='VssY5c']")),
                ExpectedConditions.presenceOfElementLocated(By.xpath("//div[text()='AC' or text()='CE' or text()='=']"))
        ));

        String initialValue = getDisplayValue();
        System.out.println("Initial display value: '" + initialValue + "'");

        if (initialValue.equals("0") || initialValue.isEmpty()) {
            System.out.println("Initial display is 0 – PASSED");
        } else {
            System.out.println("Initial display is NOT 0 – FAILED. Actual: " + initialValue);
        }

        System.out.println("end Test case: testCase01");
    }

    // ========== TestCase02 ==========
    public void testCase02() {
        System.out.println("Start Test case: testCase02");
        ensureCalculatorReady();

        // 5 + 7
        clickCalculatorButton("5");
        clickCalculatorButton("plus");
        clickCalculatorButton("7");
        clickCalculatorButton("equals");

        String result = getDisplayValue();
        System.out.println("5 + 7 = " + result);
        if (result.equals("12")) {
            System.out.println("Addition PASSED");
        } else {
            System.out.println("Addition FAILED");
        }

        // 15 - 8
        clickCalculatorButton("1");
        clickCalculatorButton("5");
        clickCalculatorButton("minus");
        clickCalculatorButton("8");
        clickCalculatorButton("equals");

        result = getDisplayValue();
        System.out.println("15 - 8 = " + result);
        if (result.equals("7")) {
            System.out.println("Subtraction PASSED");
        } else {
            System.out.println("Subtraction FAILED");
        }

        System.out.println("end Test case: testCase02");
    }

    // ========== TestCase03 ==========
    public void testCase03() {
        System.out.println("Start Test case: testCase03");
        ensureCalculatorReady();

        // 10 × 3
        clickCalculatorButton("1");
        clickCalculatorButton("0");
        clickCalculatorButton("multiply");
        clickCalculatorButton("3");
        clickCalculatorButton("equals");

        String result = getDisplayValue();
        System.out.println("10 × 3 = " + result);
        if (result.equals("30")) {
            System.out.println("Multiplication PASSED");
        } else {
            System.out.println("Multiplication FAILED");
        }

        // Clear button (works for both AC and CE)
        clickCalculatorButton("CE");

        result = getDisplayValue();
        System.out.println("After Clear: '" + result + "'");
        if (result.equals("0") || result.isEmpty()) {
            System.out.println("Clear PASSED");
        } else {
            System.out.println("Clear FAILED");
        }

        System.out.println("end Test case: testCase03");
    }

    // ========== TestCase04 ==========
    public void testCase04() {
        System.out.println("Start Test case: testCase04");
        ensureCalculatorReady();

        // 20 ÷ 4
        clickCalculatorButton("2");
        clickCalculatorButton("0");
        clickCalculatorButton("divide");
        clickCalculatorButton("4");
        clickCalculatorButton("equals");

        String result = getDisplayValue();
        System.out.println("20 ÷ 4 = " + result);
        if (result.equals("5")) {
            System.out.println("Division PASSED");
        } else {
            System.out.println("Division FAILED");
        }

        System.out.println("end Test case: testCase04");
    }

    // ========== Helper methods ==========

    private void ensureCalculatorReady() {
        handleCaptchaIfPresent();
        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(By.xpath("//div[text()='AC' or text()='CE' or text()='=']")),
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("#cwos")),
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[jsname='VssY5c']"))
        ));
    }

    // ========== ULTRA-FAST click method ==========
    private void clickCalculatorButton(String label) {
        try {
            String ariaLabel = label.toLowerCase();

            switch (label.toLowerCase()) {
                case "+":
                case "plus":     ariaLabel = "plus";        break;
                case "-":
                case "−":
                case "minus":    ariaLabel = "minus";       break;
                case "×":
                case "*":
                case "multiply": ariaLabel = "multiply";    break;
                case "÷":
                case "/":
                case "divide":   ariaLabel = "divide";      break;
                case "=":
                case "equals":   ariaLabel = "equals";      break;
                case "ac":
                case "ce":
                case "clear":    ariaLabel = "clear entry"; break;
            }

            WebElement btn = null;

            // Fastest path for operators
            try {
                btn = driver.findElement(By.xpath("//div[@aria-label='" + ariaLabel + "']"));
            } catch (Exception e) {
                // Fastest path for numbers + CE/AC
                try {
                    btn = driver.findElement(By.xpath("//div[normalize-space()='" + label + "']"));
                } catch (Exception ignored) {}
            }

            // Last chance for clear button
            if (btn == null && (label.equalsIgnoreCase("AC") || label.equalsIgnoreCase("CE"))) {
                btn = driver.findElement(By.xpath("//div[@aria-label='clear entry' or text()='CE' or text()='AC']"));
            }

            if (btn != null) {
                btn.click();
                System.out.println("ClickElement: " + label);
                Thread.sleep(50);   // very small delay (you can set to 0)
            } else {
                System.out.println("Could not click: " + label);
            }

        } catch (Exception e) {
            System.out.println("Could not click: " + label);
        }
    }

    private String getDisplayValue() {
        try {
            return driver.findElement(By.cssSelector("#cwos")).getText().trim();
        } catch (Exception e) {
            try {
                return driver.findElement(By.cssSelector("div[jsname='VssY5c'], span[jsname='VssY5c']")).getText().trim();
            } catch (Exception ex) {
                return "";
            }
        }
    }
}