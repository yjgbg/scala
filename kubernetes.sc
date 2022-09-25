import $repo.`https://oss.sonatype.org/content/repositories/snapshots`
import $ivy.`com.github.yjgbg::json-dsl:0.1-SNAPSHOT`
import com.github.yjgbg.json.KubernetesDsl.*
namespace("has") {
  for (name <- Seq("server0","server1")) {
    deployment(name) {
      _apiVersion := _appV1
      _spec ::= {
        "replicas" := 1
        "selector" ::= {
          "matchLabels" ++= {
            "app" := name
          }
        }
        "template" ::= {
          _metadata ::= {
            _labels("app" -> name)
          }
          _spec ::= {
            "containers" ++= {
              "image" := "alpine"
            }
          }
        }
      }
    }
  }
}

