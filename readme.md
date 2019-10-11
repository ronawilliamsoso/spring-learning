# How to use this "Service Skeleton"

1) Checkout this repository.

2) Create a new bibucket repository and change your upstream url:
  
  - "git remote set-url origin <your repository path>" (looks like git@github.com:zoro-eu/REPOSITORY.git)
  - "git push --set-upstream origin master --force"

3) Rename application name and occurrence of "*ServiceSkeleton" (or "*service-skeleton") within your project name.
 
  - package name "com.zoro.eu.**skeleton**"
  - class names "**SpringBootServiceSkeleton**Application" and "**SpringBootServiceSkeletonApplication**Tests"
  - pom.xml **<artifactId>**, **<name>**, **<description>**
    
4) (optional) the **<artifactId>** will be used as final (build) name of the application (this includes services names, log-files, etc..).  
If you want to use another service name on your server, replace the variable within **<finalName>${artifactId}</finalName>** with another name (**DO NOT USE SPACES OR SPECIAL CHARACTERS**).  
    *The build process automatically replaces every relevant occurrence with this name, so it is not necessary to change anything else for a working CodeDeploy or Service Startup*

5) Enable Bitbucket pipelines for your repository: [How to](https://confluence.atlassian.com/bitbucket/get-started-with-bitbucket-pipelines-792298921.html#GetstartedwithBitbucketPipelines-pipelines_getting_started_step3Step1:EnableBitbucketPipelines)

6) Set pipeline environment variables (*at least: **APPLICATION_NAME***): [How to](https://confluence.atlassian.com/bitbucket/environment-variables-794502608.html)
  
  - the value of APPLICATION_NAME must match your application name within AWS CodeDeploy
  - All (except APPLICATION_NAME) of the environment variables listed below were already set globally, change them if you need. (e.g. to change aws region)

7) Push your project changes to **your** repository

---
## Local Development requirements
To make your service runnable outside of an EC2 instance (e.g. your local development system) you need to configure your AWS credentials locally. (see section 2.2.1 @  http://cloud.spring.io/spring-cloud-aws/1.2.x/)
The easiest way to achieve this is to install the [AWS CLI](https://docs.aws.amazon.com/de_de/cli/latest/userguide/installing.html) and use the [configure](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-getting-started.html) command  
You can also create the configuration files manually. [How to (Linux / MacOs)](https://docs.aws.amazon.com/de_de/cli/latest/userguide/cli-config-files.html)

*It is also possible to set environment variables for your run configuration in IntelliJ. You will need "**AWS_ACCESS_KEY**", "**AWS_SECRET_ACCESS_KEY**", "**AWS_REGION**".  
This needs to be configured per run configuration (at least one per microservice), so it's not recommended!*

---
## AWS ParameterStore
If you need to store credentials to other services that cannot be handled by the AWS EC2 instance profile (e.g. our google shopping account), create a new parameter within the [AWS Parameter Store](https://eu-central-1.console.aws.amazon.com/ec2/v2/home?region=eu-central-1#Parameters:)  

  - use our naming convention: /<application-name>/<profile-name>/<parameter-name>  *(e.g. "/feed-exporter/staging/google-shopping-user")*
  - use encrypted values for passwords or other sensitive data *(KMS Key ID: **alias/aws/ssm**)*
  - we have some "/zte/global/**" parameters for reused (global) parameters *(e.g. our ses credentials)*

**DO NOT ADD CREDENTIALS TO YOUR APPLICATION PROPERTIES**

---
## AWS EC2 requirements
This service skeleton using AWS Instance Profiles, so it is not necessary to add any AWS credentials within your application.properties (or .yml) files.  

Make sure that the IAM Role assigned to your EC2 instance has (**at least**) the following polices:
  
  * AmazonSSMReadOnlyAccess
  * AmazonEC2RoleforSSM
  * aws-ec2-ssh 
  * ZoroEC2_S3Access_For_CodeDeploy
  * ZoroEC2AccessToS3Bucket_zoro-eu-ir-userdata
  
- You may add additional policies that fits your service needs
  

---
## Additional info: Deploy to AWS CodeDeploy

### How To Use It
* Optional:  [Setup a application](http://docs.aws.amazon.com/codedeploy/latest/userguide/getting-started-walkthrough.html) with AWS CodeDeploy if you do not already have one.
* Add the required Environment Variables below in Build settings of your Bitbucket repository.


### Required Environment Variables
* `NEXUS_USER`:  Username for the maven nexus repository.
* `NEXUS_PWD`:  Password for the maven nexus repository.
* `AWS_SECRET_ACCESS_KEY`:  Secret key for a user with the required permissions.
* `AWS_ACCESS_KEY_ID`:  Access key for a user with the required permissions.
* `AWS_DEFAULT_REGION`:  Region where the target AWS CodeDeploy application is.
* `APPLICATION_NAME`: Name of AWS CodeDeploy application.
* `DEPLOYMENT_CONFIG`: AWS CodeDeploy Deployment Configuration (CodeDeployDefault.OneAtATime|CodeDeployDefault.AllAtOnce|CodeDeployDefault.HalfAtATime|Custom).
* `DEPLOYMENT_GROUP_NAME`: Name of the Deployment group in the application.
* `S3_BUCKET`:  Name of the S3 Bucket where source code to be deployed is stored.

### Required Permissions in AWS
It is recommended you [create](http://docs.aws.amazon.com/IAM/latest/UserGuide/id_users_create.html) a separate user account used for this deploy process.  This user should be associated with a group that has the `AWSCodeDeployFullAccess` and `AmazonS3FullAccess` [AWS managed policy](http://docs.aws.amazon.com/IAM/latest/UserGuide/access_policies_managed-vs-inline.html) attached for the required permissions to upload a new application revision and execute a new deployment using AWS CodeDeploy.
Note that the above permissions are more than what is required in a real scenario. For any real use, you should limit the access to just the AWS resources in your context.