tyzden2kindle
=============

This maven project allows you to browse the tyzden.com and download the current issue. It composes the .mobi file suitable for Kindle. You need to have:

- active Piano account (http://www.pianomedia.sk)
- Firefox browser (https://ftp.mozilla.org/pub/mozilla.org/firefox/releases/24.2.0esr/)
- Maven (http://maven.apache.org/download.cgi)
- Java SE (http://www.oracle.com/technetwork/java/javase/downloads/index.html).

Tested with Linux, Firefox 24 ESR, Maven 3.0.4 and Java SE 6.

To use this program you need to do the following:

- Start the Firefox
- Browse to tyzden.com
- Sign in using your piano credentials
- go to Help -> Troubleshooting Information -> Open Directory
- copy the contents of this directory to src/main/resources/ff24esrPiano directory

After doing this the program will be able to access the current issue and download it. The Firefox binary is expected to be in ```/usr/bin/firefox```, if you installed the Firefox to different location, please update the path to Firefox binary in ```src/main/java/org/zavadzadlo/tyzden2kindle/Utils.java```.

Having all this setup done, navigate to the root directory of this project (called tyzden2kindle) and run the project by executing:

```
mvn clean compile exec:java
```

As a result, new directory called filesForGeneration-[week]-[year] appears with the .mobi file of the current issue.

You can also import the project to your favorite IDE and run the project as typical Java application.
