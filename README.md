# com_wogame_ironsource_lib

2020/7/24 18:26:24 

参考文档 [https://developers.ironsrc.com/ironsource-mobile/android/admob-mediation-guide/#step-6](https://developers.ironsrc.com/ironsource-mobile/android/admob-mediation-guide/#step-6)

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

