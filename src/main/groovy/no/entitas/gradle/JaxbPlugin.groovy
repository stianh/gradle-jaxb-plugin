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

        addGeneratedSourceSet(project)
        injectJaxbTask(project)

        project.tasks.jar {
            from project.sourceSets.main.classes
            from project.sourceSets.generatedSources.classes
        }
    }

    private def addGeneratedSourceSet(Project project) {
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

    private def injectJaxbTask(Project project) {
        def jaxbDestDir = new File(project.projectDir, project.convention.plugins.jaxb.destDir)

        Task jaxbDirTask = createJaxbDirTask(project, jaxbDestDir)
        Task jaxbTask = createJaxbTask(project, jaxbDirTask, jaxbDestDir)

        project.tasks.compileGeneratedSourcesJava.dependsOn jaxbTask
    }

    private def createJaxbDirTask(Project project, jaxbDestDir) {
        project.task('jaxbDirs') {
            outputs.upToDateWhen {jaxbDestDir.exists()}
            actions = [{
                jaxbDestDir.mkdirs()
            } as Action]
        }
    }

    private def createJaxbTask(Project project, Task jaxbDirTask, jaxbDestDir) {
        project.task('jaxb', dependsOn: jaxbDirTask) {
            inputs.dir {new File(project.convention.plugins.jaxb.schemaDir)}
            outputs.dir {jaxbDestDir}

            ant.taskdef(name: 'xjc', classname: 'com.sun.tools.xjc.XJCTask', classpath: project.configurations.jaxb.asPath)
            actions = [{
                ant.xjc(extension: true, destdir: project.convention.plugins.jaxb.destDir, package: project.convention.plugins.jaxb.genPackage) {
                    schema(dir: project.convention.plugins.jaxb.schemaDir, includes: project.convention.plugins.jaxb.includes)
                    arg(value: "-verbose")
                }
            } as Action]
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