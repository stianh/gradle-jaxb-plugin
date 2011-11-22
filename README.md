Gradle JAXB plugin
==================

This is a Gradle plugin for generating JAXB classes with XJC from the given schemas.

Usage:
------

    apply plugin: 'jaxb'

    dependencies {
      jaxb 'com.sun.xml.bind:jaxb-xjc:2.1.12'
    }

    buildscript {
      repositories {
        mavenCentral()
      }
      dependencies {
        classpath 'no.entitas.gradle:gradle-jaxb-plugin:1.3'
      }
    }

There is no configuration possible at the moment other than specifying the classpath used for generating and compiling
the generated code. The java package for the generated classes is extracted from the instruction jaxb:package inside
the schema files  by the xjc compiler. For the rest sensible defaults has been put in place:

* Includes for schemas:

        src/<source set>/jaxb/**/*.xsd

* Output directory:

        <build dir>/generated-src/jaxb/<source set>


Which schema files to use and output directory are using sensible defaults, while the package
to use is taken from the jaxb:package instruction in the schema files.

Known issues and limitations:  
----------------------------
* The plugin is not tested extensively
* There are several XJC ant task configurations that are not exposed through the plugin at the moment