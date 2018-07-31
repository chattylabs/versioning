package com.chattylabs.plugin

import com.chattylabs.plugin.model.Version
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
                Version.load(PluginUtil.getSavedVersionProperty(mProject)), mProject, mProject.objects)
        mProject.tasks.create("releaseVersion", VersionTagCreator.class)
    }
}
