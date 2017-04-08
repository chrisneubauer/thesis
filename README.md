# Thesis
This repository presents an application that has been developed during my master thesis. It is capable of invoice processing using Googles Tesseract and convert it to an electronic invoice using the ZUGFeRD standard.

# Setup and Install
This application is dependent on Java8 and JavaFX and also requires MySQL and ImageMagick (if used on ubuntu). A portable version of ImageMagick is used for windows-based OS.
Before starting the application, please create a database scheme and create the tables with the newest script. Also, chronologically execute all update scripts of the version.
After running the application the first time, configuration files are created automatically. 
You can either specify and test your database settings in the settings view, or insert the necessary information in the config.ini.

Linux: Before being able to process a form, it is necessary to install tesseract using "sudo apt-get install tesseract-ocr".

#Troubleshooting
Known problems with the application and solutions for them can be found in the Troubleshooting.md file.