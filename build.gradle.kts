plugins {
    id("java")
}

group = "com.greenatom"
version = "1.0.0-bw"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks {
    jar {
        manifest {
            attributes("Main-Class" to "com.greenatom.navybattle.Main")
        }
    }
}