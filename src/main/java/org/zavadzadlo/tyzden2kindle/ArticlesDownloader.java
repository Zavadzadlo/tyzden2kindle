package org.zavadzadlo.tyzden2kindle;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ArticlesDownloader {

    private Map<String, String> articlelinksToSectionMap = new LinkedHashMap<String, String>();
    private Map<String, String> fileNameToSectionMap = new HashMap<String, String>();

    public void downloadArticles(WebDriver browser) {

        createArticleLinksToSectionsMap(browser);

        createArticleFiles(browser);

    }

    private Map<String, String> createArticleLinksToSectionsMap(WebDriver browser) {
        String weekOfIssue = Utils.getWeekOfCurrentIssue();

        String yearOfIssue = Utils.getYearOfCurrentIssue();

        browser.get("http://www.tyzden.sk/casopis/" + yearOfIssue + "/" + weekOfIssue + ".html");
        Utils.waitForElementById(browser, "top");

        List<WebElement> cols = browser.findElements(By
            .xpath("//div[@id='main_area']//div[@class='text_area']//div[@class='col']"));

        String currentSection = null;

        for (WebElement col : cols) {
            List<WebElement> children = col.findElements(By.xpath("//*"));
            for (WebElement linkOrSection : children) {
                if (linkOrSection.getTagName().contains("h3")) {
                    currentSection = linkOrSection.getText().replaceFirst("\\.", "");
                } else if (linkOrSection.getTagName().contains("ul")) {
                    List<WebElement> links = linkOrSection.findElements(By.xpath("li/a"));
                    for (WebElement link : links) {
                        String href = link.getAttribute("href");
                        if (href.contains(Utils.getYearOfCurrentIssue() + "/" + weekOfIssue)) {
                            articlelinksToSectionMap.put(href, currentSection);
                        }
                    }
                }
            }
        }

        // browser.close();

        return articlelinksToSectionMap;
    }

    public void printMap(Map<String, String> map) {
        for (String key : map.keySet()) {
            System.out.println(key + ": " + map.get(key));
        }
    }

    private void createArticleFiles(WebDriver browser) {
        String template = Utils.readPath("articleTemplate.html");

        File genDir = Utils.getGenDir();
        genDir.mkdir();

        int count = 1;
        for (String href : articlelinksToSectionMap.keySet()) {
            if (Utils.MAX_ARTICLES >= 0 && count > Utils.MAX_ARTICLES) {
                break;
            }
            saveArticle(browser, href, genDir, count, template);
            count++;
        }

        browser.close();
    }

    private void saveArticle(WebDriver browser, String href, File dir, int count, String template) {

        String filename = dir.getPath() + File.separatorChar + String.format("%03d", count) + ".html";
        File article = new File(filename);
        String section = articlelinksToSectionMap.get(href);
        fileNameToSectionMap.put(article.getName(), section);

        if (article.exists()) {
            return;
        }

        browser.get(href);
        String text = Utils.waitForElementById(browser, "bodytext").getText().replaceAll("[\n\r]+", "</p>\n\n<p>");
        List<WebElement> blackTexts = browser.findElements(By.xpath("//div[@class='black_area']//p[@class='text']"));
        String desc = blackTexts.get(blackTexts.size() - 1).getText();
        String header = Utils.waitForElement(browser, By.xpath("//div[@class='black_area']//h1")).getText();
        String author = ".kolektiv autorov";
        if (browser.getPageSource().matches("(?s).*class=.?autor.?[^a-zA-Z](?s).*")) {
            author = browser.findElement(By.xpath("//div[@class='black_area']//a[@class='autor']")).getText();
        }

        String page = new String(template);
        page = page.replace("{AUTHOR}", author);
        page = page.replace("{LINK}", href);
        page = page.replace("{HEADER}", header);
        page = page.replace("{DESC}", desc);
        page = page.replace("{BODY}", text);
        page = page.replace("{SECTION}", section);

        Utils.writeTextToFile(filename, page, href);

        System.out.println(header);
    }

    public Map<String, String> getFileNameToSectionMap() {
        return fileNameToSectionMap;
    }

}
