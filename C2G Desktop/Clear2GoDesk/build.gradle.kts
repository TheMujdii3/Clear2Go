plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()


}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    //implementation("com.google.firebase:firebase-analytics")
    //implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-common")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-database:21.0.0")
    //implementation("com.google.firebase:firebase-auth:23.0.0")
    //implementation("com.google.firebase:firebase-firestore:25.0.0")
    implementation("com.google.firebase:firebase-storage:21.0.0")

}

tasks.test {
    useJUnitPlatform()
}



