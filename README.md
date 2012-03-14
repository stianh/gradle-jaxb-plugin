Gradle JAXB plugin
==================

This is a Gradle plugin for generating JAXB classes with the xjc compiler from the given schemas.

Usage:
------

    apply plugin: 'jaxb'

    dependencies {
      jaxb 'com.sun.xml.bind:jaxb-xjc:2.2.4-1'
    }

    buildscript {
      repositories {
        mavenCentral()
      }
      dependencies {
        classpath 'no.entitas.gradle:gradle-jaxb-plugin:1.3'
      }
    }


The java package for the generated code is extracted from the instruction jaxb:package inside the schema files. For the
schema includes and the output directory sensible defaults are used:

* Includes for schemas:

        src/<source set>/xsd/**/*.xsd

* Output directory:

        <build dir>/generated-src/jaxb/<source set>

Not many configuration options exists at the moment. You can configure

* the classpath used for generating and compiling

        dependencies {
          // This first line specifies which version of the xjc compiler to use
          jaxb 'com.sun.xml.bind:jaxb-xjc:2.1.12'

          compile '<other dependency which the generated classes need>'
        }

* the jaxb source directory sets

        sourceSets {
            main {
                jaxb {
                    <setup srcDir, includes, excludes, output, etc>
                }
            }
            test {
                jaxb {
                    <setup srcDir, includes, excludes, output, etc>
                }
            }
        }

For a quite advanced example, consider a project having 2 schemas (`schema1.xsd` and `schema2.xsd`) in `src/main/xsd`
generated into 2 different java packages. As the xjc compiler does not support generating into 2 different java packages
in a single run, these 2 schemas must be included from 2 different source sets (the xjc compiler is run once for each
source set):

    sourceSets {
        main {
            jaxb {
                exclude '**/schema1.xsd'
            }
        }
        schema2 {
            jaxb {
                srcDir 'src/main/xsd'    (1)
                exclude '**/schema2.xsd'
            }
        }
    }

    jar {
        from {
            sourceSets.schema2.output    (2)
        }
    }

This will create 2 directories for the generated code, `<build dir>/generated-src/jaxb/main` and
`<build dir>/generated-src/jaxb/schema2`.

The source directory for `schema2` would normally be `src/schema2/xsd`, but is changed to the actual directory
`src/main/xsd` (1).

Finally the compiled output from the `schema2` source set is then included in the jar (2).

Known issues and limitations
----------------------------
* The plugin is not tested extensively, especially with regards to different versions of the xjc compiler
* There are several XJC ant task configurations that are not exposed through the plugin at the moment
