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

import java.io.File;

import no.entitas.gradle.jaxb.internal.DefaultJaxbSourceVirtualDirectory;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.internal.DynamicObjectAware;
import org.gradle.api.internal.IConventionAware;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.internal.tasks.DefaultSourceSet;
import org.gradle.api.plugins.Convention;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.ConventionValue;
import org.gradle.api.tasks.SourceSet;
import static org.gradle.api.plugins.JavaPlugin.COMPILE_CONFIGURATION_NAME;

/**
 * A plugin for adding JAXB support to {@link JavaPlugin java projects}
 *
 * @author Stig Kleppe-Jørgensen
 */
// FIXME should convert to groovy; much easier to read groovy code
public class JaxbPlugin implements Plugin<ProjectInternal> {
    public static final String JAXB_CONFIGURATION_NAME = "jaxb";

    public void apply(final ProjectInternal project) {
        project.getPlugins().apply(JavaPlugin.class);

        // set up a configuration named 'jaxb' for the user to specify the JAXB libs to use in case
        // they want a specific version etc.
        Configuration jaxbConfiguration = project.getConfigurations().add(JAXB_CONFIGURATION_NAME).setVisible(false)
                .setTransitive(false).setDescription("The JAXB libraries to be used for this project.");
        project.getConfigurations().getByName(COMPILE_CONFIGURATION_NAME).extendsFrom(jaxbConfiguration);

        // FIXME refactor this messy method
        project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets().all(
                new Action<SourceSet>() {
                    public void execute(SourceSet sourceSet) {
                        // for each source set we will:
                        // 1) Add a new 'jaxb' virtual directory mapping
                        final DefaultJaxbSourceVirtualDirectory jaxbDirectoryDelegate
                                = new DefaultJaxbSourceVirtualDirectory(((DefaultSourceSet) sourceSet).getDisplayName(), project.getFileResolver());
                        ((DynamicObjectAware) sourceSet).getConvention().getPlugins().put(
                                JaxbSourceVirtualDirectory.NAME, jaxbDirectoryDelegate);
                        final String srcDir = String.format("src/%s/jaxb", sourceSet.getName());
                        jaxbDirectoryDelegate.getJaxb().srcDir(srcDir);
                        sourceSet.getAllSource().source(jaxbDirectoryDelegate.getJaxb());

                        // 2) create an JaxbTask for this sourceSet following the gradle
                        //    naming conventions via call to sourceSet.getTaskName()
                        final String taskName = sourceSet.getTaskName("generate", "SchemaSource");
                        JaxbTask jaxbTask = project.getTasks().add(taskName, JaxbTask.class);
                        jaxbTask.setDescription(String.format("Processes the %s jaxb schemas.",
                                sourceSet.getName()));

                        // 3) set up convention mapping for default sources (allows user to not have to specify)
                        jaxbTask.conventionMapping("defaultSource", new ConventionValue() {
                            public Object getValue(Convention convention, IConventionAware conventionAwareObject) {
                                return jaxbDirectoryDelegate.getJaxb();
                            }
                        });

                        // 4) set up convention mapping for handling the 'jaxb' dependency configuration
                        jaxbTask.conventionMapping("jaxbClasspath", new ConventionValue() {
                            public Object getValue(Convention convention, IConventionAware conventionAwareObject) {
                                return project.getConfigurations().getByName(JAXB_CONFIGURATION_NAME).copy()
                                        .setTransitive(true);
                            }
                        });

                        // 5) Set up the jaxb output directory (adding to javac inputs!)
                        final String outputDirectoryName =
                                String.format("%s/generated-src/jaxb/%s", project.getBuildDir(), sourceSet.getName());
                        final File outputDirectory = new File(outputDirectoryName);

                        jaxbTask.setOutputDirectory(outputDirectory);
                        sourceSet.getJava().srcDir(outputDirectory);

                        // 6) register fact that jaxb should be run before compiling
                        project.getTasks().getByName(sourceSet.getCompileJavaTaskName()).dependsOn(taskName);
                    }
                });
    }
}
