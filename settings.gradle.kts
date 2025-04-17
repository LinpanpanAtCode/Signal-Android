import java.security.cert.X509Certificate
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.net.ssl.SSLContext
import javax.net.ssl.HttpsURLConnection

pluginManagement {
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
  }
  includeBuild("build-logic")
}
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
    mavenLocal()
    maven {
      url = uri("https://raw.githubusercontent.com/signalapp/maven/master/sqlcipher/release/")
      content {
        includeGroupByRegex("org\\.signal.*")
      }
    }
    maven {
      url = uri("https://dl.cloudsmith.io/qxAgwaeEE1vN8aLU/mobilecoin/mobilecoin/maven/")
    }
  }
  versionCatalogs {
    // libs.versions.toml is automatically registered.
    create("benchmarkLibs") {
      from(files("gradle/benchmark-libs.versions.toml"))
    }
    create("testLibs") {
      from(files("gradle/test-libs.versions.toml"))
    }
    create("lintLibs") {
      from(files("gradle/lint-libs.versions.toml"))
    }
  }
}

plugins {
  id("com.gradle.develocity") version "3.19.2"
}

// settings.gradle.kts 中添加：

fun disableSslVerification() {
  try {
    val trustAllCerts = arrayOf<TrustManager>(
      object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
      }
    )

    val sslContext = SSLContext.getInstance("SSL")
    sslContext.init(null, trustAllCerts, java.security.SecureRandom())
    HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.socketFactory)
    HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }

    println("✅ SSL verification disabled for all HTTPS connections")
  } catch (e: Exception) {
    println("⚠️ Failed to disable SSL verification: ${e.message}")
  }
}

disableSslVerification()

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

// To build libsignal from source, set the libsignalClientPath property in gradle.properties.
val libsignalClientPath = if (extra.has("libsignalClientPath")) extra.get("libsignalClientPath") else null
if (libsignalClientPath is String) {
  includeBuild(rootDir.resolve(libsignalClientPath + "/java")) {
    name = "libsignal-client"
    dependencySubstitution {
      substitute(module("org.signal:libsignal-client")).using(project(":client"))
      substitute(module("org.signal:libsignal-android")).using(project(":android"))
    }
  }
}

include(":app")
include(":libsignal-service")
include(":lintchecks")
include(":paging")
include(":paging-app")
include(":core-util")
include(":core-util-jvm")
include(":glide-config")
include(":device-transfer")
include(":device-transfer-app")
include(":image-editor")
include(":image-editor-app")
include(":donations")
include(":donations-app")
include(":spinner")
include(":spinner-app")
include(":contacts")
include(":contacts-app")
include(":qr")
include(":qr-app")
include(":sticky-header-grid")
include(":photoview")
include(":core-ui")
include(":benchmark")
include(":microbenchmark")
include(":video")
include(":video-app")
include(":billing")

project(":app").name = "Signal-Android"
project(":paging").projectDir = file("paging/lib")
project(":paging-app").projectDir = file("paging/app")

project(":device-transfer").projectDir = file("device-transfer/lib")
project(":device-transfer-app").projectDir = file("device-transfer/app")

project(":image-editor").projectDir = file("image-editor/lib")
project(":image-editor-app").projectDir = file("image-editor/app")

project(":donations").projectDir = file("donations/lib")
project(":donations-app").projectDir = file("donations/app")

project(":spinner").projectDir = file("spinner/lib")
project(":spinner-app").projectDir = file("spinner/app")

project(":contacts").projectDir = file("contacts/lib")
project(":contacts-app").projectDir = file("contacts/app")

project(":qr").projectDir = file("qr/lib")
project(":qr-app").projectDir = file("qr/app")

project(":video").projectDir = file("video/lib")
project(":video-app").projectDir = file("video/app")

rootProject.name = "Signal"


