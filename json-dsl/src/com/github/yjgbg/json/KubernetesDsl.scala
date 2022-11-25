package com.github.yjgbg.json

import java.util.concurrent.atomic.AtomicReference

object KubernetesDsl extends 
  KubernetesDsl,
  KubernetesEnhenceDsl,
  KubernetesApplyDsl
trait KubernetesDsl extends JsonDsl:
  opaque type VerMan = AtomicReference[Map[String,String]]
  given VerMan = new AtomicReference(Map(
    "Deployment" -> "apps/v1",
    "Service" -> "v1",
    "Pod" -> "v1",
    "Job" -> "v1",
    "CronJob" -> "batch/v1",
    "PersistentVolumeClaim" -> "v1",
    "ConfigMap" -> "v1"
  ))
  protected def version(resourceName:String):String = summon[VerMan].get()(resourceName)
  def declareVersion(seq:(String,String)*) = summon[VerMan].getAndUpdate(_ ++ seq)
  def namespace(using Interceptor,Prefix)(value:String)(closure:(Interceptor,Prefix) ?=> Unit) = 
    prefix(value+"-") {withInterceptor{"metadata" ::= {"namespace" := value}}(closure)}
  opaque type >>[A,B[_]] = B[A]
  opaque type DeploymentScope = Scope
  def deployment(using Interceptor,Prefix)(name:String)(closure: DeploymentScope ?=> Unit):Unit = 
    withInterceptor{"kind" := "Deployment";"apiVersion" := version("Deployment");"metadata" ::= {"name" := name}}{
      writeYaml(s"$name-deployment.yaml")(closure)
    }
  opaque type ServiceScope = Scope
  def service(using Interceptor,Prefix)(name:String)(closure:ServiceScope ?=> Unit):Unit = 
    withInterceptor{"kind" := "Service";"apiVersion" := version("Service");"metadata" ::= {"name" := name}}{
      writeYaml(s"$name-service.yaml")(closure)
    }
  def tcpNodePort(using Interceptor,Prefix)(nodePort:Int|Null =null ,targetPort:Int,selector:(String,String)*) = 
    service("nodeport-"+selector.map((k,v) => s"$k-$v").mkString("--")) {
      spec {
        "type" := "NodePort"
        this.selector(selector:_*)
        "ports" ++= {
          "protocol" := "TCP"
          "targetPort" := targetPort.toLong
          "port" := targetPort.toLong
          if(nodePort!=null) "nodePort" := nodePort.nn.toLong
        }
      }
    }
  def udpNodePort(using Interceptor,Prefix)(nodePort:Int|Null = null,targetPort:Int,selector:(String,String)*) = 
    service("nodeport-"+selector.map((k,v) => s"$k-$v").mkString("--")) {
      spec {
        "type" := "NodePort"
        this.selector(selector:_*)
        "ports" ++= {
          "protocol" := "UDP"
          "targetPort" := targetPort.toLong
          "port" := targetPort.toLong
          if(nodePort!=null) "nodePort" := nodePort.nn.toLong
        }
      }
    }
  def sctpNodePort(using Interceptor,Prefix)(nodePort:Int|Null = null,targetPort:Int,selector:(String,String)*) = 
    service("nodeport-"+selector.map((k,v) => s"$k-$v").mkString("--")) {
      spec {
        "type" := "NodePort"
        this.selector(selector:_*)
        "ports" ++= {
          "protocol" := "SCTP"
          "targetPort" := targetPort.toLong
          "port" := targetPort.toLong
          if(nodePort!=null) "nodePort" := nodePort.nn.toLong
        }
      }
    }
  opaque type PodScope = Scope
  def pod(using Interceptor,Prefix)(name:String)(closure:PodScope ?=> Unit):Unit = 
    withInterceptor{"kind" := "Pod";"apiVersion" := version("Pod");"metadata" ::= {"name" := name}}{
      writeYaml(s"$name-pod.yaml")(closure)
    }
  opaque type JobScope = Scope
  def job(using Interceptor,Prefix)(name:String)(closure:JobScope ?=> Unit):Unit = 
    withInterceptor{"kind" := "Job";"apiVersion" := version("Job");"metadata" ::= {"name" := name}}{
      writeYaml(s"$name-job.yaml")(closure)
    }
  def backoffLimit(using JobScope >> SpecScope)(int:Int):Unit = {
    "backoffLimit" := int.toLong
  }
  opaque type CronJobScope = Scope
  def cronJob(using Interceptor,Prefix)(name:String)(closure:CronJobScope ?=> Unit):Unit = 
    withInterceptor{"kind" := "CronJob";"apiVersion" := version("CronJob");"metadata" ::= {"name" := name}}{
      writeYaml(s"$name-cronjob.yaml")(closure)
    }
  opaque type PersistentVolumeClaimScope = Scope
  def persistentVolumeClaim(using Interceptor,Prefix)(name:String)(closure:PersistentVolumeClaimScope ?=> Unit) =
    withInterceptor{
      "kind" := "PersistentVolumeClaim";
      "apiVersion" := version("PersistentVolumeClaim");
      "metadata" ::= {"name" := name}
    } {
      writeYaml(s"$name-persistent-volume-claim.yaml")(closure)
    }
  def schedule(using CronJobScope >> SpecScope)(cron:String):Unit = "schedule" := cron
  opaque type ConfigMapScope = Scope
  def configMap(using Interceptor,Prefix)(name:String)(closure:ConfigMapScope ?=> Unit):Unit = 
    withInterceptor{"kind" := "ConfigMap";"apiVersion" := version("ConfigMap");"metadata" ::= {"name" := name}}{
      writeYaml(s"$name-configmap.yaml")(closure)
    }
  def data(using ConfigMapScope)(values: (String,String)*) : Unit = "data" ::= {
    values.foreach((k,v) => k := v)
  }
  def labels(using DeploymentScope|ServiceScope|PodScope|JobScope|CronJobScope|ConfigMapScope|PersistentVolumeClaimScope)
    (values:(String,String)*) = "metadata" ::= {"labels" ::= {values.foreach(_ := _)}}
  def annotations(using DeploymentScope|ServiceScope|PodScope|JobScope|CronJobScope|ConfigMapScope|PersistentVolumeClaimScope)
    (values:(String,String)*) = "metadata" ::= {"annotations" ::= {values.foreach(_ := _)}}
  opaque type SpecScope[A] = Scope
  def spec[A <: DeploymentScope|ServiceScope|PodScope|JobScope|CronJobScope|ConfigMapScope|PersistentVolumeClaimScope]
  (using A)(closure: A >> SpecScope ?=> Unit) = "spec" ::= closure
  def storageClassName(using PersistentVolumeClaimScope >> SpecScope)(name:String) = 
    "storageClassName" := name
  def accessModes(using PersistentVolumeClaimScope >> SpecScope)
  (values:("ReadWriteOnce"|"ReadOnlyMany"|"ReadWriteMany"|"ReadWriteOncePod")*) = 
    values.foreach("accessModes" += _)
  def selector(using ServiceScope >> SpecScope)(labels:(String,String)*) = 
    if !labels.isEmpty then "selector" ::= {labels.foreach((k,v) => k := v)}
  def selectorMatchLabels[A <: DeploymentScope|JobScope](using A >> SpecScope)(labels:(String,String)*) = 
    if !labels.isEmpty then "selector" ::= {"matchLabels" ::= {labels.foreach((k,v) => k := v)}}
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

  def selectorMatchExpression[A <: DeploymentScope|ServiceScope|JobScope](using A >> SpecScope)(expresions:Expression*) = 
    if !expresions.isEmpty then "selector" ::= expresions.foreach(expression => {
      "matchExpressions" ::= {
        expression match
          case Expression.In(key, value) => "key" := key;"operator" := "In"; value.foreach(v => "values" += v)
          case Expression.NotIn(key, value) => "key" := key;"operator" := "NotIn"; value.foreach(v => "values" += v)
          case Expression.Exists(key) => "key" := key;"operator" := "Exists"
          case Expression.DoesNotExist(key) => "key" := key;"operator" := "DoesNotExist"
      }
    })
  def replicas(using DeploymentScope >> SpecScope)(int:Int) = "replicas" := int.toLong
  type TemplateScope[A] = A match 
    case JobScope >> SpecScope => PodScope
    case DeploymentScope >> SpecScope => PodScope
    case CronJobScope >> SpecScope => JobScope
  def template[A <: (DeploymentScope >> SpecScope) | (JobScope >> SpecScope)]
  (using A)(closure: TemplateScope[A] ?=> Unit):Unit =
    "template" ::= closure
  def suspend(using CronJobScope >> SpecScope)(boolean:Boolean = true) = "suspend" := boolean
  def jobTemplate(using CronJobScope >> SpecScope)(closure :JobScope ?=> Unit)= "jobTemplate" ::= closure
  def failedJobsHistoryLimit(using CronJobScope >> SpecScope)(int:Int) = "failedJobsHistoryLimit" := int.toLong
  def successfulJobsHistoryLimit(using CronJobScope >> SpecScope)(int:Int) = "successfulJobsHistoryLimit" := int.toLong
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
  /**
    * @param req2Limit 单位: 兆(MB)
    */
  def memory(using PodScope >> SpecScope >> ContainerScope >> ResourceScope)(req2Limit:(Long,Long)) = {
    "requests" ::= {"memory" := s"${req2Limit._1}M"}
    "limits" ::= {"memory" := s"${req2Limit._2}M"}
  }
    /**
    * @param req2Limit 单位: 吉(Gi)
    */
  def storage(using PersistentVolumeClaimScope >> SpecScope >> ResourceScope)(request:Long) = {
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
