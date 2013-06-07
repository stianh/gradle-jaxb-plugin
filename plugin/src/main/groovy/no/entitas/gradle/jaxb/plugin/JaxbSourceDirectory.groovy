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

import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.internal.file.FileResolver

/**
 * A handler for a virtual directory mapping, injecting a virtual directory named 'jaxb' into the project's various
 * {@link org.gradle.api.tasks.SourceSet source sets}.
 *
 * @author Stig Kleppe-Jørgensen
 */
class JaxbSourceDirectory {

    /**
     * All JAXB source for this source set
     */
    def SourceDirectorySet jaxb
    def String bindings

    JaxbSourceDirectory(String parentDisplayName, FileResolver fileResolver) {
        jaxb = new DefaultSourceDirectorySet("${parentDisplayName} JAXB source", fileResolver)
        jaxb.filter.include("**/*.xsd")
    }

    /**
     * Configures the JAXB source for this set. The given closure is used to configure the {@code SourceDirectorySet}
     * (see {@link #jaxb}) which contains the JAXB source.
     *
     * @param configuration The closure to use to configure the JAXB source
     * @return this
     */
    JaxbSourceDirectory jaxb(Closure configuration) {
        bindings = configuration.getProperty("bindings")
        configuration.delegate = jaxb
        configuration.call()

        this
    }
}
