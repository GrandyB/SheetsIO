# Contributing

## Initial setup
* The only prerequisite for building this project is having a Java 17 SDK installed. Gradle usually does a good job at discovering Java SDKs automatically; you can force a specific JDK by uncommenting the line in [gradle.properties](gradle.properties).
* You don't need to install Gradle - the Gradle Wrapper files (`./gradlew` and `./gradlew.bat`) will install Gradle on your system automatically.
* Once done, you can build a shaded ("fat") JAR with `./gradlew shadowJar`.
* You do not need to install JavaFX separately - this is handled by Gradle.
* You do not need to use a specific IDE. In this project, there are IntelliJ IDEA project files due to its prevalance.

## Running
You can run the application with either of these commands (assuming Java 17 or newer) - one being with a Log4j config, another without:
* `java -Dlog4j.configurationFile=resources/log4j2.xml -jar build/libs/SheetsIO.jar application.Main`
* `java -jar build/libs/SheetsIO.jar application.Main`

## Contributing
Test your changes and ensure code/documentation styles match.
