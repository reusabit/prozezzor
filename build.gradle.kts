/**
Copyright 2021 Reusabit Software LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

import org.apache.tools.ant.taskdefs.condition.Os

plugins {
  id("org.jetbrains.kotlin.jvm") version "1.4.21"
  //id("org.openjfx.javafxplugin") version "0.0.9"
  id("com.github.jk1.dependency-license-report") version "1.16"
  id("com.install4j.gradle") version "8.0.10"
  application
}

repositories {
  // Use JCenter for resolving dependencies.
  jcenter()
  mavenCentral()
  maven(url = "https://kotlin.bintray.com/kotlinx")
}

dependencies {
  implementation("org.slf4j:slf4j-api:1.7.30")
  implementation("ch.qos.logback:logback-classic:1.2.3")
  implementation("ch.qos.logback:logback-core:1.2.3")
  implementation("org.codehaus.groovy:groovy-all:3.0.8")

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

//Workaround for https://github.com/gradle/kotlin-dsl-samples/issues/1368
//per https://stackoverflow.com/questions/55456176/unresolved-reference-compilekotlin-in-build-gradle-kts
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
  kotlinOptions {
    jvmTarget = "11"
  }
}


val isWindows = Os.isFamily(Os.FAMILY_WINDOWS)
val isMac = Os.isFamily(Os.FAMILY_MAC)


application {
  // Define the main class for the application.
  mainClass.set("com.reusabit.prozezzor.ProzezzorKt")
}

licenseReport {
  //Outputs in build/reports/dependency-license
  renderers = arrayOf(com.github.jk1.license.render.TextReportRenderer("third-party-licenses.txt"))
}

install4j {
  if (isWindows)
    installDir = file("""C:\Program Files\install4j9""")
  else if (isMac)
    installDir = file("""/Applications/install4j.app""")
}

// This doesn't work anymore because a password is required for the code signing cert. Easiest way is to just
// run the installer gui.
tasks.create("buildInstaller", com.install4j.gradle.Install4jTask::class.java) {
  projectFile = file("install.install4j")
  dependsOn(":installDist")

  if (project.hasProperty("macKeystorePassword"))
    macKeystorePassword = project.property("macKeystorePassword") as String


}

