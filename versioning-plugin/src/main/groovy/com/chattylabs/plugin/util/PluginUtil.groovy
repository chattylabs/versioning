package com.chattylabs.plugin.util

import org.gradle.api.Project

class PluginUtil {

    static final String GRADLE_EXTENSION_NAME = "versioning"

    static File getSavedVersionProperty(Project project) {
        return new File("${project.getProjectDir().absolutePath}/versioning.properties");
    }
}
