package sample.gradle.plugin

import org.gradle.testkit.runner.GradleRunner

import org.junit.Rule
import org.junit.rules.TemporaryFolder

import static org.gradle.testkit.runner.TaskOutcome.*

import spock.lang.Specification

class SamplePluginTest extends Specification {
    @Rule final TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile
    def String buildLibsPath
    
    def setup() {
        buildFile  = testProjectDir.newFile('build.gradle')
        
        def buildLibsPathResource = getClass().classLoader.findResource("build-libs-path.txt")
        if (buildLibsPathResource == null) {
            throw new IllegalStateException("Did not find build-libs-path.txt, make sure createExtraTestRuntimeData was executed.")
        }
        
        buildLibsPath = "'" + buildLibsPathResource.readLines().join("', '") + "'"
    }

    def "testing sample plugin"() {
        given:
        buildFile << """
            buildscript {
                repositories {
                    jcenter()
                    flatDir {
                        dirs ${buildLibsPath}
                    }
                }
                dependencies {
                    classpath group: 'sample', name: 'sample-gradle-plugin', version: '0.1.0-SNAPSHOT', changing: true
                }
            }
            
            version = '1.0.0'
            
            apply plugin: 'sample.sample-gradle-plugin'
            
            task dummy << {
                println "Hello Gradle?"
            }
        """

        when:
        def result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments('--info', '--stacktrace', 'dummy')
            .build()

        then:
        println result.output
        result.output.contains('BUILD SUCCESSFUL')
    }
}
