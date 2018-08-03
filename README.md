# Software Versioning Plugin

![Coverage Status][02] &nbsp; ![Latest version][01]

[Gradle Plugin][3] that versions automatically any software utilising git tags.
It works across any platform and any environment that uses **Gradle** as its build automation system and **Git**
as its distributed version control system.

It is based on the [Semantic Versioning 2.0.0][1] recommendation and has been initially developed
to adopt the suggested [Google Play publishing scheme][2].


## Why choosing this library?

Because it keeps the process simple.

The problem with the various versioning plugins out there is that they are very complex to configure
due to the many options they provide and the time it takes to read all the documentation.

Regardless of the branching strategy you follow (Git Flow, Trunk based development, ...), this plugin 
will generate the accurate version based on the amount of commit messages containing a **major, minor or 
patch keyword** since the last version tag.

That's it.

It's up to you where and when to trigger the upgrade. Nevertheless, it is appropriate to upgrade either 
after you merge a PR or when you integrate any changes onto your `master` branch. Ideally this should
happen through a continuous integration system.

Besides, it works very well with multi-module projects, various libraries in the same repository
and add-on based components, see the end of this README file for more info.


## How to apply?

    plugins {
        
        id 'com.chattylabs.versioning' version '<latest version>'
            
    }
 
-or-
 
    buildscript {
        
        repositories { jcenter() }
        
        dependencies {
            classpath 'com.chattylabs:versioning:<latest version>'
        }
    }
        
    apply plugin: 'com.chattylabs.versioning'
      
Configure the required and optional values in your gradle file
 
    versioning {
        
        tagPrefix "version/"            // Optional. Default is empty.
        
        keywords {
            major "[incompatible]"      // Optional. By default it never upgrades the major version.
            minor "[feature]"           // Required
            patch "[bug]", "[patch]"    // Required
        }
    }
    
    
## How to use?

**<u>Once the plugin is applied</u>, build the project.**

The version name and version code are now available wherever you need them.

    project.version = versioning.name() // generates "0.1.0"  - string

_Android example_
     
    android {
        
        defaultConfig {
        
            ...
            
            versionCode versioning.code() // generates 000100   - int
            versionName versioning.name() // generates "0.1.0"  - string
        }
    }
    
_iOS example_
 
    afterEvaluate {
        
        // Use some (JVM) plist library such as Apache's common configuration http://commons.apache
        .org/proper/commons-configuration/ to update info.plist
        
        infoPlist.set(CFBundleShortVersionString, versioning.name())
        infoPlist.set(CFBundleVersion, "${versioning.code()}")
        
    }

When you are ok with the final version, your tests have passed, and you are ready to release, then run

```bash
gradle releaseVersion // This will store a new tag on your remote repository as of <tagPrefix><version>
```
**We recommend strongly to only run this through a continuous integration server like CircleCi, Travis, Bitrise, etc...**


## Important Notes

You must **create an initial version tag in your repository** with the `tagPrefix` and the `current version` of the project. 
Otherwise the build will throw an Exception.
    
```bash
git tag <tagPrefix + current version>   // i.e. "version/0.1.0" or "v0.1.0"
 
git push origin <tagPrefix + current version>
```

The versioning configuration block must be applied before to use its exposed methods.

    versioning {
        
        // you must include and configure this block before to call versioning.code() or versioning.name()
        
    }
     
    project.version = versioning.name() // now it works, otherwise it will throw an Exception


The only rule to increase the version is that you add the proper keyword into the commit message

```bash
git commit -m "ISSUE-99 [bug] ..." // This will increase the patch version - "0.1.1"
 
git commit -m "ISSUE-100 [feature] ..." // This will increase the minor version - "0.2.0"
 
...
```

If you have various projects/modules into the same repository, and you want to generate a different 
version per project/module, you only need to establish a specific `tagPrefix` per module/version.
<br/>Also you have to differentiate the projects/modules commits by a unique **keyword**.

    versioning {
            
        tagPrefix "${project.name}-version/"      // Use a unique prefix for multiple modules
            
        keywords {
        
            ...
            
            minor "[${project.name}-feature]"     // Use a unique keyword for the commits
        }
    }

&nbsp;

[01]: https://api.bintray.com/packages/chattylabs/maven/versioning/images/download.svg?label=Latest%20version
[02]: https://coveralls.io/repos/chattylabs/versioning/badge.svg?branch=master&service=github
[1]: https://semver.org/
[2]: https://developer.android.com/google/play/publishing/multiple-apks#VersionCodes
[3]: https://plugins.gradle.org/plugin/com.chattylabs.versioning