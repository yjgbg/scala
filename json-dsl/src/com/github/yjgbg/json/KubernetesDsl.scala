package com.github.yjgbg.json

object KubernetesDsl extends 
  KubernetesDsl,
  KubernetesEnhenceDsl
trait KubernetesDsl extends JsonDsl:
  private var versions = Map[ContextScope,Map[String,String]]()
  private val defaultVersion:Map[String,String] = Map(
    "Deployment" -> "apps/v1",
    "Service" -> "v1",
    "Pod" -> "v1",
    "Job" -> "v1",
    "CronJob" -> "batch/v1",
    "PersistentVolumeClaim" -> "v1",
    "ConfigMap" -> "v1"
  )

  protected def version(using NamespaceScope)(resourceName:String):String = versions.getOrElse(summon[NamespaceScope].context,defaultVersion)(resourceName)
  def resourceVersion(using ContextScope)(resourceAndVersion:(String,String)) = {
    val verMan = versions.getOrElse(summon,defaultVersion)
    versions = versions +((summon,verMan + (resourceAndVersion))) 
  }

  class Resource(val name:String,val json:Scope ?=> Unit)
  class NamespaceScope(private[KubernetesDsl] val context:ContextScope,val name:String,var resourceSeq:Seq[Resource])
  class ContextScope(val name:String,var namespaces:Seq[NamespaceScope])
  def context(name: String, operation: "apply"|"create"|"delete"|"apply --server-side=true"|Null = null)(closure: ContextScope ?=> Unit) = {
    import sys.process._
    s"rm -rf target/$name/".! // 清理掉工作区
    val contextScope = ContextScope(name,Seq())
    closure(using contextScope) // 执行dsl
    for {
      ns <- contextScope.namespaces
      res <- ns.resourceSeq
    } writeYaml(s"target/${name}/${ns.name}-${res.name}.yaml")(res.json)
    if (operation!=null) {
      val currentContext = "kubectl config get-contexts".!!.lines()
        .filter(it => it.contains("*"))
        .findAny()
        .orElseThrow()
        .split(" ")
        .filter(!_.isBlank())(1) // 获取当前context
      if (currentContext != name) s"kubectl config use-context ${name}".! // 切换上下文
      s"kubectl ${operation} -f target/${name}".! // 应用yaml
      if (currentContext != name) s"kubectl config use-context ${currentContext}".! // 切换上下文
    }
  }
  def namespace(using ContextScope)(value:String)(closure: NamespaceScope ?=> Unit) = {
    val x = NamespaceScope(summon,value,Seq())
    closure.apply(using x)
    summon[ContextScope].namespaces = summon[ContextScope].namespaces :+ x
  }
  opaque type >>[A,B[_]] = B[A]
  opaque type DeploymentScope = Scope
  def deployment(using NamespaceScope)(name:String)(closure: DeploymentScope ?=> Unit):Unit = 
    summon[NamespaceScope].resourceSeq = summon[NamespaceScope].resourceSeq :+ Resource(s"$name-deployment",{
      "kind" := "Deployment"
      "apiVersion" := version("Deployment")
      "metadata" ::= {
        "namespace" := summon[NamespaceScope].name
        "name" := name
      }
      closure.apply
    })
  opaque type ServiceScope = Scope
  def service(using NamespaceScope)(name:String)(closure:ServiceScope ?=> Unit):Unit = 
    summon[NamespaceScope].resourceSeq = summon[NamespaceScope].resourceSeq :+ Resource(s"$name-service",{
      "kind" := "Service"
      "apiVersion" := version("Service")
      "metadata" ::= {
        "namespace" := summon[NamespaceScope].name
        "name" := name
      }
      closure.apply
    })
  import scala.compiletime.ops.int.>=
  def tcpNodePort(using NamespaceScope)(nodePort:Int,targetPort:Int,selector:(String,String)*)(using nodePort.type >= 0 =:= true,targetPort.type >= 0 =:= true) = 
    service("nodeport-"+nodePort) {
      spec {
        "type" := "NodePort"
        this.selector(selector:_*)
        "ports" ++= {
          "protocol" := "TCP"
          "targetPort" := targetPort.toLong
          "port" := targetPort.toLong
          "nodePort" := nodePort.toLong
        }
      }
    }
  def udpNodePort(using NamespaceScope)(nodePort:Int,targetPort:Int,selector:(String,String)*)(using nodePort.type >= 0 =:= true,targetPort.type >= 0 =:= true) = 
    service("nodeport-"+nodePort) {
      spec {
        "type" := "NodePort"
        this.selector(selector:_*)
        "ports" ++= {
          "protocol" := "UDP"
          "targetPort" := targetPort.toLong
          "port" := targetPort.toLong
          "nodePort" := nodePort.toLong
        }
      }
    }
  def sctpNodePort(using NamespaceScope)(nodePort:Int,targetPort:Int,selector:(String,String)*)(using nodePort.type >= 0 =:= true,targetPort.type >= 0 =:= true) = 
    service("nodeport-"+nodePort) {
      spec {
        "type" := "NodePort"
        this.selector(selector:_*)
        "ports" ++= {
          "protocol" := "SCTP"
          "targetPort" := targetPort.toLong
          "port" := targetPort.toLong
          "nodePort" := nodePort.toLong
        }
      }
    }
  opaque type PodScope = Scope
  def pod(using NamespaceScope)(name:String)(closure:PodScope ?=> Unit):Unit = 
    summon[NamespaceScope].resourceSeq = summon[NamespaceScope].resourceSeq :+ Resource(s"$name-pod",{
      "kind" := "Pod"
      "apiVersion" := version("Pod")
      "metadata" ::= {
        "namespace" := summon[NamespaceScope].name
        "name" := name
      }
      closure.apply
    })
  opaque type JobScope = Scope
  def job(using NamespaceScope)(name:String)(closure:JobScope ?=> Unit):Unit = 
    summon[NamespaceScope].resourceSeq = summon[NamespaceScope].resourceSeq :+ Resource(s"$name-job",{
      "kind" := "Job"
      "apiVersion" := version("Job")
      "metadata" ::= {
        "namespace" := summon[NamespaceScope].name
        "name" := name
      }
      closure.apply
    })
  def backoffLimit(using JobScope >> SpecScope)(int:Int)(using int.type >= 0 =:= true):Unit = {
    "backoffLimit" := int.toLong
  }
  opaque type CronJobScope = Scope
  def cronJob(using NamespaceScope)(name:String)(closure:CronJobScope ?=> Unit):Unit = 
    summon[NamespaceScope].resourceSeq = summon[NamespaceScope].resourceSeq :+ Resource(s"$name-cron-job",{
      "kind" := "CronJob"
      "apiVersion" := version("CronJob")
      "metadata" ::= {
        "namespace" := summon[NamespaceScope].name
        "name" := name
      }
      closure.apply
    })
  opaque type PersistentVolumeClaimScope = Scope
  def persistentVolumeClaim(using NamespaceScope)(name:String)(closure:PersistentVolumeClaimScope ?=> Unit) =
    summon[NamespaceScope].resourceSeq = summon[NamespaceScope].resourceSeq :+ Resource(s"$name-persistent-volume-claim",{
      "kind" := "PersistentVolumeClaim"
      "apiVersion" := version("PersistentVolumeClaim")
      "metadata" ::= {
        "namespace" := summon[NamespaceScope].name
        "name" := name
      }
      closure.apply
    })
  def schedule(using CronJobScope >> SpecScope)(cron:String):Unit = "schedule" := cron
  opaque type ConfigMapScope = Scope
  def configMap(using NamespaceScope)(name:String)(closure:ConfigMapScope ?=> Unit):Unit = 
    summon[NamespaceScope].resourceSeq = summon[NamespaceScope].resourceSeq :+ Resource(s"$name-config-map",{
      "kind" := "ConfigMap"
      "apiVersion" := version("ConfigMap")
      "metadata" ::= {
        "namespace" := summon[NamespaceScope].name
        "name" := name
      }
      closure.apply
    })
  def data(using ConfigMapScope)(values: (String,String)*) : Unit = "data" ::= {
    values.foreach((k,v) => k := v)
  }
  def labels(using DeploymentScope|ServiceScope|PodScope|JobScope|CronJobScope|ConfigMapScope|PersistentVolumeClaimScope)
    (values:(String,String)*) = values.foreach{(k,v) => "metadata" ::= {"labels" ::= {k := v}}}
  def annotations(using DeploymentScope|ServiceScope|PodScope|JobScope|CronJobScope|ConfigMapScope|PersistentVolumeClaimScope)
    (values:(String,String)*) = values.foreach{(k,v) => "metadata" ::= {"annotations" ::= {k := v}}}
  opaque type SpecScope[A] = Scope
  def spec[A <: DeploymentScope|ServiceScope|PodScope|JobScope|CronJobScope|ConfigMapScope|PersistentVolumeClaimScope]
  (using A)(closure: A >> SpecScope ?=> Unit) = "spec" ::= closure
  def storageClassName(using PersistentVolumeClaimScope >> SpecScope)(name:String) = 
    "storageClassName" := name
  def accessModes(using PersistentVolumeClaimScope >> SpecScope)
  (values:("ReadWriteOnce"|"ReadOnlyMany"|"ReadWriteMany"|"ReadWriteOncePod")*) = 
    values.foreach("accessModes" += _)
  def selector(using ServiceScope >> SpecScope)(labels:(String,String)*) = 
    labels.foreach{(k,v) => "selector" ::= { k := v}}
  def selectorMatchLabels[A <: DeploymentScope|JobScope](using A >> SpecScope)(labels:(String,String)*) = 
    labels.foreach{(k,v) => "selector" ::= {"matchLabels" ::=  {k := v}}}
  enum Expression:
    case In(key:String,value:Seq[String]) extends Expression
    case NotIn(key:String,value:Seq[String]) extends Expression
    case Exists(key:String) extends Expression
    case DoesNotExist(key:String) extends Expression
  extension (key:String)
    infix def in(value:Seq[String]):Expression.In = Expression.In(key,value)
    infix def notIn(value:Seq[String]):Expression.NotIn = Expression.NotIn(key,value)
    infix def exists = Expression.Exists(key)
    infix def doesNotExist = Expression.DoesNotExist(key)

  def selectorMatchExpression[A <: DeploymentScope|JobScope](using A >> SpecScope)(expresions:Expression*) = 
    if !expresions.isEmpty then "selector" ::= expresions.foreach(expression => {
      "matchExpressions" ::= {
        expression match
          case Expression.In(key, value) => "key" := key;"operator" := "In"; value.foreach(v => "values" += v)
          case Expression.NotIn(key, value) => "key" := key;"operator" := "NotIn"; value.foreach(v => "values" += v)
          case Expression.Exists(key) => "key" := key;"operator" := "Exists"
          case Expression.DoesNotExist(key) => "key" := key;"operator" := "DoesNotExist"
      }
    })
  def replicas(using DeploymentScope >> SpecScope)(int:Int)(using int.type >= 0 =:= true) = "replicas" := int.toLong
  type TemplateScope[A] = A match 
    case JobScope >> SpecScope => PodScope
    case DeploymentScope >> SpecScope => PodScope
    case CronJobScope >> SpecScope => JobScope
  def template[A <: (DeploymentScope >> SpecScope) | (JobScope >> SpecScope)]
  (using A)(closure: TemplateScope[A] ?=> Unit):Unit =
    "template" ::= closure
  def suspend(using CronJobScope >> SpecScope)(boolean:Boolean = true) = "suspend" := boolean
  def jobTemplate(using CronJobScope >> SpecScope)(closure :JobScope ?=> Unit)= "jobTemplate" ::= closure
  def failedJobsHistoryLimit(using CronJobScope >> SpecScope)(int:Int)(using int.type >= 0 =:= true) = "failedJobsHistoryLimit" := int.toLong
  def successfulJobsHistoryLimit(using CronJobScope >> SpecScope)(int:Int)(using int.type >= 0 =:= true) = "successfulJobsHistoryLimit" := int.toLong
  def nodeSelector(using PodScope >> SpecScope)(labels:(String,String)*) =
    labels.toMap.foreach{ (k,v) =>"nodeSelector" ::= { k := v}}
  def restartPolicy(using PodScope >> SpecScope)(policy:"Always"|"OnFailure"|"Never"):Unit = 
    "restartPolicy" := policy
  def volumeEmptyDir(using PodScope >> SpecScope)(name:String) :Unit = 
    "volumes" ++= {"name" := name;"emptyDir" ::= {}}
  // items 是一个key to path 的元组
  def volumeConfigMap(using PodScope >> SpecScope)(name:String,configMap:String = null,items:(String,String)*):Unit = 
    "volumes" ++= {
      "name" := name
      "configMap" ::= {
        "name" := (if configMap == null then name else configMap)
        items.foreach((key,path) => "items" ++= {
          "key" := key
          "path" := path
        })
      }
    }

  def volumePVC(using PodScope >> SpecScope)(name:String,pvcName:String = null)= 
    "volumes" ++= {
      "name" := name
      "persistentVolumeClaim" ::= {
        "claimName" := (if pvcName !=null then pvcName else name)
      }
    }

  type ContainerScope[A] = Scope
  def initContainer(using PodScope >> SpecScope)
  (name:String,image:String)(closure:PodScope >> SpecScope >> ContainerScope ?=> Unit):Unit =
    "initContainers" ++= {
      "name":= name
      "image":=image
      closure.apply
    }
  def container(using PodScope >> SpecScope)
  (name:String,image:String)(closure:PodScope >> SpecScope >> ContainerScope ?=> Unit):Unit =
    "containers" ++= {
      "name":= name
      "image":=image
      closure.apply
    }
  opaque type ProbeScope[A] = Scope
  /**
    * 存活探针
    *
    * @param initialDelaySeconds 容器调度多久后开始探测？
    * @param periodSeconds 多久探测一次？
    * @param timeoutSeconds 超时时间
    * @param successThreshold 连续成功几次才算成功？
    * @param failureThreshold 连续失败几次才算失败？
    * @param closure 闭包
    */
  def livenessProbe(using PodScope >> SpecScope >> ContainerScope)
  (initialDelaySeconds:Int = 5,periodSeconds:Int = 5,timeoutSeconds:Int = 1,successThreshold:Int = 1,failureThreshold:Int = 1)
  (closure:PodScope >> SpecScope >> ContainerScope >> ProbeScope ?=> Unit):Unit = 
    "livenessProbe" ::= {
      "initialDelaySeconds" := initialDelaySeconds.toLong
      "periodSeconds" := periodSeconds.toLong
      "timeoutSeconds" := timeoutSeconds.toLong
      "successThreshold" := successThreshold.toLong
      "failureThreshold" := failureThreshold.toLong
      closure.apply
    }
  /**
    * 就绪探针
    *
    * @param initialDelaySeconds 容器调度多久后开始探测？
    * @param periodSeconds 多久探测一次？
    * @param timeoutSeconds 超时时间
    * @param successThreshold 连续成功几次才算成功？
    * @param failureThreshold 连续失败几次才算失败？
    * @param closure 闭包
    */
  def readinessProbe(using PodScope >> SpecScope >> ContainerScope)
  (initialDelaySeconds:Int = 5,periodSeconds:Int = 5,timeoutSeconds:Int = 1,successThreshold:Int = 1,failureThreshold:Int = 1)
  (closure:PodScope >> SpecScope >> ContainerScope >> ProbeScope ?=> Unit):Unit = 
    "readinessProbe" ::= {
      "initialDelaySeconds" := initialDelaySeconds.toLong
      "periodSeconds" := periodSeconds.toLong
      "timeoutSeconds" := timeoutSeconds.toLong
      "successThreshold" := successThreshold.toLong
      "failureThreshold" := failureThreshold.toLong
      closure.apply
    }
  /**
    * 启动探针
    *
    * @param initialDelaySeconds 容器调度多久后开始探测？
    * @param periodSeconds 多久探测一次？
    * @param timeoutSeconds 超时时间
    * @param successThreshold 连续成功几次才算成功？
    * @param failureThreshold 连续失败几次才算失败？
    * @param closure 闭包
    */
  def startupProbe(using PodScope >> SpecScope >> ContainerScope)
  (initialDelaySeconds:Int = 5,periodSeconds:Int = 5,timeoutSeconds:Int = 1,successThreshold:Int = 1,failureThreshold:Int = 1)
  (closure:PodScope >> SpecScope >> ContainerScope >> ProbeScope ?=> Unit):Unit = 
    "startupProbe" ::= {
      "initialDelaySeconds" := initialDelaySeconds.toLong
      "periodSeconds" := periodSeconds.toLong
      "timeoutSeconds" := timeoutSeconds.toLong
      "successThreshold" := successThreshold.toLong
      "failureThreshold" := failureThreshold.toLong
      closure.apply
    }
  def exec(using PodScope >> SpecScope >> ContainerScope >> ProbeScope)(cmd:String*) = 
    "exec" ::= {cmd.foreach("command" += _)}
  def httpGet(using PodScope >> SpecScope >> ContainerScope >> ProbeScope)
  (path:String,port:Int,host:String|Null = null,scheme:"HTTP"|"HTTPS" = "HTTP",headers:Map[String,String] = Map()) = "httpGet" ::= {
    "path" := path
    if (host!=null) "host" := host.nn
    "scheme" := scheme
    "port" := port.toLong
    headers.foreach((k,v) => {
      "httpHeaders" ++= {
        "name" := k
        "value" := v
      }
    })
  }

  def tcpSocket(using PodScope >> SpecScope >> ContainerScope >> ProbeScope)
  (port:Int):Unit = "tcpSocket" ::= {"port" := port.toLong}

  def workingDir(using PodScope >> SpecScope >> ContainerScope)(path:String):Unit = 
    "workingDir" := path
  def imagePullPolicy(using PodScope >> SpecScope >> ContainerScope)
  (value:"Always"|"IfNotPresent"|"Never") :Unit = 
    "imagePullPolicy" := value
  def command(using PodScope >> SpecScope >> ContainerScope)(values:String*):Unit = 
    values.foreach("command" += _)
  def args(using PodScope >> SpecScope >> ContainerScope)(values:String*):Unit = 
    values.foreach("args" += _)
  def env(using PodScope >> SpecScope >> ContainerScope)(values:(String,String)*):Unit = 
    values.foreach{(k,v) => "env" ++= {"name" := k;"value" := v}}
  def envFromConfigMapKey(using PodScope >> SpecScope >> ContainerScope)(kv:(String,(String,String))):Unit = 
    "env" ++= {"name" := kv._1;"valueFrom" ::= { "configMapKeyRef" ::={
      "name" := kv._2._1
      "key" := kv._2._1
    }}}
  def envFromSecretKey(using PodScope >> SpecScope >> ContainerScope)(kv:(String,(String,String))):Unit = 
    "env" ++= {"name" := kv._1;"valueFrom" ::= { "secretKeyRef" ::={
      "name" := kv._2._1
      "key" := kv._2._1
    }}}
  def volumeMounts(using PodScope >> SpecScope >> ContainerScope)
  (nameAndPath:(String,String)*):Unit = nameAndPath.foreach{(name,mountPath) => 
    "volumeMounts" ++= {"name" := name;"mountPath" := mountPath}
  }
  opaque type ResourceScope[_] = Scope
  def resources[A <: (PodScope >> SpecScope >> ContainerScope)|(PersistentVolumeClaimScope >> SpecScope)]
  (using A)(closure: A >> ResourceScope ?=> Unit) = 
    "resources" ::= closure
  def cpu(using PodScope >> SpecScope >> ContainerScope >> ResourceScope)(req2Limit:(Double,Double)) = {
    "requests" ::= {"cpu" := req2Limit._1.toString()}
    "limits" ::= {"cpu" := req2Limit._2.toString()}
  }
  def hostAlias(using PodScope >> SpecScope)(ip:String,hostnames:String*) = {
    "hostAliases" ::= {
      "ip" := ip
      hostnames.foreach{"hostnames" += _}
    }
  }
  /**
    * @param req2Limit 单位: 兆(MB)
    */
  def memory(using PodScope >> SpecScope >> ContainerScope >> ResourceScope)(req2Limit:(Int,Int)) = {
    "requests" ::= {"memory" := s"${req2Limit._1}M"}
    "limits" ::= {"memory" := s"${req2Limit._2}M"}
  }
    /**
    * @param request 单位: 吉(Gi)
    */
  def storage(using PersistentVolumeClaimScope >> SpecScope >> ResourceScope)(request:Int) = {
    "requests" ::= {"storage" := s"${request}Gi"}
  }
  def tcpPorts(using ServiceScope >> SpecScope)
  (values:(Int,Int)*) = values.foreach((targetPort,port) => "ports" ++= {
    "port" := port.toLong
    "targetPort" := targetPort.toLong
    "protocol" := "TCP"
  })
  def udpPorts(using ServiceScope >> SpecScope)
  (values:(Int,Int)*) = values.foreach((targetPort,port) => "ports" ++= {
    "port" := port.toLong
    "targetPort" := targetPort.toLong
    "protocol" := "UDP"
  })
  def sctpPorts(using ServiceScope >> SpecScope)
  (values:(Int,Int)*) = values.foreach((targetPort,port) => "ports" ++= {
    "port" := port.toLong
    "targetPort" := targetPort.toLong
    "protocol" := "SCTP"
  })
  def ports(using PodScope >> SpecScope >> ContainerScope)(ports:Int*):Unit = ports.foreach(it => "ports" ++= { "containerPort" := it.toLong})
