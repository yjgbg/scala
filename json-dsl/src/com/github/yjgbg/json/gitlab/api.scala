package com.github.yjgbg.json.gitlab

object api:
  /**
    * The Source chat channel that triggered the ChatOps command.
    *
    * gitlab version: 10.6
    * gitlab runner version: all
    */
  lazy val CHAT_CHANNEL:String = sys.env.getOrElse("CHAT_CHANNEL","${CHAT_CHANNEL}")


  /**
    * The additional arguments passed with the ChatOps command.
    *
    * gitlab version: 10.6
    * gitlab runner version: all
    */
  lazy val CHAT_INPUT:String = sys.env.getOrElse("CHAT_INPUT","${CHAT_INPUT}")


  /**
    * The chat service’s user ID of the user who triggered the ChatOps command.
    *
    * gitlab version: 14.4
    * gitlab runner version: all
    */
  lazy val CHAT_USER_ID:String = sys.env.getOrElse("CHAT_USER_ID","${CHAT_USER_ID}")


  /**
    * Available for all jobs executed in CI/CD. true when available.
    *
    * gitlab version: all
    * gitlab runner version: 0.4
    */
  lazy val CI:String = sys.env.getOrElse("CI","${CI}")


  /**
    * The GitLab API v4 root URL.
    *
    * gitlab version: 11.7
    * gitlab runner version: all
    */
  lazy val CI_API_V4_URL:String = sys.env.getOrElse("CI_API_V4_URL","${CI_API_V4_URL}")


  /**
    * The top-level directory where builds are executed.
    *
    * gitlab version: all
    * gitlab runner version: 11.10
    */
  lazy val CI_BUILDS_DIR:String = sys.env.getOrElse("CI_BUILDS_DIR","${CI_BUILDS_DIR}")


  /**
    * The author of the commit in Name <email> format.
    *
    * gitlab version: 13.11
    * gitlab runner version: all
    */
  lazy val CI_COMMIT_AUTHOR:String = sys.env.getOrElse("CI_COMMIT_AUTHOR","${CI_COMMIT_AUTHOR}")


  /**
    * The previous latest commit present on a branch or tag. Is always 0000000000000000000000000000000000000000 in merge request pipelines and for the first commit in pipelines for branches or tags.
    *
    * gitlab version: 11.2
    * gitlab runner version: all
    */
  lazy val CI_COMMIT_BEFORE_SHA:String = sys.env.getOrElse("CI_COMMIT_BEFORE_SHA","${CI_COMMIT_BEFORE_SHA}")


  /**
    * The commit branch name. Available in branch pipelines, including pipelines for the default branch. Not available in merge request pipelines or tag pipelines.
    *
    * gitlab version: 12.6
    * gitlab runner version: 0.5
    */
  lazy val CI_COMMIT_BRANCH:String = sys.env.getOrElse("CI_COMMIT_BRANCH","${CI_COMMIT_BRANCH}")


  /**
    * The description of the commit. If the title is shorter than 100 characters, the message without the first line.
    *
    * gitlab version: 10.8
    * gitlab runner version: all
    */
  lazy val CI_COMMIT_DESCRIPTION:String = sys.env.getOrElse("CI_COMMIT_DESCRIPTION","${CI_COMMIT_DESCRIPTION}")


  /**
    * The full commit message.
    *
    * gitlab version: 10.8
    * gitlab runner version: all
    */
  lazy val CI_COMMIT_MESSAGE:String = sys.env.getOrElse("CI_COMMIT_MESSAGE","${CI_COMMIT_MESSAGE}")


  /**
    * The branch or tag name for which project is built.
    *
    * gitlab version: 9.0
    * gitlab runner version: all
    */
  lazy val CI_COMMIT_REF_NAME:String = sys.env.getOrElse("CI_COMMIT_REF_NAME","${CI_COMMIT_REF_NAME}")


  /**
    * true if the job is running for a protected reference.
    *
    * gitlab version: 11.11
    * gitlab runner version: all
    */
  lazy val CI_COMMIT_REF_PROTECTED:String = sys.env.getOrElse("CI_COMMIT_REF_PROTECTED","${CI_COMMIT_REF_PROTECTED}")


  /**
    * CI_COMMIT_REF_NAME in lowercase, shortened to 63 bytes, and with everything except 0-9 and a-z replaced with -. No leading / trailing -. Use in URLs, host names and domain names.
    *
    * gitlab version: 9.0
    * gitlab runner version: all
    */
  lazy val CI_COMMIT_REF_SLUG:String = sys.env.getOrElse("CI_COMMIT_REF_SLUG","${CI_COMMIT_REF_SLUG}")


  /**
    * The commit revision the project is built for.
    *
    * gitlab version: 9.0
    * gitlab runner version: all
    */
  lazy val CI_COMMIT_SHA:String = sys.env.getOrElse("CI_COMMIT_SHA","${CI_COMMIT_SHA}")


  /**
    * The first eight characters of CI_COMMIT_SHA.
    *
    * gitlab version: 11.7
    * gitlab runner version: all
    */
  lazy val CI_COMMIT_SHORT_SHA:String = sys.env.getOrElse("CI_COMMIT_SHORT_SHA","${CI_COMMIT_SHORT_SHA}")


  /**
    * The commit tag name. Available only in pipelines for tags.
    *
    * gitlab version: 9.0
    * gitlab runner version: 0.5
    */
  lazy val CI_COMMIT_TAG:String = sys.env.getOrElse("CI_COMMIT_TAG","${CI_COMMIT_TAG}")


  /**
    * The timestamp of the commit in the ISO 8601 format.
    *
    * gitlab version: 13.4
    * gitlab runner version: all
    */
  lazy val CI_COMMIT_TIMESTAMP:String = sys.env.getOrElse("CI_COMMIT_TIMESTAMP","${CI_COMMIT_TIMESTAMP}")


  /**
    * The title of the commit. The full first line of the message.
    *
    * gitlab version: 10.8
    * gitlab runner version: all
    */
  lazy val CI_COMMIT_TITLE:String = sys.env.getOrElse("CI_COMMIT_TITLE","${CI_COMMIT_TITLE}")


  /**
    * The unique ID of build execution in a single executor.
    *
    * gitlab version: all
    * gitlab runner version: 11.10
    */
  lazy val CI_CONCURRENT_ID:String = sys.env.getOrElse("CI_CONCURRENT_ID","${CI_CONCURRENT_ID}")


  /**
    * The unique ID of build execution in a single executor and project.
    *
    * gitlab version: all
    * gitlab runner version: 11.10
    */
  lazy val CI_CONCURRENT_PROJECT_ID:String = sys.env.getOrElse("CI_CONCURRENT_PROJECT_ID","${CI_CONCURRENT_PROJECT_ID}")


  /**
    * The path to the CI/CD configuration file. Defaults to .gitlab-ci.yml. Read-only inside a running pipeline.
    *
    * gitlab version: 9.4
    * gitlab runner version: 0.5
    */
  lazy val CI_CONFIG_PATH:String = sys.env.getOrElse("CI_CONFIG_PATH","${CI_CONFIG_PATH}")


  /**
    * true if debug logging (tracing) is enabled.
    *
    * gitlab version: all
    * gitlab runner version: 1.7
    */
  lazy val CI_DEBUG_TRACE:String = sys.env.getOrElse("CI_DEBUG_TRACE","${CI_DEBUG_TRACE}")


  /**
    * The name of the project’s default branch.
    *
    * gitlab version: 12.4
    * gitlab runner version: all
    */
  lazy val CI_DEFAULT_BRANCH:String = sys.env.getOrElse("CI_DEFAULT_BRANCH","${CI_DEFAULT_BRANCH}")


  /**
    * The top-level group image prefix for pulling images through the Dependency Proxy.
    *
    * gitlab version: 13.7
    * gitlab runner version: all
    */
  lazy val CI_DEPENDENCY_PROXY_GROUP_IMAGE_PREFIX:String = sys.env.getOrElse("CI_DEPENDENCY_PROXY_GROUP_IMAGE_PREFIX","${CI_DEPENDENCY_PROXY_GROUP_IMAGE_PREFIX}")


  /**
    * The direct group image prefix for pulling images through the Dependency Proxy.
    *
    * gitlab version: 14.3
    * gitlab runner version: all
    */
  lazy val CI_DEPENDENCY_PROXY_DIRECT_GROUP_IMAGE_PREFIX:String = sys.env.getOrElse("CI_DEPENDENCY_PROXY_DIRECT_GROUP_IMAGE_PREFIX","${CI_DEPENDENCY_PROXY_DIRECT_GROUP_IMAGE_PREFIX}")


  /**
    * The password to pull images through the Dependency Proxy.
    *
    * gitlab version: 13.7
    * gitlab runner version: all
    */
  lazy val CI_DEPENDENCY_PROXY_PASSWORD:String = sys.env.getOrElse("CI_DEPENDENCY_PROXY_PASSWORD","${CI_DEPENDENCY_PROXY_PASSWORD}")


  /**
    * The server for logging in to the Dependency Proxy. This is equivalent to $CI_SERVER_HOST:$CI_SERVER_PORT.
    *
    * gitlab version: 13.7
    * gitlab runner version: all
    */
  lazy val CI_DEPENDENCY_PROXY_SERVER:String = sys.env.getOrElse("CI_DEPENDENCY_PROXY_SERVER","${CI_DEPENDENCY_PROXY_SERVER}")


  /**
    * The username to pull images through the Dependency Proxy.
    *
    * gitlab version: 13.7
    * gitlab runner version: all
    */
  lazy val CI_DEPENDENCY_PROXY_USER:String = sys.env.getOrElse("CI_DEPENDENCY_PROXY_USER","${CI_DEPENDENCY_PROXY_USER}")


  /**
    * Only available if the pipeline runs during a deploy freeze window. true when available.
    *
    * gitlab version: 13.2
    * gitlab runner version: all
    */
  lazy val CI_DEPLOY_FREEZE:String = sys.env.getOrElse("CI_DEPLOY_FREEZE","${CI_DEPLOY_FREEZE}")


  /**
    * The authentication password of the GitLab Deploy Token, if the project has one.
    *
    * gitlab version: 10.8
    * gitlab runner version: all
    */
  lazy val CI_DEPLOY_PASSWORD:String = sys.env.getOrElse("CI_DEPLOY_PASSWORD","${CI_DEPLOY_PASSWORD}")


  /**
    * The authentication username of the GitLab Deploy Token, if the project has one.
    *
    * gitlab version: 10.8
    * gitlab runner version: all
    */
  lazy val CI_DEPLOY_USER:String = sys.env.getOrElse("CI_DEPLOY_USER","${CI_DEPLOY_USER}")


  /**
    * Only available if the job is executed in a disposable environment (something that is created only for this job and disposed of/destroyed after the execution - all executors except shell and ssh). true when available.
    *
    * gitlab version: all
    * gitlab runner version: 10.1
    */
  lazy val CI_DISPOSABLE_ENVIRONMENT:String = sys.env.getOrElse("CI_DISPOSABLE_ENVIRONMENT","${CI_DISPOSABLE_ENVIRONMENT}")


  /**
    * The name of the environment for this job. Available if environment:name is set.
    *
    * gitlab version: 8.15
    * gitlab runner version: all
    */
  lazy val CI_ENVIRONMENT_NAME:String = sys.env.getOrElse("CI_ENVIRONMENT_NAME","${CI_ENVIRONMENT_NAME}")


  /**
    * The simplified version of the environment name, suitable for inclusion in DNS, URLs, Kubernetes labels, and so on. Available if environment:name is set. The slug is truncated to 24 characters.
    *
    * gitlab version: 8.15
    * gitlab runner version: all
    */
  lazy val CI_ENVIRONMENT_SLUG:String = sys.env.getOrElse("CI_ENVIRONMENT_SLUG","${CI_ENVIRONMENT_SLUG}")


  /**
    * The URL of the environment for this job. Available if environment:url is set.
    *
    * gitlab version: 9.3
    * gitlab runner version: all
    */
  lazy val CI_ENVIRONMENT_URL:String = sys.env.getOrElse("CI_ENVIRONMENT_URL","${CI_ENVIRONMENT_URL}")


  /**
    * The action annotation specified for this job’s environment. Available if environment:action is set. Can be start, prepare, or stop.
    *
    * gitlab version: 13.11
    * gitlab runner version: all
    */
  lazy val CI_ENVIRONMENT_ACTION:String = sys.env.getOrElse("CI_ENVIRONMENT_ACTION","${CI_ENVIRONMENT_ACTION}")


  /**
    * The deployment tier of the environment for this job.
    *
    * gitlab version: 14.0
    * gitlab runner version: all
    */
  lazy val CI_ENVIRONMENT_TIER:String = sys.env.getOrElse("CI_ENVIRONMENT_TIER","${CI_ENVIRONMENT_TIER}")


  /**
    * The configuration setting for whether FIPS mode is enabled in the GitLab instance.
    *
    * gitlab version: 14.10
    * gitlab runner version: all
    */
  lazy val CI_GITLAB_FIPS_MODE:String = sys.env.getOrElse("CI_GITLAB_FIPS_MODE","${CI_GITLAB_FIPS_MODE}")


  /**
    * Only available if the pipeline’s project has an open requirement. true when available.
    *
    * gitlab version: 13.1
    * gitlab runner version: all
    */
  lazy val CI_HAS_OPEN_REQUIREMENTS:String = sys.env.getOrElse("CI_HAS_OPEN_REQUIREMENTS","${CI_HAS_OPEN_REQUIREMENTS}")


  /**
    * The internal ID of the job, unique across all jobs in the GitLab instance.
    *
    * gitlab version: 9.0
    * gitlab runner version: all
    */
  lazy val CI_JOB_ID:String = sys.env.getOrElse("CI_JOB_ID","${CI_JOB_ID}")


  /**
    * The name of the Docker image running the job.
    *
    * gitlab version: 12.9
    * gitlab runner version: 12.9
    */
  lazy val CI_JOB_IMAGE:String = sys.env.getOrElse("CI_JOB_IMAGE","${CI_JOB_IMAGE}")


  /**
    * A RS256 JSON web token to authenticate with third party systems that support JWT authentication, for example HashiCorp’s Vault.
    *
    * gitlab version: 12.10
    * gitlab runner version: all
    */
  lazy val CI_JOB_JWT:String = sys.env.getOrElse("CI_JOB_JWT","${CI_JOB_JWT}")


  /**
    * The same value as CI_JOB_JWT.
    *
    * gitlab version: 14.6
    * gitlab runner version: all
    */
  lazy val CI_JOB_JWT_V1:String = sys.env.getOrElse("CI_JOB_JWT_V1","${CI_JOB_JWT_V1}")


  /**
    * A newly formatted RS256 JSON web token to increase compatibility. Similar to CI_JOB_JWT, except the issuer (iss) claim is changed from gitlab.com to https://gitlab.com, sub has changed from job_id to a string that contains the project path, and an aud claim is added. Format is subject to change. Be aware, the aud field is a constant value. Trusting JWTs in multiple relying parties can lead to one RP sending a JWT to another one and acting maliciously as a job. Note: The CI_JOB_JWT_V2 variable is available for testing, but the full feature is planned to be generally available when issue 360657 is complete.
    *
    * gitlab version: 14.6
    * gitlab runner version: all
    */
  lazy val CI_JOB_JWT_V2:String = sys.env.getOrElse("CI_JOB_JWT_V2","${CI_JOB_JWT_V2}")


  /**
    * Only available if the job was started manually. true when available.
    *
    * gitlab version: 8.12
    * gitlab runner version: all
    */
  lazy val CI_JOB_MANUAL:String = sys.env.getOrElse("CI_JOB_MANUAL","${CI_JOB_MANUAL}")


  /**
    * The name of the job.
    *
    * gitlab version: 9.0
    * gitlab runner version: 0.5
    */
  lazy val CI_JOB_NAME:String = sys.env.getOrElse("CI_JOB_NAME","${CI_JOB_NAME}")


  /**
    * The name of the job’s stage.
    *
    * gitlab version: 9.0
    * gitlab runner version: 0.5
    */
  lazy val CI_JOB_STAGE:String = sys.env.getOrElse("CI_JOB_STAGE","${CI_JOB_STAGE}")


  /**
    * The status of the job as each runner stage is executed. Use with after_script. Can be success, failed, or canceled.
    *
    * gitlab version: all
    * gitlab runner version: 13.5
    */
  lazy val CI_JOB_STATUS:String = sys.env.getOrElse("CI_JOB_STATUS","${CI_JOB_STATUS}")


  /**
    * A token to authenticate with certain API endpoints. The token is valid as long as the job is running.
    *
    * gitlab version: 9.0
    * gitlab runner version: 1.2
    */
  lazy val CI_JOB_TOKEN:String = sys.env.getOrElse("CI_JOB_TOKEN","${CI_JOB_TOKEN}")


  /**
    * The job details URL.
    *
    * gitlab version: 11.1
    * gitlab runner version: 0.5
    */
  lazy val CI_JOB_URL:String = sys.env.getOrElse("CI_JOB_URL","${CI_JOB_URL}")


  /**
    * The UTC datetime when a job started, in ISO 8601 format.
    *
    * gitlab version: 13.10
    * gitlab runner version: all
    */
  lazy val CI_JOB_STARTED_AT:String = sys.env.getOrElse("CI_JOB_STARTED_AT","${CI_JOB_STARTED_AT}")


  /**
    * Only available if the pipeline has a Kubernetes cluster available for deployments. true when available.
    *
    * gitlab version: 13.0
    * gitlab runner version: all
    */
  lazy val CI_KUBERNETES_ACTIVE:String = sys.env.getOrElse("CI_KUBERNETES_ACTIVE","${CI_KUBERNETES_ACTIVE}")


  /**
    * The index of the job in the job set. Only available if the job uses parallel.
    *
    * gitlab version: 11.5
    * gitlab runner version: all
    */
  lazy val CI_NODE_INDEX:String = sys.env.getOrElse("CI_NODE_INDEX","${CI_NODE_INDEX}")


  /**
    * The total number of instances of this job running in parallel. Set to 1 if the job does not use parallel.
    *
    * gitlab version: 11.5
    * gitlab runner version: all
    */
  lazy val CI_NODE_TOTAL:String = sys.env.getOrElse("CI_NODE_TOTAL","${CI_NODE_TOTAL}")


  /**
    * A comma-separated list of up to four merge requests that use the current branch and project as the merge request source. Only available in branch and merge request pipelines if the branch has an associated merge request. For example, gitlab-org/gitlab!333,gitlab-org/gitlab-foss!11.
    *
    * gitlab version: 13.8
    * gitlab runner version: all
    */
  lazy val CI_OPEN_MERGE_REQUESTS:String = sys.env.getOrElse("CI_OPEN_MERGE_REQUESTS","${CI_OPEN_MERGE_REQUESTS}")


  /**
    * The configured domain that hosts GitLab Pages.
    *
    * gitlab version: 11.8
    * gitlab runner version: all
    */
  lazy val CI_PAGES_DOMAIN:String = sys.env.getOrElse("CI_PAGES_DOMAIN","${CI_PAGES_DOMAIN}")


  /**
    * The URL for a GitLab Pages site. Always a subdomain of CI_PAGES_DOMAIN.
    *
    * gitlab version: 11.8
    * gitlab runner version: all
    */
  lazy val CI_PAGES_URL:String = sys.env.getOrElse("CI_PAGES_URL","${CI_PAGES_URL}")


  /**
    * The instance-level ID of the current pipeline. This ID is unique across all projects on the GitLab instance.
    *
    * gitlab version: 8.10
    * gitlab runner version: all
    */
  lazy val CI_PIPELINE_ID:String = sys.env.getOrElse("CI_PIPELINE_ID","${CI_PIPELINE_ID}")


  /**
    * The project-level IID (internal ID) of the current pipeline. This ID is unique only within the current project.
    *
    * gitlab version: 11.0
    * gitlab runner version: all
    */
  lazy val CI_PIPELINE_IID:String = sys.env.getOrElse("CI_PIPELINE_IID","${CI_PIPELINE_IID}")


  /**
    * How the pipeline was triggered. Can be push, web, schedule, api, external, chat, webide, merge_request_event, external_pull_request_event, parent_pipeline, trigger, or pipeline. For a description of each value, see Common if clauses for rules, which uses this variable to control when jobs run.
    *
    * gitlab version: 10.0
    * gitlab runner version: all
    */
  lazy val CI_PIPELINE_SOURCE:String = sys.env.getOrElse("CI_PIPELINE_SOURCE","${CI_PIPELINE_SOURCE}")


  /**
    * true if the job was triggered.
    *
    * gitlab version: all
    * gitlab runner version: all
    */
  lazy val CI_PIPELINE_TRIGGERED:String = sys.env.getOrElse("CI_PIPELINE_TRIGGERED","${CI_PIPELINE_TRIGGERED}")


  /**
    * The URL for the pipeline details.
    *
    * gitlab version: 11.1
    * gitlab runner version: 0.5
    */
  lazy val CI_PIPELINE_URL:String = sys.env.getOrElse("CI_PIPELINE_URL","${CI_PIPELINE_URL}")


  /**
    * The UTC datetime when the pipeline was created, in ISO 8601 format.
    *
    * gitlab version: 13.10
    * gitlab runner version: all
    */
  lazy val CI_PIPELINE_CREATED_AT:String = sys.env.getOrElse("CI_PIPELINE_CREATED_AT","${CI_PIPELINE_CREATED_AT}")


  /**
    * Removed in GitLab 14.0. Use CI_CONFIG_PATH.
    *
    * gitlab version: 13.8 to 13.12
    * gitlab runner version: all
    */
  lazy val CI_PROJECT_CONFIG_PATH:String = sys.env.getOrElse("CI_PROJECT_CONFIG_PATH","${CI_PROJECT_CONFIG_PATH}")


  /**
    * The full path the repository is cloned to, and where the job runs from. If the GitLab Runner builds_dir parameter is set, this variable is set relative to the value of builds_dir. For more information, see the Advanced GitLab Runner configuration.
    *
    * gitlab version: all
    * gitlab runner version: all
    */
  lazy val CI_PROJECT_DIR:String = sys.env.getOrElse("CI_PROJECT_DIR","${CI_PROJECT_DIR}")


  /**
    * The ID of the current project. This ID is unique across all projects on the GitLab instance.
    *
    * gitlab version: all
    * gitlab runner version: all
    */
  lazy val CI_PROJECT_ID:String = sys.env.getOrElse("CI_PROJECT_ID","${CI_PROJECT_ID}")


  /**
    * The name of the directory for the project. For example if the project URL is gitlab.example.com/group-name/project-1, CI_PROJECT_NAME is project-1.
    *
    * gitlab version: 8.10
    * gitlab runner version: 0.5
    */
  lazy val CI_PROJECT_NAME:String = sys.env.getOrElse("CI_PROJECT_NAME","${CI_PROJECT_NAME}")


  /**
    * The project namespace (username or group name) of the job.
    *
    * gitlab version: 8.10
    * gitlab runner version: 0.5
    */
  lazy val CI_PROJECT_NAMESPACE:String = sys.env.getOrElse("CI_PROJECT_NAMESPACE","${CI_PROJECT_NAMESPACE}")


  /**
    * $CI_PROJECT_PATH in lowercase with characters that are not a-z or 0-9 replaced with - and shortened to 63 bytes. Use in URLs and domain names.
    *
    * gitlab version: 9.3
    * gitlab runner version: all
    */
  lazy val CI_PROJECT_PATH_SLUG:String = sys.env.getOrElse("CI_PROJECT_PATH_SLUG","${CI_PROJECT_PATH_SLUG}")


  /**
    * The project namespace with the project name included.
    *
    * gitlab version: 8.10
    * gitlab runner version: 0.5
    */
  lazy val CI_PROJECT_PATH:String = sys.env.getOrElse("CI_PROJECT_PATH","${CI_PROJECT_PATH}")


  /**
    * A comma-separated, lowercase list of the languages used in the repository. For example ruby,javascript,html,css. The maximum number of languages is limited to 5. An issue proposes to increase the limit.
    *
    * gitlab version: 12.3
    * gitlab runner version: all
    */
  lazy val CI_PROJECT_REPOSITORY_LANGUAGES:String = sys.env.getOrElse("CI_PROJECT_REPOSITORY_LANGUAGES","${CI_PROJECT_REPOSITORY_LANGUAGES}")


  /**
    * The root project namespace (username or group name) of the job. For example, if CI_PROJECT_NAMESPACE is root-group/child-group/grandchild-group, CI_PROJECT_ROOT_NAMESPACE is root-group.
    *
    * gitlab version: 13.2
    * gitlab runner version: 0.5
    */
  lazy val CI_PROJECT_ROOT_NAMESPACE:String = sys.env.getOrElse("CI_PROJECT_ROOT_NAMESPACE","${CI_PROJECT_ROOT_NAMESPACE}")


  /**
    * The human-readable project name as displayed in the GitLab web interface.
    *
    * gitlab version: 12.4
    * gitlab runner version: all
    */
  lazy val CI_PROJECT_TITLE:String = sys.env.getOrElse("CI_PROJECT_TITLE","${CI_PROJECT_TITLE}")


  /**
    * The project description as displayed in the GitLab web interface.
    *
    * gitlab version: 15.1
    * gitlab runner version: all
    */
  lazy val CI_PROJECT_DESCRIPTION:String = sys.env.getOrElse("CI_PROJECT_DESCRIPTION","${CI_PROJECT_DESCRIPTION}")


  /**
    * The HTTP(S) address of the project.
    *
    * gitlab version: 8.10
    * gitlab runner version: 0.5
    */
  lazy val CI_PROJECT_URL:String = sys.env.getOrElse("CI_PROJECT_URL","${CI_PROJECT_URL}")


  /**
    * The project visibility. Can be internal, private, or public.
    *
    * gitlab version: 10.3
    * gitlab runner version: all
    */
  lazy val CI_PROJECT_VISIBILITY:String = sys.env.getOrElse("CI_PROJECT_VISIBILITY","${CI_PROJECT_VISIBILITY}")


  /**
    * The project external authorization classification label.
    *
    * gitlab version: 14.2
    * gitlab runner version: all
    */
  lazy val CI_PROJECT_CLASSIFICATION_LABEL:String = sys.env.getOrElse("CI_PROJECT_CLASSIFICATION_LABEL","${CI_PROJECT_CLASSIFICATION_LABEL}")


  /**
    * The address of the project’s Container Registry. Only available if the Container Registry is enabled for the project.
    *
    * gitlab version: 8.10
    * gitlab runner version: 0.5
    */
  lazy val CI_REGISTRY_IMAGE:String = sys.env.getOrElse("CI_REGISTRY_IMAGE","${CI_REGISTRY_IMAGE}")


  /**
    * The password to push containers to the project’s GitLab Container Registry. Only available if the Container Registry is enabled for the project. This password value is the same as the CI_JOB_TOKEN and is valid only as long as the job is running. Use the CI_DEPLOY_PASSWORD for long-lived access to the registry
    *
    * gitlab version: 9.0
    * gitlab runner version: all
    */
  lazy val CI_REGISTRY_PASSWORD:String = sys.env.getOrElse("CI_REGISTRY_PASSWORD","${CI_REGISTRY_PASSWORD}")


  /**
    * The username to push containers to the project’s GitLab Container Registry. Only available if the Container Registry is enabled for the project.
    *
    * gitlab version: 9.0
    * gitlab runner version: all
    */
  lazy val CI_REGISTRY_USER:String = sys.env.getOrElse("CI_REGISTRY_USER","${CI_REGISTRY_USER}")


  /**
    * The address of the GitLab Container Registry. Only available if the Container Registry is enabled for the project. This variable includes a :port value if one is specified in the registry configuration.
    *
    * gitlab version: 8.10
    * gitlab runner version: 0.5
    */
  lazy val CI_REGISTRY:String = sys.env.getOrElse("CI_REGISTRY","${CI_REGISTRY}")


  /**
    * The URL to clone the Git repository.
    *
    * gitlab version: 9.0
    * gitlab runner version: all
    */
  lazy val CI_REPOSITORY_URL:String = sys.env.getOrElse("CI_REPOSITORY_URL","${CI_REPOSITORY_URL}")


  /**
    * The description of the runner.
    *
    * gitlab version: 8.10
    * gitlab runner version: 0.5
    */
  lazy val CI_RUNNER_DESCRIPTION:String = sys.env.getOrElse("CI_RUNNER_DESCRIPTION","${CI_RUNNER_DESCRIPTION}")


  /**
    * The OS/architecture of the GitLab Runner executable. Might not be the same as the environment of the executor.
    *
    * gitlab version: all
    * gitlab runner version: 10.6
    */
  lazy val CI_RUNNER_EXECUTABLE_ARCH:String = sys.env.getOrElse("CI_RUNNER_EXECUTABLE_ARCH","${CI_RUNNER_EXECUTABLE_ARCH}")


  /**
    * The unique ID of the runner being used.
    *
    * gitlab version: 8.10
    * gitlab runner version: 0.5
    */
  lazy val CI_RUNNER_ID:String = sys.env.getOrElse("CI_RUNNER_ID","${CI_RUNNER_ID}")


  /**
    * The revision of the runner running the job.
    *
    * gitlab version: all
    * gitlab runner version: 10.6
    */
  lazy val CI_RUNNER_REVISION:String = sys.env.getOrElse("CI_RUNNER_REVISION","${CI_RUNNER_REVISION}")


  /**
    * The runner’s unique ID, used to authenticate new job requests. In GitLab 14.9 and later, the token contains a prefix, and the first 17 characters are used. Prior to 14.9, the first eight characters are used.
    *
    * gitlab version: all
    * gitlab runner version: 12.3
    */
  lazy val CI_RUNNER_SHORT_TOKEN:String = sys.env.getOrElse("CI_RUNNER_SHORT_TOKEN","${CI_RUNNER_SHORT_TOKEN}")


  /**
    * A comma-separated list of the runner tags.
    *
    * gitlab version: 8.10
    * gitlab runner version: 0.5
    */
  lazy val CI_RUNNER_TAGS:String = sys.env.getOrElse("CI_RUNNER_TAGS","${CI_RUNNER_TAGS}")


  /**
    * The version of the GitLab Runner running the job.
    *
    * gitlab version: all
    * gitlab runner version: 10.6
    */
  lazy val CI_RUNNER_VERSION:String = sys.env.getOrElse("CI_RUNNER_VERSION","${CI_RUNNER_VERSION}")


  /**
    * The host of the GitLab instance URL, without protocol or port. For example gitlab.example.com.
    *
    * gitlab version: 12.1
    * gitlab runner version: all
    */
  lazy val CI_SERVER_HOST:String = sys.env.getOrElse("CI_SERVER_HOST","${CI_SERVER_HOST}")


  /**
    * The name of CI/CD server that coordinates jobs.
    *
    * gitlab version: all
    * gitlab runner version: all
    */
  lazy val CI_SERVER_NAME:String = sys.env.getOrElse("CI_SERVER_NAME","${CI_SERVER_NAME}")


  /**
    * The port of the GitLab instance URL, without host or protocol. For example 8080.
    *
    * gitlab version: 12.8
    * gitlab runner version: all
    */
  lazy val CI_SERVER_PORT:String = sys.env.getOrElse("CI_SERVER_PORT","${CI_SERVER_PORT}")


  /**
    * The protocol of the GitLab instance URL, without host or port. For example https.
    *
    * gitlab version: 12.8
    * gitlab runner version: all
    */
  lazy val CI_SERVER_PROTOCOL:String = sys.env.getOrElse("CI_SERVER_PROTOCOL","${CI_SERVER_PROTOCOL}")


  /**
    * GitLab revision that schedules jobs.
    *
    * gitlab version: all
    * gitlab runner version: all
    */
  lazy val CI_SERVER_REVISION:String = sys.env.getOrElse("CI_SERVER_REVISION","${CI_SERVER_REVISION}")


  /**
    * File containing the TLS CA certificate to verify the GitLab server when tls-ca-file set in runner settings.
    *
    * gitlab version: all
    * gitlab runner version: all
    */
  lazy val CI_SERVER_TLS_CA_FILE:String = sys.env.getOrElse("CI_SERVER_TLS_CA_FILE","${CI_SERVER_TLS_CA_FILE}")


  /**
    * File containing the TLS certificate to verify the GitLab server when tls-cert-file set in runner settings.
    *
    * gitlab version: all
    * gitlab runner version: all
    */
  lazy val CI_SERVER_TLS_CERT_FILE:String = sys.env.getOrElse("CI_SERVER_TLS_CERT_FILE","${CI_SERVER_TLS_CERT_FILE}")


  /**
    * File containing the TLS key to verify the GitLab server when tls-key-file set in runner settings.
    *
    * gitlab version: all
    * gitlab runner version: all
    */
  lazy val CI_SERVER_TLS_KEY_FILE:String = sys.env.getOrElse("CI_SERVER_TLS_KEY_FILE","${CI_SERVER_TLS_KEY_FILE}")


  /**
    * The base URL of the GitLab instance, including protocol and port. For example https://gitlab.example.com:8080.
    *
    * gitlab version: 12.7
    * gitlab runner version: all
    */
  lazy val CI_SERVER_URL:String = sys.env.getOrElse("CI_SERVER_URL","${CI_SERVER_URL}")


  /**
    * The major version of the GitLab instance. For example, if the GitLab version is 13.6.1, the CI_SERVER_VERSION_MAJOR is 13.
    *
    * gitlab version: 11.4
    * gitlab runner version: all
    */
  lazy val CI_SERVER_VERSION_MAJOR:String = sys.env.getOrElse("CI_SERVER_VERSION_MAJOR","${CI_SERVER_VERSION_MAJOR}")


  /**
    * The minor version of the GitLab instance. For example, if the GitLab version is 13.6.1, the CI_SERVER_VERSION_MINOR is 6.
    *
    * gitlab version: 11.4
    * gitlab runner version: all
    */
  lazy val CI_SERVER_VERSION_MINOR:String = sys.env.getOrElse("CI_SERVER_VERSION_MINOR","${CI_SERVER_VERSION_MINOR}")


  /**
    * The patch version of the GitLab instance. For example, if the GitLab version is 13.6.1, the CI_SERVER_VERSION_PATCH is 1.
    *
    * gitlab version: 11.4
    * gitlab runner version: all
    */
  lazy val CI_SERVER_VERSION_PATCH:String = sys.env.getOrElse("CI_SERVER_VERSION_PATCH","${CI_SERVER_VERSION_PATCH}")


  /**
    * The full version of the GitLab instance.
    *
    * gitlab version: all
    * gitlab runner version: all
    */
  lazy val CI_SERVER_VERSION:String = sys.env.getOrElse("CI_SERVER_VERSION","${CI_SERVER_VERSION}")


  /**
    * Available for all jobs executed in CI/CD. yes when available.
    *
    * gitlab version: all
    * gitlab runner version: all
    */
  lazy val CI_SERVER:String = sys.env.getOrElse("CI_SERVER","${CI_SERVER}")


  /**
    * Only available if the job is executed in a shared environment (something that is persisted across CI/CD invocations, like the shell or ssh executor). true when available.
    *
    * gitlab version: all
    * gitlab runner version: 10.1
    */
  lazy val CI_SHARED_ENVIRONMENT:String = sys.env.getOrElse("CI_SHARED_ENVIRONMENT","${CI_SHARED_ENVIRONMENT}")


  /**
    * The host of the registry used by CI/CD templates. Defaults to registry.gitlab.com.
    *
    * gitlab version: 15.3
    * gitlab runner version: all
    */
  lazy val CI_TEMPLATE_REGISTRY_HOST:String = sys.env.getOrElse("CI_TEMPLATE_REGISTRY_HOST","${CI_TEMPLATE_REGISTRY_HOST}")


  /**
    * Available for all jobs executed in CI/CD. true when available.
    *
    * gitlab version: all
    * gitlab runner version: all
    */
  lazy val GITLAB_CI:String = sys.env.getOrElse("GITLAB_CI","${GITLAB_CI}")


  /**
    * The comma-separated list of licensed features available for the GitLab instance and license.
    *
    * gitlab version: 10.6
    * gitlab runner version: all
    */
  lazy val GITLAB_FEATURES:String = sys.env.getOrElse("GITLAB_FEATURES","${GITLAB_FEATURES}")


  /**
    * The email of the user who started the job.
    *
    * gitlab version: 8.12
    * gitlab runner version: all
    */
  lazy val GITLAB_USER_EMAIL:String = sys.env.getOrElse("GITLAB_USER_EMAIL","${GITLAB_USER_EMAIL}")


  /**
    * The ID of the user who started the job.
    *
    * gitlab version: 8.12
    * gitlab runner version: all
    */
  lazy val GITLAB_USER_ID:String = sys.env.getOrElse("GITLAB_USER_ID","${GITLAB_USER_ID}")


  /**
    * The username of the user who started the job.
    *
    * gitlab version: 10.0
    * gitlab runner version: all
    */
  lazy val GITLAB_USER_LOGIN:String = sys.env.getOrElse("GITLAB_USER_LOGIN","${GITLAB_USER_LOGIN}")


  /**
    * The name of the user who started the job.
    *
    * gitlab version: 10.0
    * gitlab runner version: all
    */
  lazy val GITLAB_USER_NAME:String = sys.env.getOrElse("GITLAB_USER_NAME","${GITLAB_USER_NAME}")


  /**
    * The webhook payload. Only available when a pipeline is triggered with a webhook.
    *
    * gitlab version: 13.9
    * gitlab runner version: all
    */
  lazy val TRIGGER_PAYLOAD:String = sys.env.getOrElse("TRIGGER_PAYLOAD","${TRIGGER_PAYLOAD}")


  /**
    * Approval status of the merge request. true when merge request approvals is available and the merge request has been approved.
    *
    * gitlab version: 14.1
    * gitlab runner version: all
    */
  lazy val CI_MERGE_REQUEST_APPROVED:String = sys.env.getOrElse("CI_MERGE_REQUEST_APPROVED","${CI_MERGE_REQUEST_APPROVED}")


  /**
    * Comma-separated list of usernames of assignees for the merge request.
    *
    * gitlab version: 11.9
    * gitlab runner version: all
    */
  lazy val CI_MERGE_REQUEST_ASSIGNEES:String = sys.env.getOrElse("CI_MERGE_REQUEST_ASSIGNEES","${CI_MERGE_REQUEST_ASSIGNEES}")


  /**
    * The instance-level ID of the merge request. This is a unique ID across all projects on GitLab.
    *
    * gitlab version: 11.6
    * gitlab runner version: all
    */
  lazy val CI_MERGE_REQUEST_ID:String = sys.env.getOrElse("CI_MERGE_REQUEST_ID","${CI_MERGE_REQUEST_ID}")


  /**
    * The project-level IID (internal ID) of the merge request. This ID is unique for the current project.
    *
    * gitlab version: 11.6
    * gitlab runner version: all
    */
  lazy val CI_MERGE_REQUEST_IID:String = sys.env.getOrElse("CI_MERGE_REQUEST_IID","${CI_MERGE_REQUEST_IID}")


  /**
    * Comma-separated label names of the merge request.
    *
    * gitlab version: 11.9
    * gitlab runner version: all
    */
  lazy val CI_MERGE_REQUEST_LABELS:String = sys.env.getOrElse("CI_MERGE_REQUEST_LABELS","${CI_MERGE_REQUEST_LABELS}")


  /**
    * The milestone title of the merge request.
    *
    * gitlab version: 11.9
    * gitlab runner version: all
    */
  lazy val CI_MERGE_REQUEST_MILESTONE:String = sys.env.getOrElse("CI_MERGE_REQUEST_MILESTONE","${CI_MERGE_REQUEST_MILESTONE}")


  /**
    * The ID of the project of the merge request.
    *
    * gitlab version: 11.6
    * gitlab runner version: all
    */
  lazy val CI_MERGE_REQUEST_PROJECT_ID:String = sys.env.getOrElse("CI_MERGE_REQUEST_PROJECT_ID","${CI_MERGE_REQUEST_PROJECT_ID}")


  /**
    * The path of the project of the merge request. For example namespace/awesome-project.
    *
    * gitlab version: 11.6
    * gitlab runner version: all
    */
  lazy val CI_MERGE_REQUEST_PROJECT_PATH:String = sys.env.getOrElse("CI_MERGE_REQUEST_PROJECT_PATH","${CI_MERGE_REQUEST_PROJECT_PATH}")


  /**
    * The URL of the project of the merge request. For example, http://192.168.10.15:3000/namespace/awesome-project.
    *
    * gitlab version: 11.6
    * gitlab runner version: all
    */
  lazy val CI_MERGE_REQUEST_PROJECT_URL:String = sys.env.getOrElse("CI_MERGE_REQUEST_PROJECT_URL","${CI_MERGE_REQUEST_PROJECT_URL}")


  /**
    * The ref path of the merge request. For example, refs/merge-requests/1/head.
    *
    * gitlab version: 11.6
    * gitlab runner version: all
    */
  lazy val CI_MERGE_REQUEST_REF_PATH:String = sys.env.getOrElse("CI_MERGE_REQUEST_REF_PATH","${CI_MERGE_REQUEST_REF_PATH}")


  /**
    * The source branch name of the merge request.
    *
    * gitlab version: 11.6
    * gitlab runner version: all
    */
  lazy val CI_MERGE_REQUEST_SOURCE_BRANCH_NAME:String = sys.env.getOrElse("CI_MERGE_REQUEST_SOURCE_BRANCH_NAME","${CI_MERGE_REQUEST_SOURCE_BRANCH_NAME}")


  /**
    * The HEAD SHA of the source branch of the merge request. The variable is empty in merge request pipelines. The SHA is present only in merged results pipelines.
    *
    * gitlab version: 11.9
    * gitlab runner version: all
    */
  lazy val CI_MERGE_REQUEST_SOURCE_BRANCH_SHA:String = sys.env.getOrElse("CI_MERGE_REQUEST_SOURCE_BRANCH_SHA","${CI_MERGE_REQUEST_SOURCE_BRANCH_SHA}")


  /**
    * The ID of the source project of the merge request.
    *
    * gitlab version: 11.6
    * gitlab runner version: all
    */
  lazy val CI_MERGE_REQUEST_SOURCE_PROJECT_ID:String = sys.env.getOrElse("CI_MERGE_REQUEST_SOURCE_PROJECT_ID","${CI_MERGE_REQUEST_SOURCE_PROJECT_ID}")


  /**
    * The path of the source project of the merge request.
    *
    * gitlab version: 11.6
    * gitlab runner version: all
    */
  lazy val CI_MERGE_REQUEST_SOURCE_PROJECT_PATH:String = sys.env.getOrElse("CI_MERGE_REQUEST_SOURCE_PROJECT_PATH","${CI_MERGE_REQUEST_SOURCE_PROJECT_PATH}")


  /**
    * The URL of the source project of the merge request.
    *
    * gitlab version: 11.6
    * gitlab runner version: all
    */
  lazy val CI_MERGE_REQUEST_SOURCE_PROJECT_URL:String = sys.env.getOrElse("CI_MERGE_REQUEST_SOURCE_PROJECT_URL","${CI_MERGE_REQUEST_SOURCE_PROJECT_URL}")


  /**
    * The target branch name of the merge request.
    *
    * gitlab version: 11.6
    * gitlab runner version: all
    */
  lazy val CI_MERGE_REQUEST_TARGET_BRANCH_NAME:String = sys.env.getOrElse("CI_MERGE_REQUEST_TARGET_BRANCH_NAME","${CI_MERGE_REQUEST_TARGET_BRANCH_NAME}")


  /**
    * The protection status for the target branch of the merge request.
    *
    * gitlab version: 15.2
    * gitlab runner version: all
    */
  lazy val CI_MERGE_REQUEST_TARGET_BRANCH_PROTECTED:String = sys.env.getOrElse("CI_MERGE_REQUEST_TARGET_BRANCH_PROTECTED","${CI_MERGE_REQUEST_TARGET_BRANCH_PROTECTED}")


  /**
    * The HEAD SHA of the target branch of the merge request. The variable is empty in merge request pipelines. The SHA is present only in merged results pipelines.
    *
    * gitlab version: 11.9
    * gitlab runner version: all
    */
  lazy val CI_MERGE_REQUEST_TARGET_BRANCH_SHA:String = sys.env.getOrElse("CI_MERGE_REQUEST_TARGET_BRANCH_SHA","${CI_MERGE_REQUEST_TARGET_BRANCH_SHA}")


  /**
    * The title of the merge request.
    *
    * gitlab version: 11.9
    * gitlab runner version: all
    */
  lazy val CI_MERGE_REQUEST_TITLE:String = sys.env.getOrElse("CI_MERGE_REQUEST_TITLE","${CI_MERGE_REQUEST_TITLE}")


  /**
    * The event type of the merge request. Can be detached, merged_result or merge_train.
    *
    * gitlab version: 12.3
    * gitlab runner version: all
    */
  lazy val CI_MERGE_REQUEST_EVENT_TYPE:String = sys.env.getOrElse("CI_MERGE_REQUEST_EVENT_TYPE","${CI_MERGE_REQUEST_EVENT_TYPE}")


  /**
    * The version of the merge request diff.
    *
    * gitlab version: 13.7
    * gitlab runner version: all
    */
  lazy val CI_MERGE_REQUEST_DIFF_ID:String = sys.env.getOrElse("CI_MERGE_REQUEST_DIFF_ID","${CI_MERGE_REQUEST_DIFF_ID}")


  /**
    * The base SHA of the merge request diff.
    *
    * gitlab version: 13.7
    * gitlab runner version: all
    */
  lazy val CI_MERGE_REQUEST_DIFF_BASE_SHA:String = sys.env.getOrElse("CI_MERGE_REQUEST_DIFF_BASE_SHA","${CI_MERGE_REQUEST_DIFF_BASE_SHA}")


  /**
    * Pull request ID from GitHub.
    *
    * gitlab version: 12.3
    * gitlab runner version: all
    */
  lazy val CI_EXTERNAL_PULL_REQUEST_IID:String = sys.env.getOrElse("CI_EXTERNAL_PULL_REQUEST_IID","${CI_EXTERNAL_PULL_REQUEST_IID}")


  /**
    * The source repository name of the pull request.
    *
    * gitlab version: 13.3
    * gitlab runner version: all
    */
  lazy val CI_EXTERNAL_PULL_REQUEST_SOURCE_REPOSITORY:String = sys.env.getOrElse("CI_EXTERNAL_PULL_REQUEST_SOURCE_REPOSITORY","${CI_EXTERNAL_PULL_REQUEST_SOURCE_REPOSITORY}")


  /**
    * The target repository name of the pull request.
    *
    * gitlab version: 13.3
    * gitlab runner version: all
    */
  lazy val CI_EXTERNAL_PULL_REQUEST_TARGET_REPOSITORY:String = sys.env.getOrElse("CI_EXTERNAL_PULL_REQUEST_TARGET_REPOSITORY","${CI_EXTERNAL_PULL_REQUEST_TARGET_REPOSITORY}")


  /**
    * The source branch name of the pull request.
    *
    * gitlab version: 12.3
    * gitlab runner version: all
    */
  lazy val CI_EXTERNAL_PULL_REQUEST_SOURCE_BRANCH_NAME:String = sys.env.getOrElse("CI_EXTERNAL_PULL_REQUEST_SOURCE_BRANCH_NAME","${CI_EXTERNAL_PULL_REQUEST_SOURCE_BRANCH_NAME}")


  /**
    * The HEAD SHA of the source branch of the pull request.
    *
    * gitlab version: 12.3
    * gitlab runner version: all
    */
  lazy val CI_EXTERNAL_PULL_REQUEST_SOURCE_BRANCH_SHA:String = sys.env.getOrElse("CI_EXTERNAL_PULL_REQUEST_SOURCE_BRANCH_SHA","${CI_EXTERNAL_PULL_REQUEST_SOURCE_BRANCH_SHA}")


  /**
    * The target branch name of the pull request.
    *
    * gitlab version: 12.3
    * gitlab runner version: all
    */
  lazy val CI_EXTERNAL_PULL_REQUEST_TARGET_BRANCH_NAME:String = sys.env.getOrElse("CI_EXTERNAL_PULL_REQUEST_TARGET_BRANCH_NAME","${CI_EXTERNAL_PULL_REQUEST_TARGET_BRANCH_NAME}")


  /**
    * The HEAD SHA of the target branch of the pull request.
    *
    * gitlab version: 12.3
    * gitlab runner version: all
    */
  lazy val CI_EXTERNAL_PULL_REQUEST_TARGET_BRANCH_SHA:String = sys.env.getOrElse("CI_EXTERNAL_PULL_REQUEST_TARGET_BRANCH_SHA","${CI_EXTERNAL_PULL_REQUEST_TARGET_BRANCH_SHA}")
