tyzden2kindle
=============

This maven project allows you to browse the .tyzden.com and download the current issue. It composes the .mobi file suitable for Kindle. You need to have active piano account, Firefox browser, Maven and Java installed.

Tested with Linux, Firefox 24 ESR, Maven 3.04 and Java SE 6.

To use this program you need to do the following:
Start the Firefox
Browse to tyzden.com
Sign in using your piano credentials
go to Help -> Troubleshooting Information -> Open Directory
copy the contents of this directory to src/main/resources/ff24esrPiano directory

After doing this the program will be able to access the current issue download it. The Firefox binary is expected to be in "/usr/bin/firefox", if you installed the Firefox to different location, please update the path to Firefox binary in Utils.java

Having all this setup done, you can run the project by executing:
mvn clean compile exec:java

As a result, new directory called filesForGeneration-${week}-${year} appears with the .mobi file of the current issue.

