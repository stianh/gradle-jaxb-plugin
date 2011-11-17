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
package no.entitas.gradle.jaxb.internal;

import groovy.lang.Closure;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.file.DefaultSourceDirectorySet;
import org.gradle.api.internal.file.FileResolver;
import no.entitas.gradle.jaxb.JaxbSourceVirtualDirectory;
import org.gradle.util.ConfigureUtil;

/**
 * The implementation of the {@link JaxbSourceVirtualDirectory} contract
 *
 * @author Stig Kleppe-Jørgensen
 */
public class DefaultJaxbSourceVirtualDirectory implements JaxbSourceVirtualDirectory {
    private final SourceDirectorySet jaxb;

    public DefaultJaxbSourceVirtualDirectory(String parentDisplayName, FileResolver fileResolver) {
        final String displayName = String.format("%s jaxb source", parentDisplayName);
        jaxb = new DefaultSourceDirectorySet(displayName, fileResolver);
        jaxb.getFilter().include("**/*.xsd");
    }

    public SourceDirectorySet getJaxb() {
        return jaxb;
    }

    public JaxbSourceVirtualDirectory jaxb(Closure configureClosure) {
        ConfigureUtil.configure(configureClosure, getJaxb());
        return this;
    }
}
