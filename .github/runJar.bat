@echo off
FOR /f "tokens=*" %%G IN ('dir /b SheetsIO*.jar') DO java -Dlog4j.configurationFile=resources/log4j2.xml -jar %%G
PAUSE