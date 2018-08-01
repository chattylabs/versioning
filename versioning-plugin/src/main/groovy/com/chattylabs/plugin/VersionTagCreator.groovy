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
        def tagName = generateTagName()
        if (!GitUtil.isTagExists(tagName)) {
            GitUtil.createTag(tagName)
            GitUtil.pushTags(tagName)
            println "Tag $tagName created."
        }
    }

    String generateTagName() {
        def vExtension = project.versioning as VersioningExtension
        def version = vExtension.version()
        return "${vExtension.tagPrefix()}" +
                "${version.getMajor().toInteger()}." +
                "${version.getMinor().toInteger()}." +
                "${version.getPatch().toInteger()}"
    }
}