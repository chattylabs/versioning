package com.example.model

class Version {
    private String major
    private String minor
    private String patch
    private String sdk
    private String screen

    Version(Properties properties) {
        major = properties.getProperty("major")
        minor = properties.getProperty("minor")
        patch = properties.getProperty("patch")
        sdk = properties.getProperty("sdk")
        screen = properties.getProperty("screen")
    }

    String getMajorVersion() {
        return major
    }

    String getMinorVersion() {
        return minor
    }

    String getPatchVersion() {
        return patch
    }

    String getSdkVersion() {
        return sdk
    }

    String getScreenVersion() {
        return screen
    }
}
