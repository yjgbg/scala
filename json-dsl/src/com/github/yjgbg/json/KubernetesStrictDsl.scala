package com.github.yjgbg.json

object KubernetesStrictDsl extends KubernetesStrictDsl
trait KubernetesStrictDsl extends JsonDsl:
  def namespace(using Interceptor,Prefix)(value:String)(closure:(Interceptor,Prefix) ?=> Unit) = 
    prefix(value+"-") {interceptor{"metadata" ::= {"namespace" := value}}(closure)}
    
  import scala.annotation.implicitNotFound
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
  opaque type ConfigMapScope = Scope
  def configMap(using Interceptor,Prefix)(name:String)(closure:ConfigMapScope ?=> Unit):Unit = 
    interceptor{"kind" := "ConfigMap";"apiVersion" := "batch/v1";"metadata" ::= {"name" := name}}{
      writeYaml(s"configmap-$name.yaml")(closure)
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
  def template[A <: (DeploymentScope >> SpecScope)|(CronJobScope >> SpecScope) | (JobScope >> SpecScope)]
  (using A)(closure: TemplateScope[A] ?=> Unit):Unit =
    "template" ::= closure
  type ContainerScope[A] = Scope
  def container(using PodScope >> SpecScope)(closure:PodScope >> SpecScope >> ContainerScope ?=> Unit):Unit =
    "containers" ++= closure






