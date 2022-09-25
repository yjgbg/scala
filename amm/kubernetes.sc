// Ammonite 2.5.4-33-0af04a5b, Scala 3.2.0
import $repo.`https://oss.sonatype.org/content/repositories/snapshots`
import $ivy.`com.github.yjgbg::json-dsl:0.1-SNAPSHOT`

import com.github.yjgbg.json.KubernetesDsl.*

namespace("has") {
  for (name <- Seq("server0","server1")) {
    gradleSubProjectSpringBootWebApp(
      name = "has-mgr-be",
      replicas = 1L,
      ports = Seq(13080),
      image = "reg2.hypers.cc/dockerhub.com",
      jarPath = "/opt/has-mgr-be.jar",
      javaVersion = 17,
      env = Seq()
    ){}
  }
}

