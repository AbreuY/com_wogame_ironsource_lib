# com_wogame_ironsource_lib


buildscript {
    ext.kotlin_version = '1.3.21'
    repositories {
        google()
        maven { url 'http://maven.aliyun.com/nexus/content/groups/public/' }
        maven { url 'https://dl.bintray.com/umsdk/release' } //需要添加
        maven { url "http://dl.bintray.com/ironsource-mobile/android-adapters" }//需要添加
        jcenter()
        
    }
}
allprojects {
    repositories {
        google()
        maven { url 'http://maven.aliyun.com/nexus/content/groups/public/' }
        maven { url 'https://dl.bintray.com/umsdk/release' }//需要添加
        maven { url "http://dl.bintray.com/ironsource-mobile/android-adapters"}//需要添加
        jcenter()
        
    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}

ext {
    unityLibrary = "../StackBallResLibs/libs"
}
