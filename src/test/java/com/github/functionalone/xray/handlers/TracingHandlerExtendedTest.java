package com.github.functionalone.xray.handlers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.auth.profile.internal.AwsProfileNameLoader;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.entities.Segment;
import com.amazonaws.xray.entities.Subsegment;

public class TracingHandlerExtendedTest {
  
  public static final String S3_BUCKET_SYS_PROP_NAME = "aws-xray-whitelist.s3-bucket";
  public static final String S3_BUCKET_ENV_PROP_NAME = "AWS_XRAY_WHITELIST_TEST_S3_BUCKET";
  
  private static String S3_BUCKET = null;
  private static final String S3_KEY = "aws-xray-whitelist-java-test/s3-test-object";

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    System.setProperty(AwsProfileNameLoader.AWS_PROFILE_SYSTEM_PROPERTY, "test");
    System.setProperty("com.amazonaws.xray.strategy.contextMissingStrategy", "LOG_ERROR");
    if(S3_BUCKET == null) {
      S3_BUCKET = System.getProperty(S3_BUCKET_SYS_PROP_NAME);
    }
    if(S3_BUCKET == null) {
      S3_BUCKET = System.getenv(S3_BUCKET_ENV_PROP_NAME);
    }    
  }

  @Test
  public void testS3() {
    if(S3_BUCKET == null) {
      fail("S3 Bucket name is not defined. Make sure to define system property: " + S3_BUCKET_SYS_PROP_NAME);      
    }
    Segment s = AWSXRay.beginSegment("whitelist-test");
    System.out.println("starting S3 test...");        
    AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
    S3Object obj = s3.getObject(S3_BUCKET, S3_KEY);
    assertNotNull(obj);
    AWSXRay.endSegment();
    List<Subsegment> subsegments = s.getSubsegments();
    assertEquals(1, subsegments.size());
    Subsegment sub = subsegments.get(0);
    assertEquals(sub.getAws().get("bucket_name"), S3_BUCKET);
    assertEquals(sub.getAws().get("key"), S3_KEY);    
    System.out.println("subsegments aws: " + sub.getAws());
  }

}
