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
                volumeCustom("www") {
                  fileImagePath("www","reg2.hypers.cc/has-frontend:latest","/usr/share/nginx/www")
                  fileLiteralText("nginx.conf","""
                    |server {
                    |  listen 80;
                    |  location / {
                    |    
                    |  }
                    |}
                    |""".stripMargin.stripLeading().stripTrailing())
                  fileLiteralText( "0.script.sh", "echo 'hello' > /hello.txt")
                }
                container("app", "nginx:alpine") {
                  volumeMounts("www" -> "/usr/share/nginx/www")
                  env("k0" -> "v0")
                }
                rabbitmqTopo("username","password","localhost",init = true) {
                  for (x <- Array("/has","/has-saas","/has-test")) vHost(x) {
                    exchange("aaa")
                    queue("queue0")
                    binding("aaa","direct","queue0")
                  }
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
