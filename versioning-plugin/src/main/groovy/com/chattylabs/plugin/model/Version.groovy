package com.chattylabs.plugin.model

class Version {

    static String MAJOR = "major"
    static String MINOR = "minor"
    static String PATCH = "patch"
    static String SDK = "sdk"
    static String SCREEN = "screen"

    private final Map<String, String> versionMap = new HashMap<>()

    Version(Properties properties) {
        versionMap.put(MAJOR, properties.getProperty(MAJOR, "0"))
        versionMap.put(MINOR, properties.getProperty(MINOR, "1"))
        versionMap.put(PATCH, properties.getProperty(PATCH, "0"))
        versionMap.put(SDK, properties.getProperty(SDK, "0"))
        versionMap.put(SCREEN, properties.getProperty(SCREEN, "0"))
    }

    String getMajor() {
        return versionMap.get(MAJOR)
    }

    void setMajor(int major) {
        versionMap.put(MAJOR, "$major".toString())
    }

    String getMinor() {
        return versionMap.get(MINOR)
    }

    void setMinor(int minor) {
        versionMap.put(MINOR, "$minor".toString())
    }

    String getPatch() {
        return versionMap.get(PATCH)
    }

    void setPatch(int patch) {
        versionMap.put(PATCH, "$patch".toString())
    }

    String getSdk() {
        return versionMap.get(SDK)
    }

    void setSdk(int sdk) {
        versionMap.put(SDK, "$sdk".toString())
    }

    String getScreen() {
        return versionMap.get(SCREEN)
    }

    void setScreen(int screen) {
        versionMap.put(SCREEN, "$screen".toString())
    }

    Map<String, String> getAll() {
        return versionMap.asImmutable()
    }

    @Override
    String toString() {
        return "${this.major}.${this.minor}.${this.patch}"
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
        properties.putAll(versionMap)
        properties.store(new FileOutputStream(file), "Version Update")
    }
}
