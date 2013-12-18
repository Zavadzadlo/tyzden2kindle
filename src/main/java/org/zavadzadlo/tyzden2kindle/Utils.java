package org.zavadzadlo.tyzden2kindle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Utils {

    private static String week = null;
    private static String year = null;

    private static FileFilter articleFilter;

    private static final long TIMEOUT = 5;

    // negative number indicates no limit => all the articles will be processed
    public static final int MAX_ARTICLES = -1;

    // you need to create the contents of this file manually before execution
    private static String FIREFOX_PROFILE_PATH = "src/main/resources/ff24esrPiano";

    private static String FIREFOX_BINARY = "/usr/bin/firefox";

    static {
        computeWeekAndYearOfCurrentIssue();

        createArticleFilter();
    }

    private static void computeWeekAndYearOfCurrentIssue() {
        Calendar cal = Calendar.getInstance();
        year = String.valueOf(cal.get(Calendar.YEAR));
        int weekInt = cal.get(Calendar.WEEK_OF_YEAR);
        if (cal.get(Calendar.DAY_OF_WEEK) < 5 || (cal.get(Calendar.DAY_OF_WEEK) == 5 && cal.get(Calendar.HOUR_OF_DAY) < 15)) {
            weekInt--;
        }
        week = Integer.toString(weekInt);
    }

    private static void createArticleFilter() {
        articleFilter = new FileFilter() {
            public boolean accept(File file) {
                return file.isFile() && file.getName().matches("^0[0-9][0-9].html$");
            }
        };
    }

    public static String getWeekOfCurrentIssue() {
        return week;
    }

    public static String getYearOfCurrentIssue() {
        return year;
    }

    public static FirefoxDriver createFirefoxDriver() {

        File profileDir = new File(FIREFOX_PROFILE_PATH);
        FirefoxProfile profile = new FirefoxProfile(profileDir);
        FirefoxBinary ffBinary = new FirefoxBinary(new File(FIREFOX_BINARY));
        FirefoxDriver browser = new FirefoxDriver(ffBinary, profile);
        return browser;
    }

    public static WebElement waitForElement(WebDriver driver, final By by) {
        WebElement webElement = (new WebDriverWait(driver, TIMEOUT)).until(new ExpectedCondition<WebElement>() {
            public WebElement apply(WebDriver d) {
                return d.findElement(by);
            }
        });
        return webElement;
    }

    public static WebElement waitForElementById(WebDriver driver, String id) {
        WebElement webElement = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
        return webElement;
    }

    public static String readPath(String path) {
        BufferedReader br = null;
        String template = "";
        try {
            File pageTemplate = new File(path);
            FileReader fr = new FileReader(pageTemplate);
            br = new BufferedReader(fr);

            String line = null;
            while ((line = br.readLine()) != null) {
                template += line + "\n";
            }

            br.close();

            return template;

        } catch (Exception e) {
            e.printStackTrace();
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                    return null;
                }
            }
            return null;
        }
    }

    public static void writeTextToFile(String filename, String text, String errorMessage) {
        BufferedWriter bw = null;
        try {
            File f = new File(filename);
            f.delete();
            FileWriter fw = new FileWriter(f, false);
            bw = new BufferedWriter(fw);
            bw.write(text);

            bw.close();
        } catch (Exception e) {
            System.err.println("Error: " + errorMessage);
            e.printStackTrace();
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                    return;
                }
            }
            return;
        }
    }

    public static File getGenDir() {
        return new File("./filesForGeneration-" + Utils.getYearOfCurrentIssue() + "-" + Utils.getWeekOfCurrentIssue());
    }

    public static FileFilter getArticleFilter() {
        return articleFilter;
    }

    public static String getCurrentDate() {
        String date = SimpleDateFormat.getDateInstance().format(new Date());
        SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd");
        date = sfd.format(new Date());
        return date;
    }

}
