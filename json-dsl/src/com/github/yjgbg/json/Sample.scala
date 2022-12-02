package com.github.yjgbg.json

object Sample {
  @main def main = {
    import KubernetesDsl.*
    context("aaa") {
      namespace("default") {
        pod("xxx") {
          spec {
            proxy("mysql", 3306){}
          }
        }
        simplePVC("xxx")
        deployment("gateway") {
          labels("app" -> "gateway")
          spec {
            selectorMatchLabels("app" -> "gateway")
            template {
              labels("app" -> "gateway")
              spec {
                volumePVC("xxx")
                volumeImage("www", "reg2.hypers.cc/has-frontend:latest", "/usr/share/nginx/www/")
                volumeLiterialText("conf",
                "nginx.conf" -> """
                  |server {
                  |  listen 80;
                  |  location / {
                  |    
                  |  }
                  |}
                  |""".stripMargin.stripLeading().stripIndent())
                volumeLiterialText(
                  "scripts",
                  "0.script.sh" -> "echo 'hello' > /hello.txt"
                )
                container("app", "nginx:alpine") {
                  volumeMounts("www" -> "/usr/share/nginx/www")
                  env("k0" -> "v0")
                  volumeMounts("conf" -> "/etc/nginx/conf.d/")
                  volumeMounts("scripts" -> "/entrypoint.d/40.custom/")
                }
              }
            }
          }
        }
        tcpNodePort(8080,8080,"" -> "")
        service("gateway") {
          spec {
            selector("app" -> "gateway")
            tcpPorts(80 -> 80)
          }
        }
      }
    }
  }
}
