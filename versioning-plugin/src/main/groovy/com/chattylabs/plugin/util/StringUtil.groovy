package com.chattylabs.plugin.util

class StringUtil {

    static Integer[] splitVersion(String version) {
        return [version.find("^\\d+").toInteger(),
                version.find("\\.\\d+\\.").find("\\d+").toInteger(),
                version.find("\\d+\$").toInteger()]
    }

    static String formatNumber(int number, int prefixDigit){
        return String.format("%0${prefixDigit}d", number)
    }
}