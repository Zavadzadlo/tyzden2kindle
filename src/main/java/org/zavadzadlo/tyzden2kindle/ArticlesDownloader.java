package org.zavadzadlo.tyzden2kindle;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.By.ByClassName;
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
        String issueUrl = "http://www.tyzden.sk/casopis/" + weekOfIssue + "-" + yearOfIssue + "/";

        browser.get(issueUrl);
        Utils.waitForElement(browser, By.className("container"));
        List<WebElement> sections = browser.findElements(By
            .xpath("//div[@class='content']//div[@class='mag__contents']//div[@class='container']/div[@class='mag__section']"));
        String currentSection = null;

        for (WebElement section : sections) {
            currentSection = section.findElement(By.className("mag__section-title")).getText().replace(".", "");
            List<WebElement> links = section.findElements(By.xpath(".//h1[@class='teaser__title']/a"));
            for (WebElement link : links) {
                String href = link.getAttribute("href");
                articlelinksToSectionMap.put(href, currentSection);
            }
        }

        System.out.println("Article links with sections:");
        for (String href : articlelinksToSectionMap.keySet()) {
            System.out.println(href + ", " + articlelinksToSectionMap.get(href));
        }

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
        String text = Utils.waitForElement(browser, By.className("article__unlocked_content")).getText().replaceAll("[\n\r]+", "</p>\n\n<p>");
        String desc = browser.findElement(By.xpath("//div[contains(@class,'detail__title article__title')]//p")).getText();
        String header = browser.findElement(By.xpath("//div[contains(@class,'detail__title article__title')]//h1")).getText();
        String author = ".kolektiv autorov";
        String authorFromPage = browser.findElement(By.xpath("//div[contains(@class,'detail__title article__title')]//p/span[@class='highlight']")).getText();
        if (authorFromPage != null && authorFromPage.trim().length() > 2) {
            author = authorFromPage;
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
