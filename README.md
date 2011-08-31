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

    buildscript {
      repositories {
        mavenLocal()
      }
      dependencies {
        classpath group: 'no.entitas', name: 'gradle-jaxb-plugin', version: '1.0'
      }
    }

    // Configuration of the plugin
    jaxb {
      destDir = 'build/generated-src' // This is the default directory for generated sources
      genPackage =  'no.entitas.jaxb'
      schemaDir = 'src/main/resources/no/entitas/schema'
      includes = 'some.xsd'
    }

Known issues and limitations:  
----------------------------
* The plugin is only tested in some very simple cases.   
* There are several XJC ant task configurations that are not exposed through the plugin at the moment.