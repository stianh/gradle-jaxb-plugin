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
package no.entitas.gradle.jaxb

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSet

/**
 * A plugin for adding JAXB support to {@link JavaPlugin java projects}
 *
 * @author Stig Kleppe-Jørgensen
 */
public class JaxbPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.plugins.apply(JavaPlugin)

        project.configurations.add('jaxb') {
            visible = false
            transitive = false
            description = "The JAXB libraries to be used for this project."
        }

        project.configurations.compile {
            extendsFrom project.configurations.jaxb
        }

        project.convention.plugins.java.sourceSets.all { SourceSet sourceSet ->
            setupJaxbFor(sourceSet, project)
        }
    }

    private setupJaxbFor(SourceSet sourceSet, Project project) {
        insertJaxbInto(sourceSet, project)

        Task jaxb = createJaxbTaskFor(sourceSet, project)
        project.tasks[sourceSet.compileJavaTaskName].dependsOn(jaxb)
    }

    private insertJaxbInto(SourceSet sourceSet, Project project) {
        def schemasDir = "src/${sourceSet.name}/jaxb"
        sourceSet.convention.plugins.jaxb = new JaxbSourceDirectory(sourceSet.name, project.fileResolver)
        sourceSet.java { srcDir generatedJavaDirFor(project, sourceSet) }
        sourceSet.jaxb { srcDir schemasDir }
        sourceSet.resources { srcDir schemasDir }
    }

    private Task createJaxbTaskFor(SourceSet sourceSet, Project project) {
        def jaxbTask = project.tasks.add(sourceSet.getTaskName('generate', 'SchemaSource'), JaxbTask)

        jaxbTask.group = 'JAXB'
        jaxbTask.description = "Processes the ${sourceSet.name} JAXB schemas."
        jaxbTask.outputDirectory = generatedJavaDirFor(project, sourceSet)
        jaxbTask.conventionMapping.defaultSource = { sourceSet.jaxb }
        jaxbTask.conventionMapping.jaxbClasspath = {
            def jaxbClassPath = project.configurations.jaxb.copy()
            jaxbClassPath.transitive = true
            jaxbClassPath
        }

        jaxbTask
    }

    private File generatedJavaDirFor(Project project, SourceSet sourceSet) {
        project.file("${project.buildDir}/generated-src/jaxb/${sourceSet.name}")
    }
}
