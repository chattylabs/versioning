# Project Versioning Plugin

Latest version ![Latest version][01]

This [Gradle Plugin][3] will generate automatically project versions based on your repository's tags and commits.

It is a basic implementation of [Semantic Versioning 2.0.0][1] recommendation and has been initially developed
to adopt the suggested [Google Play publishing scheme][2].

It works with Git version 2.x.


## Why choosing this library?

Because it keeps the process simple, automatic and you can apply the plugin in any gradle-based project.


## How does it work?

The plugin counts the number of commits with `major` or `minor` or `patch` keywords.

Let's say you have the following number of commits in your repository:
<br/>(The keyword here is _[minor-change]_, but you can define your own keywords.)

    "[minor-change] JIRA-01 Initial commit"
    "[minor-change] JIRA-02 Pushing random code"
    
Then, the plugin will generate the version `0.2.0`.
<br/>If you add a new commit with a _[minor-change]_ keyword.

    "[minor-change] JIRA-01 Initial update"
    "[minor-change] JIRA-02 Pushing random code"
    "[minor-change] JIRA-03 Adding a new random feature" <--

Then, the plugin will generate the version `0.3.0`.
<br/>If you add a new commit with a _[patch-change]_ keyword.

    "[minor-change] JIRA-01 Initial update"
    "[minor-change] JIRA-02 Pushing random code"
    "[minor-change] JIRA-03 Adding a new random feature"
    "[patch-change] JIRA-04 Fixing a previous feature" <--
    
Then, the plugin will generate the version `0.3.1`.
<br/>If you add a new commit with a _[minor-change]_ keyword.

    "[minor-change] JIRA-01 Initial update"
    "[minor-change] JIRA-02 Pushing random code"
    "[minor-change] JIRA-03 Adding a new random feature"
    "[patch-change] JIRA-04 Fixing a previous feature"
    "[minor-change] JIRA-05 Adding a new random feature" <--

Then, the plugin will generate the version `0.4.0`.
<br/>(When you *increase* the *minor* version, the patch version *resets to 0*)
<br/>If you add a new commit with a _[major-change]_ keyword.

    "[minor-change] JIRA-01 Initial update"
    "[minor-change] JIRA-02 Pushing random code"
    "[minor-change] JIRA-03 Adding a new random feature"
    "[patch-change] JIRA-04 Fixing a previous feature"
    "[minor-change] JIRA-05 Adding a new random feature"
    "[major-change] JIRA-06 Adding an incompatible or big change" <--

Then, the plugin will generate the version `1.0.0`.
<br/>(When you *increase* the *major* version, the minor and patch version *reset to 0*)

You can define your own keywords like _[feature]_, _[bug]_, _[hotfix]_, etc.

### Important!
Before you can start using the plugin, you must create a *version tag* in your repository.

    git tag <tagname> // "version/0.7.0" for example from master
    git push origin <tagname>


## How to apply?

    plugins {
        
        id 'com.chattylabs.versioning' version '<latest version>'
    }
 
-or-
 
    buildscript {
        
        repositories { 
        
            jcenter() 
            
            // Optional. Access to early versions.
            maven { url "https://dl.bintray.com/chattylabs/maven" }
        }
        
        dependencies {
        
            classpath 'com.chattylabs:versioning:<latest version>'
        }
    }
        
    apply plugin: 'com.chattylabs.versioning'
      
Now, setup the required and optional values in your gradle file.
 
    versioning {
        
        tagPrefix "version/"                // Required
        
        keywords {
        
            major "[incompatible]"          // Optional. By default it never upgrades the major version.
            minor "[feature]"               // Required
            patch "[bug]"                   // Required
        }
    }

The plugin will generate a `version.properties` file within the project's module.
<br/>You should add this file into your `.gitignore` config.
    
    
## How to use?

The following functions are available:

    versioning.name()                   // i.e. generates "0.1.0"  - string
    versioning.code()                   // i.e. generates 000100   - integer
    versioning.computedVersionCode()    // i.e. generates 725      - integer (based on the total number of commits)

To update to the last project version run:

    ./gradlew pullVersion
    
To create a new project version tag run:

    ./gradlew pushVersion
    

## Where to use?

The followings are some platform examples:

**Web**

    version = versioning.name()

**Android**
     
    android {
        
        defaultConfig {
        
            [..]
            
            versionCode versioning.computedVersionCode()
            versionName versioning.name()
        }
    }
    
**iOS**
 
    afterEvaluate {
        
        // Use some (JVM) plist library such as Apache's common configuration http://commons.apache
        .org/proper/commons-configuration/ to update info.plist
        
        infoPlist.set(CFBundleShortVersionString, versioning.name())
        infoPlist.set(CFBundleVersion, "${versioning.computedVersionCode()}")
    }


## Notes


**Setup**

You must **create an initial version tag in your repository** following the `tagPrefix` you have setup and 
the `current version` of your project. Otherwise the build will throw an Exception.
    
```bash
git tag <tagPrefix + current version>   // i.e. "version/0.1.0" or "v0.1.0"
 
git push origin <tagPrefix + current version>
```


**Increasing versions**

The only rule to increase the version is that you add the keywords you have configured into the commit message.
<br/>If you push commits without the keywords, it won't be counted as a version upgrade.


**Continuous Integration**

If you are running the update through a **Continuous Integration** system, and you want to get 
the generated version, you can use the following bash function to read the _version.properties_.

    function retrieveVersion() {
      if [ -f "$file" ]
      then
        printf "\"$file\" found."
        while IFS='=' read -r key value
        do
          eval ${key}=\${value}
        done < "$file" &> /dev/null
      else
        printf "\"$file\" not found."
      fi
    }
     
    file="./app/version.properties"
     
    # Example of use
     
    ./gradlew :app:pullVersion :app:assemble
     
    retrieveVersion  // <-- The function
     
    versionName="${major#0}.${minor#0}.${patch#0}"
     
    # Publish to Google Play Store / Apple Store / Bintray / Fastlane / HockeyApp ...
     
    # To create a new version on the reposiroty. Needs repository write access.
    ./gradlew :app:pushVersion 
     
    printf "New version <${versionName}> published successfuly"


**Multiple modules**

If you have several projects/modules into the same repository, and you want to generate a different 
version per project/module, you only need to establish a specific `tagPrefix` per module/version.
<br/><br/>You also have to distinguish the projects/modules commits by a unique **keyword**.

    versioning {
            
        tagPrefix "${project.name}-version/"      // Use a unique prefix per each module
            
        keywords {
        
            ...
            
            minor "[${project.name}-feature]"     // Use a unique keyword for the commits
        }
    }
    
**Extras**

You can setup a different `.git` folder by applying the following option:

    versioning {

        git { // Optional. You can specify a custom .git folder path
        
            dir new File("../another/different/.git/folder")
        }
    }

&nbsp;

[01]: https://api.bintray.com/packages/chattylabs/maven/versioning/images/download.svg?label=Latest%20version
[02]: https://coveralls.io/repos/chattylabs/versioning/badge.svg?branch=master&service=github
[1]: https://semver.org/
[2]: https://developer.android.com/google/play/publishing/multiple-apks#VersionCodes
[3]: https://plugins.gradle.org/plugin/com.chattylabs.versioning