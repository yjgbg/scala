// Ammonite 2.5.4-33-0af04a5b, Scala 3.2.0
import $repo.`https://oss.sonatype.org/content/repositories/snapshots`
import $ivy.`com.github.yjgbg::json-dsl:0.1-SNAPSHOT`
import com.github.yjgbg.json.KubernetesDsl.*

namespace("has") {
  val name = "tag-alert"
  val script = "alert.sc"
  configMapFromDir(name,"cm")
  cronJob(name) {
    _apiVersion := "batch/v1"
    _spec ::= {
      "schedule" := "1/3 * * * *"
      "jobTemplate" ::= {
        _spec ::= {
          "template" ::= {
            _spec ::= {
              "restartPolicy" := "Never"
              val vol = "vol"
              "volumes" ++= {
                "name" := vol
                "hostPath" ::= {
                  "path" := s"/mnt/data/cronJob/$name"
                }
              }
              "volumes" ++= {
                "name" := name
                "configMap" ::= {"name" := name}
              }
              "containers" ++= {
                val workspace = "/workspace"
                _name := name
                _image := "yjgbg/ammonite:2.5.4-3.2-cross"
                "workingDir" := workspace
                "imagePullPolicy" := "IfNotPresent"

                Seq("sh", "-c",s"amm /script/$script").foreach(it =>
                  "command" += it
                )
                Map(
                  "COURSIER_CACHE" -> "/workspace/.coursier/",
                ).foreach((k, v) => "env" ++= { "name" := k; "value" := v })
                "volumeMounts" ++= {
                  _name := vol
                  "mountPath" := workspace
                }
                "volumeMounts" ++= {
                  _name := name
                  "mountPath" := "/script"
                }
              }
            }
          }
        }
      }
    }
  }
}
