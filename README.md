Gradle JAXB plugin
==================

This is a Gradle plugin for generating JAXB classes with XJC out of the given schemas.

Installation 
------------
1. Clone the repo
2. Run the following command:

        gradle install

Usage:  
------

    apply plugin: 'jaxb'

    dependencies {
      jaxb 'com.sun.xml.bind:jaxb-xjc:2.1.12'
    }

    buildscript {
      repositories {
        mavenLocal()
      }
      dependencies {
        classpath group: 'no.entitas', name: 'gradle-jaxb-plugin', version: '1.1-SNAPSHOT'
      }
    }

There is no configuration possible at the moment other than specifying the classpath used when generating and compiling
the generated code. Which schema files to include and output directory are using sensible defaults, while the package
to use is taken from the jaxb:package instruction in the schema files.

Known issues and limitations:  
----------------------------
* The plugin is only tested in some very simple cases.   
* There are several XJC ant task configurations that are not exposed through the plugin at the moment.