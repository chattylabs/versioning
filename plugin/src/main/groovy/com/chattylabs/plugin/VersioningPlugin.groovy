package com.chattylabs.plugin

import com.chattylabs.plugin.model.Version
import com.chattylabs.plugin.util.PluginUtil
import org.gradle.api.Plugin
import org.gradle.api.Project

class VersioningPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        loadVersionProperties(project)
    }

    def loadVersionProperties(Project project) {
        project.extensions.create("versioning", VersioningExtension.class,
                Version.load(PluginUtil.getSavedVersionProperty(project)), project)
    }
}
