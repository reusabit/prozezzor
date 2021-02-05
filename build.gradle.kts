plugins {
  id("org.jetbrains.kotlin.jvm") version "1.4.21"
  //id("org.openjfx.javafxplugin") version "0.0.9"
  id ("com.github.jk1.dependency-license-report") version "1.16"
  application
}

repositories {
  // Use JCenter for resolving dependencies.
  jcenter()
  mavenCentral()
  maven(url = "https://kotlin.bintray.com/kotlinx")
}

dependencies {
  // Align versions of all Kotlin components
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

  // Use the Kotlin JDK 8 standard library.
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

  // This dependency is used by the application.
  implementation("com.google.guava:guava:29.0-jre")

  // Use the Kotlin test library.
  testImplementation("org.jetbrains.kotlin:kotlin-test")

  // Use the Kotlin JUnit integration.
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

  testImplementation("org.assertj:assertj-core:3.17.2")

  implementation("com.github.ajalt.clikt:clikt:3.1.0")

  implementation("org.apache.poi:poi:5.0.0")

  implementation("org.apache.poi:poi-ooxml:5.0.0")

  //implementation("org.openjfx:javafx:15.0.1")
}

application {
  // Define the main class for the application.
  mainClass.set("com.reusabit.prozezzor.ProzezzorKt")
}

licenseReport {
  //Outputs in build/reports/dependency-license
  renderers = arrayOf(com.github.jk1.license.render.TextReportRenderer("third-party-licenses.txt"))
}