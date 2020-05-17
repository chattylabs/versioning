package com.chattylabs.plugin

import com.chattylabs.plugin.util.GitUtil
import com.chattylabs.plugin.model.GitSettings
import com.chattylabs.plugin.model.LogKeywords
import com.chattylabs.plugin.model.Version
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory

class VersioningExtension {

    private String mTagPrefix
    private LogKeywords mLogKeywords
    private GitSettings mGitSettings
    private Version mVersion
    private Project mProject

    VersioningExtension(Project project, ObjectFactory objectFactory) {
        mProject = project
        mLogKeywords = objectFactory.newInstance(LogKeywords)
        mGitSettings = objectFactory.newInstance(GitSettings)
        GitUtil.setGitDir(new File("./"))
    }

    private void initialize() {
        synchronized (this) {
            if (mVersion == null) {
                // Load Version From Property File
                mVersion = Version.load(mProject)
            }
        }
    }

    String name() {
        return version().toName()
    }

    int code() {
        return version().toCode()
    }

    int computedVersionCode() {
        return version().toComputedVersionCode()
    }

    Version version() {
        if (mVersion == null) {
            initialize()
        }
        return mVersion
    }

    void tagPrefix(String prefix) {
        mTagPrefix = prefix
    }

    String tagPrefix() {
        return mTagPrefix
    }

    void keywords(Action<? super LogKeywords> kAction) {
        kAction.execute(mLogKeywords)
    }

    LogKeywords keywords() {
        return mLogKeywords
    }

    void git(Action<? super GitSettings> gitAction) {
        gitAction.execute(mGitSettings)
        GitUtil.setGitDir(mGitSettings.dir())
    }

    GitSettings git() {
        return mGitSettings
    }
}
