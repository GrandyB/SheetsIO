plugins {
    application
    `java-library`
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("com.gradleup.shadow") version "8.3.6"
}

javafx {
    version = "17"
    modules("javafx.controls", "javafx.fxml", "javafx.graphics")
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    api(libs.com.google.code.gson.gson)
    api(libs.commons.io.commons.io)
    api(libs.javax.validation.validation.api)
    api(libs.org.hibernate.validator.hibernate.validator)
    api(libs.org.hibernate.validator.hibernate.validator.annotation.processor)
    api(libs.javax.el.javax.el.api)
    api(libs.org.glassfish.web.javax.el)
    api(libs.org.apache.logging.log4j.log4j.api)
    api(libs.org.apache.logging.log4j.log4j.core)
    api(libs.org.mockito.mockito.all)
    api(libs.org.greenrobot.eventbus)

    compileOnly(libs.org.projectlombok.lombok)
    annotationProcessor(libs.org.projectlombok.lombok)
    testAnnotationProcessor(libs.org.projectlombok.lombok)

    testImplementation(libs.org.junit.jupiter.junit.jupiter.api)
    testImplementation(libs.org.junit.jupiter.junit.jupiter.engine)
    testImplementation(libs.org.junit.platform.junit.platform.launcher)
    testImplementation(libs.org.testfx.testfx.core)
    testImplementation(libs.org.testfx.testfx.junit5)
    testImplementation(libs.org.hamcrest.hamcrest)
}

application {
    mainClass.set("application.Launcher")
}

tasks.withType<JavaCompile> {
    options.isFork = true
    options.forkOptions.jvmArgs = mutableListOf(
        "--add-opens", "jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
        "--add-opens", "jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED",
        "--add-opens", "jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
        "--add-opens", "jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED",
        "--add-opens", "jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED",
        "--add-opens", "jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED",
        "--add-opens", "jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED",
        "--add-opens", "jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
        "--add-opens", "jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED"
    )
}

tasks.processResources {
    from("src/main/java") {
        include("**/*.css", "**/*.png", "**/*.properties", "**/*.xml", "**/*.fxml")
    }
}

tasks.shadowJar {
    archiveBaseName.set("SheetsIO")
    archiveClassifier.set("")
    archiveVersion.set("")
    mergeServiceFiles()
    exclude("module-info.class")
    manifest {
        attributes["Main-Class"] = "application.Launcher"
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}