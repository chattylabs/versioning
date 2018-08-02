package com.chattylabs.plugin

import com.chattylabs.plugin.internal.VersionChecker
import com.chattylabs.plugin.model.LogKeywords
import com.chattylabs.plugin.util.GitUtil
import com.chattylabs.plugin.util.PluginUtil
import com.chattylabs.plugin.util.StringUtil
import com.chattylabs.plugin.util.VersionHandler
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

    void execute(boolean initialUpdate, boolean shouldUpdateVersion) {
        if (initialUpdate || shouldUpdateVersion) {
            readCurrentVersionTag()
            doInitialUpdate()
        }

        if (shouldUpdateVersion) {
            processNewVersionElements(getNewVersionElements())
        }
    }

    private void doInitialUpdate() {
        Integer[] currentVersion = StringUtil.splitVersion(mCurrentVersion)
        mVersioningExtension.version().setMajor(currentVersion[0])
        mVersioningExtension.version().setMinor(currentVersion[1])
        mVersioningExtension.version().setPatch(currentVersion[2])
        mVersioningExtension.version().save(PluginUtil.getSavedVersionProperty(this.mProject))
    }

    private void readCurrentVersionTag() {
        def versionPattern = "([0-99](\\.[0-99]){2})"
        def prefix = mVersionPrefix.replace("/", "\\/")
        def regEx = "^${prefix}${versionPattern}.*\$"
        GitUtil.fetchAll({
            GitUtil.checkTags(mVersionPrefix, {
                if (it.split("\n")[0].matches(regEx)) {
                    mCurrentVersion = it.replace("\n", "")
                            .replaceFirst(regEx, "\$1")
                    println "Current Version: $mCurrentVersion"
                } else {
                    throw new StopExecutionException("There is no such repository version. " +
                            "Have you forgotten to create the first version tag?")
                }
            })
        })
    }

    private void processNewVersionElements(List<Integer> newVersionElements) {
        if (newVersionElements.shouldUpdateProperties()) {
            Integer[] currentVersion = StringUtil.splitVersion(mCurrentVersion)
            VersionHandler versionHandler = new VersionHandler(currentVersion)
            versionHandler.increaseVersionBy(0, newVersionElements[0])
            versionHandler.increaseVersionBy(1, newVersionElements[1])
            versionHandler.increaseVersionBy(2, newVersionElements[2])
            mVersioningExtension.version().setMajor(versionHandler.getVersion(0))
            mVersioningExtension.version().setMinor(versionHandler.getVersion(1))
            mVersioningExtension.version().setPatch(versionHandler.getVersion(2))
            mVersioningExtension.version().save(PluginUtil.getSavedVersionProperty(this.mProject))
            println("Upgraded version from ${currentVersion} to ${mVersioningExtension.version().toName()}.")
        }
    }

    private List<Integer> getNewVersionElements() {
        final VersionChecker versionChecker = new VersionChecker("$mVersionPrefix$mCurrentVersion")
        def newMajorVersion = versionChecker.calculateVersion(mLogKeywords.getMajor(), true)
        def newMinorVersion = versionChecker.calculateVersion(mLogKeywords.getMinor(), newMajorVersion.getSecond())
        def newPatchVersion = versionChecker.calculateVersion(mLogKeywords.getPatch(), newMinorVersion.getSecond())
        def newVersionElements = [newMajorVersion.getFirst(), newMinorVersion.getFirst(), newPatchVersion.getFirst()]
        newVersionElements.metaClass.shouldUpdateProperties = {
            for (int element : delegate) {
                if (element > 0) {
                    return true
                }
            }

            return false
        }
        return newVersionElements
    }

}
