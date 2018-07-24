package com.chattylabs.plugin.internal

import java.util.regex.Pattern

class CommandUtil {

    static List<String> processCommands(String template, String[] input = null) {
        def commands = null
        if (input != null) {
            def specifierFinder = Pattern.compile('%(\\d+)\\$s')
            commands = new ArrayList<String>()
            template.split(" ").each {
                def command = it
                def matcher = specifierFinder.matcher(it)
                while (matcher.find()) {
                    command = command.replaceFirst("%${matcher.group(1)}\\\$s",
                            disableRegExChars(input[matcher.group(1).toInteger() - 1]))
                }
                commands.add(command)
            }
        }

        // TODO: Add a logger and print (helpful)
//        println "Commands : $commands -- template ${template}"
        return commands ?: template.split(" ").toList()
    }

    static String disableRegExChars(String text) {
        return text.replace("[","\\\\[")
        .replace("]", "\\\\]")
    }

    static String formGrepTemplate(int startIndex, int length) {
        def grepTemplate = new StringBuilder()
        (startIndex..(startIndex + length - 1)).each {
            grepTemplate.append("--grep %$it\$s")
        }

        return grepTemplate.replaceAll("(?!^)--grep", " --grep").toString()
    }
}
