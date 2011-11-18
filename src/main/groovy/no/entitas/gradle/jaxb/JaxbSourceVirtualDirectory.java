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
package no.entitas.gradle.jaxb;

import groovy.lang.Closure;
import org.gradle.api.file.SourceDirectorySet;

/**
 * Contract for a Gradle "convention object" that acts as a handler for a virtual directory mapping, injecting a virtual
 * directory named 'jaxb' into the project's various {@link org.gradle.api.tasks.SourceSet source sets}.  Its
 * implementation gets pushed onto the {@link org.gradle.api.internal.DynamicObjectAware} portion of the source set
 * under the name 'jaxb'.
 *
 * @author Stig Kleppe-Jørgensen
 */
public interface JaxbSourceVirtualDirectory {
    public static final String NAME = "jaxb";

    /**
     * All JAXB source for this source set
     *
     * @return The JAXB source. Never returns null.
     */
    public SourceDirectorySet getJaxb();

    /**
     * Configures the JAXB source for this set. The given closure is used to configure the {@code SourceDirectorySet}
     * (see {@link #getJaxb}) which contains the JAXB source.
     *
     * @param configureClosure The closure to use to configure the JAXB source
     * @return this
     */
    public JaxbSourceVirtualDirectory jaxb(Closure configureClosure);
}
