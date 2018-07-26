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
        this.mVersion = version
        this.mProject = project
        mLogKeywords = objectFactory.newInstance(LogKeywords)
        mGitSettings = objectFactory.newInstance(GitSettings)
        GitUtil.setGitDir(new File("./"))
    }

    String name() {
        initializeIfRequired()
        return this.mVersion.toString()
    }

    int code() {
        initializeIfRequired()
        return this.mVersion.toCode()
    }

    void tagPrefix(String prefix) {
        mTagPrefix = prefix
    }

    String tagPrefix() {
        return mTagPrefix
    }

    void initializeIfRequired() {
        synchronized (this) {
            if (!mIsInitialized) {
                new VersioningTask(mProject).execute()
                mIsInitialized = true
            }
        }
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

    Version version() {
        return mVersion
    }
}
