package com.github.yjgbg.json

object Sample {
  @main def main = {
    import KubernetesDsl.*
    namespace("default") {
      pod("") {
        spec {
          delegate("mysql",3306)
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
              volumeFromLiterial(name = "conf",files = Map(
                "nginx.conf" -> """
                |server {
                |  listen 80;
                |  location / {
                |    
                |  }
                |}
                """.stripMargin.stripLeading().stripTrailing()
              ))
              volumePVC("xxx")
              volumeFromImage("www","reg2.hypers.cc/has-frontend","/usr/share/nginx/www/")
              container("app","nginx:alpine") {
                volumeMounts("www" -> "/usr/share/nginx/www")
                volumeMounts("conf" -> "/etc/nginx/conf.d/")
                volumeMounts("xxx" -> "/tmp")
              }
            }
          }
        }
      }
      service("gateway") {
        spec {
          selector("app" -> "gateway")
          tcpPorts(80 -> 80)
        }
      }
    }
  }
}
