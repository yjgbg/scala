// Ammonite 2.5.4-33-0af04a5b, Scala 3.2.0
import $repo.`https://oss.sonatype.org/content/repositories/snapshots`
import $ivy.`com.github.yjgbg::json-dsl:0.2-SNAPSHOT`
import com.github.yjgbg.json.KubernetesStrictDsl.*
namespace("has") {
  val name = "tag-alert"
  ammoniteCronJob(
    name = "tag-alert",
    scriptPath = "cm/alert.sc",
    schedule = "1/3 * * * *",
    suspend = false
  )
}
