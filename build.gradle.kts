import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.3.41"
}

group = "me.mazeika.pure"
version = "0.1.0"

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.slf4j:slf4j-simple:1.7.28")

    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.3.2")
}

application {
    mainClassName = "me.mazeika.pure.PureKt"
}

val test by tasks.getting(Test::class) {
    useJUnitPlatform {}
}

val run: JavaExec by tasks
run.standardInput = System.`in`

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs = listOf("-XXLanguage:+InlineClasses")
}
