package com.chattylabs.plugin

import com.chattylabs.plugin.util.PluginUtil
import org.gradle.api.Plugin
import org.gradle.api.Project

class VersioningPlugin implements Plugin<Project> {

    Project mProject

    @Override
    void apply(Project project) {
        mProject = project
        loadVersionProperties()
    }

    def loadVersionProperties() {
        mProject.extensions.create(PluginUtil.GRADLE_EXTENSION_NAME, VersioningExtension.class,
                mProject, mProject.objects)
        mProject.tasks.create("pushVersion", VersionTagCreator.class)
        mProject.tasks.create("pullVersion", UpdateVersionTask.class)
    }
}
