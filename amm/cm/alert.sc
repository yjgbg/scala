// Ammonite 2.5.4-33-0af04a5b, Scala 3.2.0
import $ivy.`mysql:mysql-connector-java:8.0.30`
import $ivy.`org.scalikejdbc::scalikejdbc:4.0.0`
import $ivy.`com.softwaremill.sttp.tapir::tapir-sttp-client:1.1.1`
val user = "root"
val password = "M79jpqu"
val url = "jdbc:mysql://172.25.111.12:31191/hypersdspmgmt?useUnicode=true&characterEncoding=utf8&allowMultiQueries=true"
import scalikejdbc._
Class.forName("com.mysql.cj.jdbc.Driver")
ConnectionPool.singleton(url, user, password)

given session: AutoSession.type = AutoSession

case class Tag(id: Long, name: String)

object Tags extends SQLSyntaxSupport[Tag] {
  override val tableName = "tag"
  def apply(rs: WrappedResultSet) = new Tag(rs.long("id"), rs.string("name"))
}

val tagsListInDb: List[Tag] = sql"select id,name from tag where status = 0".map(rs => Tags(rs)).list.apply()
import java.io.{FileWriter,FileReader}
// read 出磁盘上存储的文件
import java.nio.file.{Files,Paths}
println(s"tagsListInDb.size=${tagsListInDb.size}")
val lines = try {
  Files.readAllLines(Paths.get("saved.csv"))
} catch
  case _: java.nio.file.NoSuchFileException => java.util.List.of()
println(s"linesInDisk.size = ${lines.size}")
val tagsIdInDisk = lines.stream().map(x => x.toLong).collect(java.util.stream.Collectors.toList)
val diff = tagsListInDb.filter(it => !tagsIdInDisk.contains(it.id))
// 发送报警请求
import sttp.client3._
val backend = HttpClientSyncBackend()
if(!diff.isEmpty && tagsIdInDisk.size > 0) {
  println("以下tag已被停用")
  diff.foreach(println)
  println(basicRequest.post(uri"https://alert.hypers.cc/send?app=tag_alert")
    .contentType("application/json")
    .body(s"""{"content" : "${diff.map(_.toString).mkString("\n")}"}""").send(backend).body)
  println("----------")
}
// 写出文件
val writer = new FileWriter("saved.csv")
writer.write(tagsListInDb.map(x => x.id).mkString("\n"))
writer.flush()
writer.close()
