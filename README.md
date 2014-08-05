# This plugin is not maintained anymore; look at [jacobono's](https://github.com/jacobono/gradle-jaxb-plugin) instead.


## Gradle JAXB plugin

This is a Gradle plugin for generating JAXB classes with the xjc compiler from the given schemas. It also supports
catalog and episode files for separate compilation.

### Usage

    apply plugin: 'jaxb'

    dependencies {
      jaxb 'com.sun.xml.bind:jaxb-xjc:2.2.4-1'
    }

    buildscript {
      repositories {
        mavenCentral()
      }
      dependencies {
        classpath 'no.entitas.gradle.jaxb:gradle-jaxb-plugin:2.0'
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


Separate compilation
--------------------

To be able to reference external schemas without specifying the location directly, you should put a
`src/main/xsd/catalog.cat` file in the project. This file can be in several formats ([XML Catalog, TR9401 Catalogs and
XCatalogs](http://docs.oracle.com/cd/E17802_01/webservices/webservices/docs/1.6/jaxb/catalog.html)), but the simplest
is the line based format (TR9401 Catalog).

It should contain the link between the namespace or schema location and the actual schema file. More info in the
[unofficial JAXB guide](http://jaxb.java.net/guide/Fixing_broken_references_in_schema.html). Here is a couple examples:

    PUBLIC "http://test.com/schema/1/simple" "path/to/simple.xsd"
    SYSTEM "http://test.com/schema/1/simple/simple.xsd" "path/to/simple.xsd"

The built in catalog resolver in xjc resolves the path to the schema relative to the catalog.cat file. This does not
work very well when doing separate compilation where these files are in different projects. Therefore the plugin
enables lookup of schemas through the classpath using a resolver modeled after the one in the
[Maven JAXB plugin](http://confluence.highsource.org/display/MJIIP/User+Guide):

    PUBLIC "http://test.com/schema/1/simple" "classpath:simple.xsd"

This will lookup `simple.xsd` from classpath.

If you have several schemas, you could put

    SYSTEM "http://test.com/schema/1/simple/simple1.xsd" "classpath:simple1.xsd"
    SYSTEM "http://test.com/schema/1/simple/simple2.xsd" "classpath:simple2.xsd"

in your catalog file. To simplify this, use `REWRITE_SYSTEM`:

    REWRITE_SYSTEM "http://test.com/schema/1/simple" "classpath:"

and it will resolve all references below `http://test.com/schema/1/simple` from `classpath:`.

Every project that applies this plugin will get an episode file generated under `<build dir>/META-INF/sun-jaxb.episode`.
This is the default location for where the xjc compiler will look, so there is no need to specify anything to get xjc to
use it for separate compilation.

Further explanation of this can be found at the [inaugural source in Kohsuke Kawaguchi's blog]
(http://weblogs.java.net/blog/kohsuke/archive/2006/09/separate_compil.html).


Known issues and limitations
----------------------------
* The separate compilation is not really separate yet. This is not enabled because of various problems of getting it to
  work properly, especially when having Java package specification in the schema files
* The plugin is not tested extensively, especially with regards to different versions of the xjc compiler
* There are several XJC ant task configurations that are not exposed through the plugin at the moment


[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/stephanheinze/gradle-jaxb-plugin/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

