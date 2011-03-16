Gradle JAXB plugin
==================
Gradle plugin for generating jaxb classes.  

Installation 
------------
Clone the repo  
run: gradle clean install  

Usage:  
------

<pre><code>
apply plugin: 'jaxb'

buildscript {
  repositories {
    mavenRepo urls: ['file://' + new File(System.getProperty('user.home'), '.m2/repository').absolutePath]
  }
  dependencies {
    classpath group: 'no.entitas', name: 'gradle-jaxb-plugin', version: '1.0-SNAPSHOT'
  }
}
//Configures the plugin 
jaxb{
  String destDir = 'build/generated-src' //This is the default. 
  genPackage =  'no.entitas.jaxb'
  schemaDir = 'src/main/resources/no/entitas/schema'
  includes = 'some.xsd'
}

</code></pre>  

Known issues and limitations:  
----------------------------
* The plugin is only tested in some very simple cases.   
* There are several XJC ant task configurations that are not exposed through the plugin at the moment.  
* The plugin is not integrated with gradle's incremental build support.