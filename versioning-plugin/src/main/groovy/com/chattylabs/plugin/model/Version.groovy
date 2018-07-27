package com.chattylabs.plugin.model

import com.chattylabs.plugin.util.StringUtil

class Version {

    private static final int PREFIX_DIGIT_LEN = 2

    static String MAJOR = "major"
    static String MINOR = "minor"
    static String PATCH = "patch"
    static String SDK = "sdk"
    static String SCREEN = "screen"

    private final Map<String, String> versionMap = new HashMap<>()

    Version(Properties properties) {
        versionMap.put(MAJOR, properties.getProperty(MAJOR))
        versionMap.put(MINOR, properties.getProperty(MINOR))
        versionMap.put(PATCH, properties.getProperty(PATCH))
        versionMap.put(SDK, properties.getProperty(SDK))
        versionMap.put(SCREEN, properties.getProperty(SCREEN))
    }

    String getMajor() {
        return versionMap.get(MAJOR)
    }

    void setMajor(int major) {
        versionMap.put(MAJOR, StringUtil.formatNumber(major, PREFIX_DIGIT_LEN))
    }

    String getMinor() {
        return versionMap.get(MINOR)
    }

    void setMinor(int minor) {
        versionMap.put(MINOR, StringUtil.formatNumber(minor, PREFIX_DIGIT_LEN))
    }

    String getPatch() {
        return versionMap.get(PATCH)
    }

    void setPatch(int patch) {
        versionMap.put(PATCH, StringUtil.formatNumber(patch, PREFIX_DIGIT_LEN))
    }

    String getSdk() {
        return versionMap.get(SDK)
    }

    void setSdk(int sdk) {
        versionMap.put(SDK, StringUtil.formatNumber(sdk, PREFIX_DIGIT_LEN))
    }

    String getScreen() {
        return versionMap.get(SCREEN)
    }

    void setScreen(int screen) {
        versionMap.put(SCREEN, StringUtil.formatNumber(screen, PREFIX_DIGIT_LEN))
    }

    Map<String, String> getAll() {
        return versionMap.asImmutable()
    }

    String toName() {
        return "${this.major.toInteger()}.${this.minor.toInteger()}.${this.patch.toInteger()}"
    }

    int toCode() {
        return Integer.parseInt("${getSdk()}${getScreen()}${getMajor()}${getMinor()}${getPatch()}")
    }

    static def load(File file) {
        if (!file.exists()) {
            def propertiesToWrite = new Properties()
            propertiesToWrite.load(Version.class.getResourceAsStream("/file/default_version.properties"))
            propertiesToWrite.store(new FileOutputStream(file), "Default write")
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
