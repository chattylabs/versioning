#!/usr/bin/env bash
# fail if any commands fails
set -e
# debug log
#set -x

if [ $# -lt 3 ]; then
    echo "You need to provide 3 arguments"
    exit 1
fi

DEFAULT_VERSION="0.1.0"

version_prefix=$1
feature_commit_pattern=$2
patch_commit_pattern=$3

# Update all
git fetch --all --prune &> /dev/null

original_version=""
is_new=false

# Check for versions
git rev-parse HEAD &> /dev/null
project=`git describe --tags --all --always --long --match "$version_prefix[0-99]*" HEAD`

if [[ ${project} =~ .*$version_prefix[0-99]+\.[0-99]+\.[0-99]+.* ]]; then
  original_version=${project##tags/}
  original_version=${original_version##"$version_prefix"}
  original_version=${original_version%%-*}
fi
if [[ -z "$original_version" ]]; then
  original_version=${DEFAULT_VERSION}
  is_new=true
else
  last_version_commit_id=`git rev-list -n 1 "$version_prefix$original_version"`
fi

IFS=\. read major minor patch <<< "$original_version"

number_of_features=0
number_of_patches=0

if [[ "$is_new" != true ]]; then
  # It does contain previous versions
  number_of_features=`git rev-list ${last_version_commit_id}..HEAD \
  --grep "$feature_commit_pattern" --count`
  if [[ ${number_of_features} == 0 ]]; then
    # No previous features
    number_of_patches=`git rev-list ${last_version_commit_id}..HEAD \
    --grep "$patch_commit_pattern" --count`
  else
    last_feature_commit_id=`git rev-list -1 --grep \
    "$feature_commit_pattern" ${last_version_commit_id}..HEAD --abbrev-commit`
    number_of_patches=`git rev-list ${last_feature_commit_id}..HEAD \
    --grep "$patch_commit_pattern" --count`
  fi
fi

minor=$(($minor + $number_of_features))
patch=$(($patch + $number_of_patches))

final_version="$major.$minor.$patch"

./gradlew doUpdateVersion -Pkey=minor -Pvalue=${minor} --quiet &> /dev/null
./gradlew doUpdateVersion -Pkey=patch -Pvalue=${patch} --quiet &> /dev/null

echo "$final_version|$original_version"
