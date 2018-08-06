package com.chattylabs.plugin.model

import com.chattylabs.plugin.VersioningTask
import com.chattylabs.plugin.util.PluginUtil
import com.chattylabs.plugin.util.StringUtil
import org.gradle.api.Project

class Version {

    private static final int PREFIX_DIGIT_LEN = 2

    static String MAJOR = "major"
    static String MINOR = "minor"
    static String PATCH = "patch"
    static String SDK = "sdk"
    static String SCREEN = "screen"

    private final Map<String, String> mVersionMap = new HashMap<>()

    Version(Properties properties) {
        mVersionMap.put(MAJOR, properties.getProperty(MAJOR))
        mVersionMap.put(MINOR, properties.getProperty(MINOR))
        mVersionMap.put(PATCH, properties.getProperty(PATCH))
        mVersionMap.put(SDK, properties.getProperty(SDK))
        mVersionMap.put(SCREEN, properties.getProperty(SCREEN))
    }

    String getMajor() {
        return mVersionMap.get(MAJOR)
    }

    void setMajor(int major) {
        mVersionMap.put(MAJOR, StringUtil.formatNumber(major, PREFIX_DIGIT_LEN))
    }

    String getMinor() {
        return mVersionMap.get(MINOR)
    }

    void setMinor(int minor) {
        mVersionMap.put(MINOR, StringUtil.formatNumber(minor, PREFIX_DIGIT_LEN))
    }

    String getPatch() {
        return mVersionMap.get(PATCH)
    }

    void setPatch(int patch) {
        mVersionMap.put(PATCH, StringUtil.formatNumber(patch, PREFIX_DIGIT_LEN))
    }

    String getSdk() {
        return mVersionMap.get(SDK)
    }

    void setSdk(int sdk) {
        mVersionMap.put(SDK, StringUtil.formatNumber(sdk, PREFIX_DIGIT_LEN))
    }

    String getScreen() {
        return mVersionMap.get(SCREEN)
    }

    void setScreen(int screen) {
        mVersionMap.put(SCREEN, StringUtil.formatNumber(screen, PREFIX_DIGIT_LEN))
    }

    Map<String, String> getAll() {
        return mVersionMap.asImmutable()
    }

    String toName() {
        return "${this.major.toInteger()}.${this.minor.toInteger()}.${this.patch.toInteger()}"
    }

    int toCode() {
        return Integer.parseInt("${getSdk()}${getScreen()}${getMajor()}${getMinor()}${getPatch()}")
    }

    static def load(Project project) {
        File file = PluginUtil.getSavedVersionProperty(project)
        if (!file.exists()) {
            // File doesn't exist. Fetch the current tag and create property file from it.
            def propertiesToWrite = new Properties()
            propertiesToWrite.load(Version.class.getResourceAsStream("/file/default_version.properties"))
            Version currentVersion = new Version(propertiesToWrite)
            new VersioningTask(project).initializeVersionProperties(currentVersion)
            return currentVersion
        }

        def properties = new Properties()
        properties.load(new FileInputStream(file))
        return new Version(properties)
    }

    def save(File file) {
        def properties = new Properties()
        properties.putAll(getAll())
        properties.store(new FileOutputStream(file), "Version Update")
    }
}
