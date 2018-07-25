package com.chattylabs.plugin

import com.chattylabs.plugin.internal.VersionChecker
import com.chattylabs.plugin.model.LogKeywords
import com.chattylabs.plugin.util.GitUtil
import com.chattylabs.plugin.util.PluginUtil
import com.chattylabs.plugin.util.StringUtil
import org.gradle.api.Project
import org.gradle.api.tasks.StopExecutionException

class VersioningTask {

    private VersioningExtension mVersioningExtension = null
    private String mVersionPrefix = null
    private LogKeywords mLogKeywords = null
    private String mCurrentVersion = null
    private Project mProject = null

    VersioningTask(Project project) {
        this.mProject = project
        mVersioningExtension = (project.property(PluginUtil.GRADLE_EXTENSION_NAME) as VersioningExtension)
        mVersionPrefix = mVersioningExtension.tagPrefix() ?: ""
        mLogKeywords = mVersioningExtension.keywords()
    }

    void execute() {
        readCurrentVersionTag()
        calculateNewVersion()
    }

    private void readCurrentVersionTag() {
        def versionPattern = "([0-99](\\.[0-99]){2})"
        def prefix = mVersionPrefix.replace("/", "\\/")
        def regEx = "^tags\\/${prefix}${versionPattern}.*"
        GitUtil.describeTags(mVersionPrefix, {
            if (it.replace("\n", "").matches(regEx)) {
                mCurrentVersion = it.replace("\n", "")
                        .replaceFirst("^tags\\/${prefix}${versionPattern}.*", "\$1")
            } else {
                throw new StopExecutionException("There is no such repository version. " +
                        "Have you forgotten to create the first version tag?")
            }
        })
    }

    private void calculateNewVersion() {
        final VersionChecker versionChecker = new VersionChecker("$mVersionPrefix$mCurrentVersion")
        int[] currentVersion = StringUtil.splitVersion(mCurrentVersion)
        def newMajorVersion = versionChecker.calculateVersion(mLogKeywords.getMajor(), true)
        def newMinorVersion = versionChecker.calculateVersion(mLogKeywords.getMinor(), newMajorVersion.getSecond())
        def newPatchVersion = versionChecker.calculateVersion(mLogKeywords.getPatch(), newMinorVersion.getSecond())
        mVersioningExtension.version().setMajor(currentVersion[0] + newMajorVersion.getFirst())
        mVersioningExtension.version().setMinor(currentVersion[1] + newMinorVersion.getFirst())
        mVersioningExtension.version().setPatch(currentVersion[2] + newPatchVersion.getFirst())
        mVersioningExtension.version().save(PluginUtil.getSavedVersionProperty(this.mProject))

        println("new Version --- ${mVersioningExtension.version().toString()}")
    }
}
