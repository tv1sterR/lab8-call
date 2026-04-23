plugins {
    application
}

group = "ru.university"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    // javax.sound.sampled уже есть в JDK, внешние зависимости не нужны
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

application {
    mainClass.set("ru.university.p2p.Main")
}
