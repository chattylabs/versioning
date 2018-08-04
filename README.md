# Software Versioning Plugin

![Coverage Status][02] &nbsp; ![Latest version][01]

[Gradle Plugin][3] that versions automatically a project by a repository git tag.
It works across any platform and any environment that uses **Gradle** as its build automation system and **Git**
as its distributed version control system.

It is based on the [Semantic Versioning 2.0.0][1] recommendation and has been initially developed
to adopt the suggested [Google Play publishing scheme][2].


## Why choosing this library?

Because it keeps the process simple.

The problem with the various versioning plugins out there is that they are very complex to configure
due to the many options they provide and the time it takes to read all the documentation.

Regardless of the branching strategy you follow (Git Flow, Github Flow, Trunk based, ...), this plugin 
will generate the version based on the amount of **commits** with a _major_, _minor_ or 
_patch_ keyword message.

That's it.


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
      
Setup the required and optional values in your gradle file.
 
    versioning {
        
        tagPrefix "version/"            // Optional. Default is empty.
        
        keywords {
            major "[incompatible]"      // Optional. By default it never upgrades the major version.
            minor "[feature]"           // Required
            patch "[bug]", "[patch]"    // Required
        }
    }
    
    
## How to use?

You must apply the `versioning { }` block before calling the functions and tasks

    versioning.name()   // generates "0.1.0"  - string
    versioning.code()   // generates 000100   - integer

To update the version

    ./gradlew updateVersion
    
To increase the version

    git commit -m "ISSUE-99 [bug] ..." // This will increase in 1 the patch version
    
To save the new version

    ./gradlew releaseVersion


## Where?

_Web_

    version = versioning.name()  // generates "0.1.0"

_Android_
     
    android {
        
        defaultConfig {
        
            ...
            
            versionCode versioning.code() // generates 000100   - int
            versionName versioning.name() // generates "0.1.0"  - string
        }
    }
    
_iOS_
 
    afterEvaluate {
        
        // Use some (JVM) plist library such as Apache's common configuration http://commons.apache
        .org/proper/commons-configuration/ to update info.plist
        
        infoPlist.set(CFBundleShortVersionString, versioning.name())
        infoPlist.set(CFBundleVersion, "${versioning.code()}")
        
    }


## Important Notes

You must **create an initial version tag in your repository** following the `tagPrefix` and 
the `current version` of the project. 
<br/>Otherwise the build will throw an Exception.
    
```bash
git tag <tagPrefix + current version>   // i.e. "version/0.1.0" or "v0.1.0"
 
git push origin <tagPrefix + current version>
```


The only rule to increase the version is that you add the proper keyword into the commit message

```bash
git commit -m "ISSUE-99 [bug] ..." // This will increase the patch version - "0.1.1"
 
git commit -m "ISSUE-100 [feature] ..." // This will increase the minor version - "0.2.0"
 
...
```

If you have various projects/modules into the same repository, and you want to generate a different 
version per project/module, you only need to establish a specific `tagPrefix` per module/version.
<br/>You also have to distinguish the projects/modules commits by a unique **keyword**.

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