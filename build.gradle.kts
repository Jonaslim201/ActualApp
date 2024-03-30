import java.util.regex.Pattern.compile

buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.1")
    }


    repositories{
        google()
        mavenCentral()
    }

}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.1" apply false
}