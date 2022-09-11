import $repo.`https://oss.sonatype.org/content/repositories/snapshots`
import $ivy.`com.github.yjgbg::json-dsl:0.1-SNAPSHOT`
import com.github.yjgbg.json.JsonDsl.*

writeYaml("pipeline.yaml") {
  "aa"+="xx"
  "aa"+="yy"
}
