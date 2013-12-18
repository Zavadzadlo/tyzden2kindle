package org.zavadzadlo.tyzden2kindle;

import org.openqa.selenium.WebDriver;

public class Main {

    public static void main(String[] args) {

        System.out.println(".tyzden2kindle starting...");

        WebDriver browser = Utils.createFirefoxDriver();

        ArticlesDownloader downloader = new ArticlesDownloader();
        downloader.downloadArticles(browser);

        MobiComposer composer = new MobiComposer();
        composer.setFilenameToSectionMap(downloader.getFileNameToSectionMap());

        composer.createMobi();

        System.out.println("\nfinish");
    }

}
