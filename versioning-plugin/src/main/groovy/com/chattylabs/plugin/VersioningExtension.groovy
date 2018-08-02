package com.chattylabs.plugin

import com.chattylabs.plugin.util.GitUtil
import com.chattylabs.plugin.model.GitSettings
import com.chattylabs.plugin.model.LogKeywords
import com.chattylabs.plugin.model.Version
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory

class VersioningExtension {

    String mTagPrefix
    private LogKeywords mLogKeywords
    private GitSettings mGitSettings

    private Version mVersion
    private Project mProject
    private Boolean mIsInitialized = false

    VersioningExtension(Version version, Project project, ObjectFactory objectFactory) {
        mVersion = version
        mProject = project
        mLogKeywords = objectFactory.newInstance(LogKeywords)
        mGitSettings = objectFactory.newInstance(GitSettings)
        GitUtil.setGitDir(new File("./"))
    }

    private void initializeIfRequired() {
        synchronized (this) {
            if (!mIsInitialized) {
                new VersioningTask(mProject).execute(mVersion.needsInitialUpdate(), false)
                mIsInitialized = true
            }
        }
    }

    String name() {
        initializeIfRequired()
        return mVersion.toName()
    }

    int code() {
        initializeIfRequired()
        return mVersion.toCode()
    }

    Version version() {
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
