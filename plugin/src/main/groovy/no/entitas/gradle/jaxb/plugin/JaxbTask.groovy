/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package no.entitas.gradle.jaxb.plugin

import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction

/**
 * Generates XML Binding classes from a set of XML Schemas.
 *
 * @author Stig Kleppe-Jørgensen
 */
public class JaxbTask extends SourceTask {
    /**
     * The classpath containing the Ant XJC task implementation.
     * <p>
     * This is implemented dynamically from the task's convention mapping setup in <code>JaxbPlugin</code>
     *
     * @see JaxbPlugin
     */
    @InputFiles
    FileCollection getJaxbClasspath() {
        null
    }

    /**
     * The directory to generate the parser source files into
     */
    @OutputDirectory
    File outputDirectory

    /**
     * File or directory with binding definitions (usually *.xjb files).
     * Default: src/${sourceSet.name}/xjb/*.xjb
     */
    def bindings = ""

    /**
     * Catalog file.
     */
    def catalog = ""

    /**
     * Specifying a target package via this command-line option overrides any binding customization for package name
     * and the default package name algorithm defined in the specification.
     */
    def packageName = "";

    @TaskAction
    def generate() {
        // TODO how to get to the task's SourceSetOutput which holds the resources dir
        def metaInfDirectory = "${project.buildDir}/resources/main/META-INF"
        project.file(metaInfDirectory).mkdirs()

        def xjcLibs = jaxbClasspath + project.configurations.antextension
        // TODO maybe have another classpath that holds classes needed when compiling generated code and not include
        // this in the jaxb configuration, which should only hold classpath necessary for running xjc
        ant.taskdef(name: 'xjc', classname: 'no.entitas.gradle.jaxb.antextension.XJCTask', classpath: xjcLibs.asPath)

        ant.xjc(
                extension: true,
                binding: bindings,
                catalog: catalog,
                destdir: outputDirectory,
                packageName: packageName,
                classpath: project.configurations.compile.asPath) {
            source.addToAntBuilder(ant, 'schema', FileCollection.AntType.FileSet)
            arg(value: '-episode')
            arg(value: "${metaInfDirectory}/sun-jaxb.episode")
        }
    }

}
