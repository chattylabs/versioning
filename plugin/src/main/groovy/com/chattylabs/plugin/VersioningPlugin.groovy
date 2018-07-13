package com.chattylabs.plugin

import com.chattylabs.plugin.model.Version
import org.gradle.api.Plugin
import org.gradle.api.Project

class VersioningPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        loadVersionProperties(project)
    }

    def loadVersionProperties(Project module) {
        def vProp = new File("${module.getProjectDir().absolutePath}/versioning.properties")
        if (!vProp.exists()) {
            createPropertyIfRequired(vProp)
        }

        // Load Versioning
        def versioning = module.extensions.create("Versioning", VersioningExtension.class,
                new Version(getVersionProperties(vProp)))

        module.afterEvaluate {
            // TODO : Handle versioning settings
        }
    }

    def createPropertyIfRequired(File outFile) {
        def propertiesToWrite = new Properties()
        propertiesToWrite.load(this.class.getResourceAsStream("/file/default_version.properties"))
        propertiesToWrite.store(new FileOutputStream(outFile), "Default write")
    }

    private static Properties getVersionProperties(File inFile) {
        def properties = new Properties()
        properties.load(new FileInputStream(inFile))
        return properties
    }
}
