# CreateScreenshots
This tool can do 2 things :+1:


## 1. To create a screenshots every few milliseconds, run your app accordingly

> java -jar CreateScreenshots-1.0-SNAPSHOT.jar "C:\screenshots" 1000 "png"

1. "C:\screenshots" - where we want to store our screenshots
2. 1000 - how oftern do we take a screenshot (in milliseconds)
3. "png" - output image format
<br>


## 2. To format those screenshots in a number of sizes, transparency, ratio, etc... run your app accordingly

> java -jar CreateScreenshots-1.0-SNAPSHOT.jar "C:\source_folder" "C:\destination_folder" "png" 0 0 800 800 255 255 255

1. "C:\source_folder" - the source folder with all your screenshots
2. "C:\destination_folder" - where you want the new images written to
3. "png" - desired output image format
4. 0 0 800 800 - x, y, width, height of source image that we want to format our new images
5. 255 255 255 - desired rgb background color
<br>
