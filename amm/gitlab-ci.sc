// Ammonite 2.5.4-33-0af04a5b, Scala 3.2.0
import $repo.`https://oss.sonatype.org/content/repositories/snapshots`
import $ivy.`com.github.yjgbg::json-dsl:0.2-SNAPSHOT`
import com.github.yjgbg.json.GitlabCiDsl.*

gitlabCi {
  if (Seq("refactor","master").contains(api.CI_COMMIT_BRANCH)) {
    default{
      tags("kubernetes")
    }
    import scala.concurrent.duration._
    val gradleVersion = readFile("gradle/wrapper/gradle-wrapper.properties").lines()
      .map(x => x.split("=")).filter(it => it.length == 2 && it(0) == "distributionUrl")
      .map(_(1)).map(x => x.split("-")(1)).findAny().orElse("7.4")
    job("build-with-gradle") {
      image(s"gradle:$gradleVersion-jdk17")
      script(
        "gradle clean bootJar -Phypers=true",
        "cp hyper-id-sync/build/libs/*.jar hyper-id-sync.jar",
        s"echo ${api.CI_SERVER_URL}/api/v4/projects/${api.CI_PROJECT_ID}/jobs/${api.CI_JOB_ID}/artifacts/hyper-id-sync.jar > hyper-id-sync.jar.url",
        "cp hyper-id-server/build/libs/*.jar hyper-id-server.jar",
        s"echo ${api.CI_SERVER_URL}/api/v4/projects/${api.CI_PROJECT_ID}/jobs/${api.CI_JOB_ID}/artifacts/hyper-id-server.jar > hyper-id-server.jar.url"
      )
      artifacts(
        paths = Seq("hyper-id-sync.jar","hyper-id-server.jar","hyper-id-sync.jar.url","hyper-id-server.jar.url"),
        expireIn = 20.seconds
      )
    }
    val imageName = s"${api.CI_REGISTRY_IMAGE}:${api.CI_PIPELINE_ID}"
    job("build-image-by-kaniko") {
      image("gcr.io/kaniko-project/executor:v1.9.0-debug")
      script(raw"""/kaniko/executor 
        |  --context "${api.CI_PROJECT_DIR}" 
        |  --dockerfile "${api.CI_PROJECT_DIR}/Dockerfile" 
        |  --destination $imageName
        |""".stripMargin
        )
    }
    
    job("push-to-k8s") {
      needs("build-with-gradle")
      image("alpine:latest")
      script(
        "sed -i 's/dl-cdn.alpinelinux.org/mirrors.ustc.edu.cn/g' /etc/apk/repositories",
        "apk add git gettext",
        "export HYPER_ID_SYNC_JAR_URL=$(cat hyper-id-sync.jar.url)",
        "export HYPER_ID_SERVER_JAR_URL=$(cat hyper-id-server.jar.url)",
        """find ./templates -type f -exec sh -c "cat {} | envsubst | sed -e 's/§/$/g' > tmp; mv tmp {}" \;""",
        "rm -rf ./kubernetes/latest",
        "cp -r ./templates ./kubernetes/revision/$CI_COMMIT_TIMESTAMP",
        "cp -r ./templates ./kubernetes/latest",
        "git add ./kubernetes",
        """git config user.name "bot"""",
        """git config user.email "bot@no-email.com"""",
        """git commit -m "${CI_COMMIT_AUTHOR}" -m "${CI_COMMIT_SHA}"""",
        """git push "https://${GITLAB_USER_NAME}:${GITLAB_TOKEN}@${CI_REPOSITORY_URL#*@}" "HEAD:${CI_COMMIT_BRANCH}"  -o ci.skip"""
      )
    }
  }
}
