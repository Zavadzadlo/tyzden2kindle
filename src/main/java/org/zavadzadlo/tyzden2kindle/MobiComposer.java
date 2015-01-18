package org.zavadzadlo.tyzden2kindle;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MobiComposer {

    private Map<String, String> filenameToSectionMap;

    private String currentSection;
    private String followingSection;

    FileFilter articleFilter;
    File genDir;
    List<File> articles;

    public MobiComposer() {
        articleFilter = Utils.getArticleFilter();

        genDir = Utils.getGenDir();

        articles = new ArrayList<File>();
        for (File article : genDir.listFiles(articleFilter)) {
            articles.add(article);
        }
        Collections.sort(articles);
    }

    public void setFilenameToSectionMap(Map<String, String> map) {
        this.filenameToSectionMap = map;
    }

    public void createMobi() {
        createContentsHtml();
        createTemplateOpf();
        createNavContents();

        generateMobi();
    }

    private void createContentsHtml() {
        String template = Utils.readPath("contentsTemplate.html");

        String contents = "";
        if (articles.size() > 0) {
            initializeSections();
            contents = "<h4>" + currentSection + "</h4>\n<ul>\n";
            int articleCount = 0;
            for (File article : articles) {
                String header = Utils.readPath(article.getAbsolutePath());
                header = header.substring(header.indexOf("<title>") + 7, header.indexOf("</title>"));
                String author = Utils.readPath(article.getAbsolutePath());
                author = author.substring(author.indexOf("<p>") + 7, author.indexOf("</p>"));
                contents += "<li><a href=\"" + article.getName() + "\">" + header + "</a></li>\n";
                if (followingSection != null && !currentSection.equals(followingSection)) {
                    contents += "</ul>\n";
                    contents += "<h4>" + followingSection + "</h4>\n<ul>\n";
                }
                articleCount++;
                updateSections(articleCount);
            }
            if (!contents.endsWith("</ul>\n")) {
                contents += "</ul>\n";
            }
        }

        String contentsPath = genDir.getPath() + File.separator + "contents.html";
        String contentsText = new String(template);
        contentsText = contentsText.replace("{CONTENTS}", contents);

        Utils.writeTextToFile(contentsPath, contentsText, "Can't save contents.html file");

    }

    private void createTemplateOpf() {
        String template = Utils.readPath("opfTemplate.opf");

        String items = "";
        String itemRefs = "";
        String firstTitle = "";
        for (File article : articles) {
            if (article.getName().equals("001.html")) {
                firstTitle = Utils.readPath(article.getAbsolutePath());
                firstTitle = firstTitle.substring(firstTitle.indexOf("<title>") + 7, firstTitle.indexOf("</title>"));
            }
            items += "<item href=\"" + article.getName() + "\" media-type=\"application/xhtml+xml\" id=\"" + article.getName()
                + "\"/>\n";
            itemRefs += "<itemref idref=\"" + article.getName() + "\"/>\n";
        }

        String opfFile = new String(template);
        opfFile = opfFile.replace("{ITEMS}", items);
        opfFile = opfFile.replace("{ITEMREFS}", itemRefs);
        opfFile = opfFile.replace("{NUMBER}", Utils.getWeekOfCurrentIssue());
        opfFile = opfFile.replace("{DATE}", Utils.getCurrentDate());
        opfFile = opfFile.replace("{FIRST_TITLE}", firstTitle);

        String filename = genDir.getPath() + File.separator + "tyzden.opf";
        Utils.writeTextToFile(filename, opfFile, "Can't save tyzden.opf file");
    }

    private void createNavContents() {
        String template = Utils.readPath("nav-contentsTemplate.ncx");

        int articleCount = 0;
        int playOrder = 1;
        String navpoints = "";
        String currentTitle = null;
        String author = null;
        String pageContent = null;

        if (articles.size() > 0) {
            initializeSections();

            navpoints += "<navPoint playOrder=\"" + playOrder + "\" class=\"section\" id=\"" + currentSection.replace(" ", "")
                + "\">\n";
            navpoints += "<navLabel>\n<text>" + currentSection + "</text>\n</navLabel>\n";
            playOrder += 2;

            for (File article : articles) {

                pageContent = Utils.readPath(article.getAbsolutePath());
                currentTitle = new String(pageContent);
                currentTitle = currentTitle.substring(currentTitle.indexOf("<title>") + 7, currentTitle.indexOf("</title>"));
                author = new String(pageContent);
                author = author.substring(author.indexOf("<p>") + 3, author.indexOf("</p>"));

                navpoints += "<content src=\"" + article.getName() + "\"/>\n";
                navpoints += "<navPoint playOrder=\"" + playOrder + "\" class=\"article\" id=\"" + article.getName() + "\">\n"
                    + "<navLabel>\n<text>" + currentTitle + "</text>\n" + "</navLabel>\n" + "<content src=\""
                    + article.getName() + "\"/>\n" + "<mbp:meta name=\"author\">" + author + "</mbp:meta>\n" + "</navPoint>\n";
                playOrder += 2;
                if (followingSection != null && !currentSection.equals(followingSection)) {
                    navpoints += "</navPoint>\n";
                    navpoints += "<navPoint playOrder=\"" + playOrder + "\" class=\"section\" id=\""
                        + followingSection.replace(" ", "") + "\">\n";
                    navpoints += "<navLabel>\n<text>" + followingSection + "</text>\n</navLabel>\n";
                    playOrder += 2;
                }
                articleCount++;
                updateSections(articleCount);
            }
            if (!navpoints.endsWith("</navPoint>\n</navPoint>\n")) {
                navpoints += "</navPoint>\n";
            }
        }

        String filename = genDir.getPath() + File.separator + "nav-contents.ncx";
        String contentsFile = new String(template);
        contentsFile = contentsFile.replace("{NAVPOINTS}", navpoints);
        contentsFile = contentsFile.replace("{DATE}", Utils.getCurrentDate());

        Utils.writeTextToFile(filename, contentsFile, "Can't save nav-contents.ncx file");
    }

    private void initializeSections() {
        if (articles.size() > 0) {
            currentSection = filenameToSectionMap.get(articles.get(0).getName());
        }
        followingSection = null;
        if (articles.size() > 1) {
            followingSection = filenameToSectionMap.get(articles.get(1).getName());
        }
    }

    private void updateSections(int count) {
        currentSection = followingSection;
        if (count + 1 < articles.size()) {
            followingSection = filenameToSectionMap.get(articles.get(count + 1).getName());
        } else {
            followingSection = null;
        }
    }

    private void generateMobi() {
        String command = Utils.KINDLEGEN_BINARY + " " + genDir.getAbsolutePath() + File.separator
            + "tyzden.opf -c0 -o tyzden-" + Utils.getYearOfCurrentIssue() + "-" + Utils.getWeekOfCurrentIssue() + ".mobi";

        System.out.println("Executing command:");
        System.out.println(command);

        try {
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
        } catch (Exception e) {
            System.err.println("Error while generating mobi using command:");
            System.err.println(command);
            e.printStackTrace();
        }
    }

}
