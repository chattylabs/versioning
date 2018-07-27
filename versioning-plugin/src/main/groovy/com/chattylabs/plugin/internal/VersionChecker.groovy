package com.chattylabs.plugin.internal

import com.chattylabs.plugin.util.GitUtil
import org.gradle.api.tasks.StopExecutionException

class VersionChecker {

    private String mLastTagCommit = null

    VersionChecker(String lastTagCommit) {
        mLastTagCommit = lastTagCommit
    }

    Tuple2<Integer, String> calculateVersion(String[] keywords,
                                             String fromCommit,
                                             boolean optional) {
        if (keywords == null || keywords.length == 0) {
            if (optional) {
                return new Tuple2<Integer, String>(0, mLastTagCommit)
            } else {
                throw new StopExecutionException("Please provide the list of (log) keywords for Major, Minor and Patch.")
            }
        }

        final List<String> commitList = GitUtil.getCommitList(keywords, fromCommit) ?: new ArrayList<String>()
        return new Tuple2<Integer, String>(commitList.size(), commitList.size() > 0 ? commitList.get(0) : mLastTagCommit)
    }

    Tuple2<Integer, String> calculateVersion(String[] keywords,
                                             String fromCommit) {
        return calculateVersion(keywords, fromCommit, false)
    }

    Tuple2<Integer, String> calculateVersion(String[] keywords) {
        return calculateVersion(keywords, mLastTagCommit, false)
    }

    Tuple2<Integer, String> calculateVersion(String[] keywords, boolean optional) {
        return calculateVersion(keywords, mLastTagCommit, optional)
    }
}