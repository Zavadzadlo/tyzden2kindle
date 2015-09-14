tyzden2kindle
=============

This maven project allows you to browse the tyzden.sk and download the current issue. It composes the .mobi file suitable for Kindle. You need to have:

- Linux operating system (https://getfedora.org/)
- Active Piano account (http://www.pianomedia.sk)
- Firefox browser (https://ftp.mozilla.org/pub/mozilla.org/firefox/releases/)
- Maven (http://maven.apache.org/download.cgi)
- Java SE (http://www.oracle.com/technetwork/java/javase/downloads/index.html).
- Kindlegen (http://www.amazon.com/gp/feature.html?docId=1000765211)

Tested with Linux (Fedora), Firefox 24/31/38 ESR, Maven 3.3.1, Java SE 6/7/8, and kindlegen 2.9.

To use this program you need to do the following:

- Start the Firefox
- Browse to tyzden.com
- Sign in using your piano credentials
- Create ./src/main/resources/ffPiano directory
- Go to Help -> Troubleshooting Information -> Open Directory
- Copy the contents of that directory to src/main/resources/ffPiano directory

After doing this the program will be able to access the current issue and download it.

The Firefox binary is expected to be in ```/usr/bin/firefox```, Kindlegen binary is expected to be in ```/usr/bin/kindlegen```. If you want to use different locations, please update the appropriate paths in ```src/main/java/org/zavadzadlo/tyzden2kindle/Utils.java``` class.

Having all this setup done, navigate to the root directory of this project (called ```tyzden2kindle```) and run the project by executing:

```
mvn clean compile exec:java
```

As a result, new directory called filesForGeneration-[week]-[year] appears with the .mobi file of the current issue.

If you want to download some other issue, use ```WEEK_OF_ISSUE``` and ```YEAR_OF_ISSUE``` static variables in ```src/main/java/org/zavadzadlo/tyzden2kindle/Utils.java``` class.
