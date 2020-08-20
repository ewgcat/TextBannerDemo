# TextBannerDemo

### 集成步骤

    allprojects {
            repositories {
                 google()
                 jcenter()
                 mavenCentral(
                url: "https://ewgcat.bintray.com/utils/"
              )
     }
        
    dependencies {
     api 'com.lsh.textbanner:textbanner:1.0.0'
    }
