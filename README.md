AWS X-Ray Parameter Whitelist - Java 
===================

Configure AWS X-Ray with an alternate parameter whitelist file.

AWS X-Ray supports a parameter whitelist file which specifies what parameters should be included in trace segments when performing outgoing AWS SDK calls. The default parameter file which is included with the X-Ray SDK adds parameters to the following services: DynamoDB, SQS, Lambda (file is available: [here](https://github.com/aws/aws-xray-sdk-java/blob/master/aws-xray-recorder-sdk-aws-sdk/src/main/resources/com/amazonaws/xray/handlers/DefaultOperationParameterWhitelist.json)). If you wish to include parameters for other services there is need to configure a custom parameter whitelist file. In the AWS Java SDK this requires passing a custom instance of X-Ray [TracingHandler](https://github.com/aws/aws-xray-sdk-java/blob/master/aws-xray-recorder-sdk-aws-sdk/src/main/java/com/amazonaws/xray/handlers/TracingHandler.java#L105) configured with the custom parameters whitelist. 

This project aims at providing an alternate parameter whitelist with support for additional services not yet included in the default implementation. Additionally, it provides an easy way to include a custom parameter whitelist file via system or environment properties without requiring code changes.

Services added in the provided parameter whitelist:
* S3 - Request parameters: BucketName, Key, VersionId, Prefix

Parameter whitelist file is available: [here](https://github.com/functionalone/aws-xray-parameter-whitelist-java/blob/master/src/main/resources/com/github/functionalone/xray/handlers/ExtendedOperationParameterWhitelist.json).

# Installation

Project provides a drop-in replacement jar which should be used instead of the AWS X-Ray SDK jar of: `aws-xray-recorder-sdk-aws-sdk-instrumentor`. The project jar includes the alternate configuration for the X-Ray tracing request handler. This means that you can simply use the provided jar without any source code modifications, same way as the original `aws-xray-recorder-sdk-aws-sdk-instrumentor` is used.

## Adding the Jar as a Dependency

Build artifacts are available via Bintray's JCenter. Project binary distributions are available at: https://bintray.com/functionalone/maven/aws-xray-parameter-whitelist-instrumentor. To add the jar as a dependency, you will need to use the jcenter repository. For example, if you are using Gradle you will need to add the following section to the repositories closure:

```
repositories {
    jcenter()
}
```

And then add the following compilation dependency:
```
compile 'com.github.functionalone:aws-xray-parameter-whitelist-instrumentor:<version>'
```

Make sure to remove the `aws-xray-recorder-sdk-aws-sdk-instrumentor` compile dependency.

## Compiling from Source

Clone (or download) the project. Then run:

```
./gradlew assemble
```
Target jar will be created at: `build/lib`. Jar will be named: `aws-xray-parameter-whitelist-instrumentor-<version>.jar`. Add the jar to your application classpath and make sure to remove: `aws-xray-recorder-sdk-aws-sdk-instrumentor`.

# Parameter Whitelist Configuration

It is possible to configure a custom parameter whitelist file instead of the default one provided with the package. This can be done either via a the environment variable: `AWS_XRAY_WHITELIST_URL` or the System property: `alt.aws.xray.whitelist.url`. The System property takes precedence over the environment variable. Value should be set to a resource path as specified by [Class.getResource()](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#getResource-java.lang.String-). For example: `/com/myconpany/mypackage/MyParameterWhitelist.json`.

**Note**: If setting the System property programmatically, you need to set this before using the AWS SDK. The configuration is evaluated once upon first usage of the AWS SDK.  

