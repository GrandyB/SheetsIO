# Setup

- Clone project
- Import existing Maven project in Eclipse

# Creating releases

## .jar file
In eclipse, File > Export (Java > Runnable .jar) > Extract required libraries into generated JAR (I send it to `/dist/dist.jar`)

## to EXE
Using [Jsmooth 0.9.9-7](https://sourceforge.net/projects/jsmooth/files/jsmooth/0.9.9-7/):

1. Can load [pre-built config](https://github.com/GrandyB/SheeTXT/blob/master/SheeTXT-exe-packager.jsmooth) from the repo
1. Run jsmooth compile (if you didn't use `/dist/dist.jar` as destination for your `.jar` file from Eclipse, supply what you _did_ use within the 'Application' tab of the UI)
 