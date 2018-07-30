package com.chattylabs.plugin

import com.chattylabs.plugin.util.GitUtil
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class VersionTagCreator extends DefaultTask {

    @Override
    String getGroup() {
        return "versioning"
    }

    @TaskAction
    void performAction() {
        def vExtension = project.versioning as VersioningExtension
        def tagName = "${vExtension.tagPrefix()}${vExtension.version().toString()}"
        GitUtil.createTag(tagName)
        GitUtil.pushTags()
    }
}