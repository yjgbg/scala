package com.github.yjgbg.json

object KubernetesStrictDsl extends 
  KubernetesStrictDsl,
  KubernetesStrictEnhenceDsl,
  KubernetesStrictApplyDsl
trait KubernetesStrictDsl extends JsonDsl:
  def commonLabels(using Interceptor)(values:(String,String)*)(closure:Interceptor ?=> Unit) = 
    interceptor {"metadata" ::= {"labels" ::= {values.foreach((k,v) => k := v)}}}(closure)
  def commonAnnotations(using Interceptor)(values:(String,String)*)(closure:Interceptor ?=> Unit) =
    interceptor {"metadata" ::= {"annotations" ::= {values.foreach((k,v) => k := v)}}}(closure)
  def namespace(using Interceptor,Prefix)(value:String)(closure:(Interceptor,Prefix) ?=> Unit) = 
    prefix(value+"-") {interceptor{"metadata" ::= {"namespace" := value}}(closure)}
  opaque type >>[A,B[_]] = B[A]
  opaque type DeploymentScope = Scope
  def deployment(using Interceptor,Prefix)(name:String)(closure: DeploymentScope ?=> Unit):Unit = 
    interceptor{"kind" := "Deployment";"apiVersion" := "v1";"metadata" ::= {"name" := name}}{
      writeYaml(s"deployment-$name.yaml")(closure)
    }
  opaque type ServiceScope = Scope
  def service(using Interceptor,Prefix)(name:String)(closure:ServiceScope ?=> Unit):Unit = 
    interceptor{"kind" := "Service";"apiVersion" := "v1";"metadata" ::= {"name" := name}}{
      writeYaml(s"service-$name.yaml")(closure)
    }
  opaque type PodScope = Scope
  def pod(using Interceptor,Prefix)(name:String)(closure:PodScope ?=> Unit):Unit = 
    interceptor{"kind" := "Pod";"apiVersion" := "v1";"metadata" ::= {"name" := name}}{
      writeYaml(s"pod-$name.yaml")(closure)
    }
  opaque type JobScope = Scope
  def job(using Interceptor,Prefix)(name:String)(closure:JobScope ?=> Unit):Unit = 
    interceptor{"kind" := "Job";"apiVersion" := "v1";"metadata" ::= {"name" := name}}{
      writeYaml(s"job-$name.yaml")(closure)
    }
  opaque type CronJobScope = Scope
  def cronJob(using Interceptor,Prefix)(name:String)(closure:CronJobScope ?=> Unit):Unit = 
    interceptor{"kind" := "CronJob";"apiVersion" := "v1";"metadata" ::= {"name" := name}}{
      writeYaml(s"cronjob-$name.yaml")(closure)
    }
  def schedule(using CronJobScope)(cron:String):Unit = "schedule" := cron
  opaque type ConfigMapScope = Scope
  def configMap(using Interceptor,Prefix)(name:String)(closure:ConfigMapScope ?=> Unit):Unit = 
    interceptor{"kind" := "ConfigMap";"apiVersion" := "batch/v1";"metadata" ::= {"name" := name}}{
      writeYaml(s"configmap-$name.yaml")(closure)
    }
  def data(using ConfigMapScope)(values: (String,String)*) : Unit = "data" ::= {
    values.foreach((k,v) => k := v)
  }
  def labels(using DeploymentScope|ServiceScope|PodScope|JobScope|CronJobScope|ConfigMapScope)
    (values:(String,String)*) = "metadata" ::= {"labels" ::= {values.foreach(_ := _)}}
  def annotations(using DeploymentScope|ServiceScope|PodScope|JobScope|CronJobScope|ConfigMapScope)
    (values:(String,String)*) = "metadata" ::= {"annotations" ::= {values.foreach(_ := _)}}
  opaque type SpecScope[A] = Scope
  def spec[A <: DeploymentScope|ServiceScope|PodScope|JobScope|CronJobScope|ConfigMapScope]
  (using A)(closure: A >> SpecScope ?=> Unit) = "spec" ::= closure
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
  def restartPolicy(using PodScope >> SpecScope)(policy:"Always"|"OnFailure"|"Never"):Unit = 
    "restartPolicy" := policy
  def volumeEmptyDir(using PodScope >> SpecScope)(name:String) :Unit = 
    "volumes" ++= {"name" := name;"emptyDir" ::= {}}
  def volumeConfigMap(using PodScope >> SpecScope)(name:String,configMap:String):Unit = 
    "volumes" ++= {"name" := name;"configMap" ::= {"name" := configMap}}
  def volumeHostPath(using PodScope >> SpecScope) (name:String,hostPath:String) =
    "volumes" ++= {"name" := name;"hostPath" ::= {"path" := hostPath}}
  type ContainerScope[A] = Scope
  def initContainer(using PodScope >> SpecScope)
  (name:String,image:String)(closure:PodScope >> SpecScope >> ContainerScope ?=> Unit):Unit =
    "initContainer" ++= {
      "name":= name
      "image":=image
      closure.apply
    }
  def container(using PodScope >> SpecScope)
  (name:String,image:String)(closure:PodScope >> SpecScope >> ContainerScope ?=> Unit):Unit =
    "container" ++= {
      "name":= name
      "image":=image
      closure.apply
    }
  def workDir(using PodScope >> SpecScope >> ContainerScope)(path:String):Unit = 
    "workDir" := path
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






