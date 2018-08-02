package com.chattylabs.plugin.util

import org.gradle.api.Project

class PluginUtil {

    static final String GRADLE_EXTENSION_NAME = "versioning"

    static final String TASK_GROUP = "versioning"

    static File getSavedVersionProperty(Project project) {
        return new File("${project.getProjectDir().absolutePath}/version.properties")
    }
}
