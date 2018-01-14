AWS X-Ray Parameter Whitelist - Java 
===================

Configure AWS X-Ray with an alternate parameter whitelist file.

AWS X-Ray supports a parameter whitelist file which specifies what parameters should be included in trace segments when performing outgoing AWS SDK calls. The default parameter file which is included with the X-Ray SDK adds parameters to the following services: DynamoDB, SQS, Lambda (file is available: [here](https://github.com/aws/aws-xray-sdk-java/blob/master/aws-xray-recorder-sdk-aws-sdk/src/main/resources/com/amazonaws/xray/handlers/DefaultOperationParameterWhitelist.json). If you wish to include parameters for other services there is need to configure a custom parameter whitelist file. In the AWS Java SDK this requires passing a custom instance of X-Ray [TracingHandler](https://github.com/aws/aws-xray-sdk-java/blob/master/aws-xray-recorder-sdk-aws-sdk/src/main/java/com/amazonaws/xray/handlers/TracingHandler.java#L105) configured with the custom parameters whitelist. 

This project aims at providing an alternate parameter whitelist with support for additional services not yet included in the default implementation. Additionally, it provides an easy way to include a custom parameter whitelist file via system or environment properties without requiring code changes.

Services added in the provided parameter whitelist:
* S3 - Request parameters: BucketName, Key, VersionId, Prefix

# Installation

Project provides a drop-in replacement jar which should be used instead of the AWS X-Ray SDK jar of: `aws-xray-recorder-sdk-aws-sdk-instrumentor`. The project jar includes the alternate configuration for the X-Ray tracing request handler. 

## Adding the Jar as a Dependency

Stay tuned. We are still in the process of publishing the project to JCenter.

## Compiling from Source

Clone (or download) the project. Then run:

```
./gradlew assemble
```
Target jar will be created at: `build/lib`. Jar will be named: `aws-xray-parameter-whitelist-instrumentor-<version>.jar`. Add the jar to your application classpath and make sure to remove: `aws-xray-recorder-sdk-aws-sdk-instrumentor`.


