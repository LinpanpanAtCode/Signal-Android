pluginManagement {
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
  }
}
plugins {
  id("com.gradle.develocity") version "3.19.2"
}
develocity {
  buildScan {
    termsOfUseUrl = "https://gradle.com/terms-of-service"
    termsOfUseAgree = "yes"
  }
}

buildCache {
  remote<HttpBuildCache> {
    url = uri("https://172.20.14.3/repository/raw-yalla-android-remote-cache-test/")
    isPush = true
    credentials {
      username = "ios" // 替换为您的用户名
      password = "Z7tvVUMU" // 替换为您的密码
    }
    isAllowInsecureProtocol = true // 允许使用不安全的协议 (HTTP)
    isAllowUntrustedServer = true  // 允许连接到证书不受信任的服务器
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
  }
  versionCatalogs {
    create("libs") {
      from(files("../gradle/libs.versions.toml"))
    }
    create("testLibs") {
      from(files("../gradle/test-libs.versions.toml"))
    }
  }
}

rootProject.name = "build-logic"

include(":plugins")
include(":tools")
