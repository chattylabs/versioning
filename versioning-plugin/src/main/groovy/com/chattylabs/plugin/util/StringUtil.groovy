package com.chattylabs.plugin.util

class StringUtil {

    static int[] splitVersion(String version) {
        return [version.find("^\\d+").toInteger(),
                version.find("\\.\\d+\\.").find("\\d+").toInteger(),
                version.find("\\d+\$").toInteger()]
    }
}