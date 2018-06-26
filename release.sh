#!/bin/bash
# fail if any commands fails
set -e
# debug log
set -x

if [ $# -lt 5 ]; then
    echo "You need to provide 5 arguments"
    exit 1
fi

main_branch=$1
version_tag_prefix=$2
final_version=$3
mapping_path=$4
version_upgrade_message=$5

IFS=\. read major minor patch <<< "$final_version"

if [[ ! -z "$mapping_path" ]]; then
    # Update internal stored version
    rm -f -r ./${version_tag_prefix}
    mkdir -p ./${version_tag_prefix}/${final_version}/mapping/release/
    cp ${mapping_path} ./${version_tag_prefix}/${final_version}/mapping/release/
fi

# Push version changes
git status
git add .
git commit -m "<$version_tag_prefix/$final_version> $version_upgrade_message"
git tag "$version_tag_prefix/$final_version"
git push --tags origin HEAD:${main_branch}