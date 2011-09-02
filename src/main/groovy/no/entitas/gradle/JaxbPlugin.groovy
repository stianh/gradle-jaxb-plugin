package no.entitas.gradle

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class JaxbPlugin implements Plugin<Project> {
    def void apply(Project project) {
        project.apply(plugin: 'java')
        project.convention.plugins.jaxb = new JaxbPluginConvention()
        project.configurations {jaxb}
        project.dependencies {
            jaxb 'com.sun.xml.bind:jaxb-xjc:2.1.12'
        }
        addSourceSet(project)
        Task createJaxbDir = project.task('createDirs') << {
            new File(project.projectDir, project.convention.plugins.jaxb.destDir).mkdirs()
        }

        createJaxbDir.outputs.upToDateWhen {new File(project.projectDir, project.convention.plugins.jaxb.destDir).exists()}

        Task jaxbTask = project.task('jaxb', dependsOn: createJaxbDir) {
            inputs.dir {new File(project.convention.plugins.jaxb.schemaDir)}
            outputs.dir {new File(project.projectDir, project.convention.plugins.jaxb.destDir)}
            ant.taskdef(name: 'xjc', classname: 'com.sun.tools.xjc.XJCTask', classpath: project.configurations.jaxb.asPath)
            actions = [
                    {
                        ant.xjc(extension: true, destdir: project.convention.plugins.jaxb.destDir, package: project.convention.plugins.jaxb.genPackage) {
                            schema(dir: project.convention.plugins.jaxb.schemaDir, includes: project.convention.plugins.jaxb.includes)
                        }
                    } as Action]
        }

        project.tasks.compileGeneratedSourcesJava.dependsOn jaxbTask

        project.tasks.jar {
            from project.sourceSets.main.classes
            from project.sourceSets.generatedSources.classes
        }
    }

    def addSourceSet(Project project) {
        project.sourceSets {
            generatedSources {
                java {
                    srcDir 'build/generated-src'
                }
            }
            main {
                compileClasspath += generatedSources.classes
            }
            test {
                compileClasspath += generatedSources.classes
                runtimeClasspath += generatedSources.classes
            }
        }
    }
}

class JaxbPluginConvention {
    String destDir = "build/generated-src"
    String genPackage = "gen.src"
    String schemaDir
    String includes

    def jaxb(Closure closure) {
        closure.delegate = this
        closure()
    }
}