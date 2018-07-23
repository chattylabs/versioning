package com.chattylabs.plugin

import com.chattylabs.plugin.model.Version
import com.chattylabs.plugin.model.VersionSettings
import org.gradle.api.Plugin
import org.gradle.api.Project

class VersioningPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        loadVersionProperties(project)
    }

    def loadVersionProperties(Project project) {
        def vProp = new File("${project.getProjectDir().absolutePath}/versioning.properties")

        // Load Versioning
        def versioning = project.extensions.create("versioning", VersioningExtension.class,
                Version.load(vProp))

        project.afterEvaluate {
            // TODO : Handle versioning settings
        }

        project.tasks.create("upgrade", VersioningTask.class)
    }
}
