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

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class JaxbPluginTest extends Specification {
    private final Project project = ProjectBuilder.builder().build()
    private final JaxbPlugin plugin = new JaxbPlugin()

    def "should add JAXB dir to each source set"() {
        when:
        plugin.apply(project)

        then:
        def main = project.sourceSets.main
        main.jaxb.srcDirs == [project.file('src/main/xsd')] as Set
        main.resources.srcDirs == [project.file('src/main/xsd'), project.file('src/main/resources')] as Set

        def test = project.sourceSets.test
        test.jaxb.srcDirs == [project.file('src/test/xsd')] as Set
        test.resources.srcDirs == [project.file('src/test/xsd'), project.file('src/test/resources')] as Set

        when:
        project.sourceSets.create('custom')

        then:
        def custom = project.sourceSets.custom
        custom.jaxb.srcDirs == [project.file('src/custom/xsd')] as Set
        custom.resources.srcDirs == [project.file('src/custom/xsd'), project.file('src/custom/resources')] as Set
    }

    def "should add configuration named jaxb"() {
        when:
        plugin.apply(project)

        then:
        project.configurations.jaxb
    }

    def "should add JAXB generate task for each source set"() {
        when:
        plugin.apply(project)

        then:
        def main = project.tasks.generateSchemaSource
        main instanceof JaxbTask
        project.tasks.compileJava.taskDependencies.getDependencies(null).contains(main)

        def test = project.tasks.generateTestSchemaSource
        test instanceof JaxbTask
        project.tasks.compileTestJava.taskDependencies.getDependencies(null).contains(test)

        when:
        project.sourceSets.create('custom')

        then:
        def custom = project.tasks.generateCustomSchemaSource
        custom instanceof JaxbTask
        project.tasks.compileCustomJava.taskDependencies.getDependencies(null).contains(custom)
    }

}
