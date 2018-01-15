package com.github.functionalone.xray.handlers;

import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.AWSXRayRecorder;
import com.amazonaws.xray.handlers.TracingHandler;

/**
 * Implementation of tracing handler which uses an alternate whitelist configuration. 
 * Will use the following resource file for the whitelist file: com/github/functionalone/xray/handlers/ExtendedOperationParameterWhitelist.json .
 *
 */
public class TracingHandlerExtended extends TracingHandler {
  
  public static final String WHITELIST_URL_SYS_PROP_NAME = "alt.aws.xray.whitelist.url";
  public static final String WHITELIST_URL_ENV_PROP_NAME = "AWS_XRAY_WHITELIST_URL";  
  
  private static final String DEFAULT_WHITELIST_RESOURCE_URL = "/com/github/functionalone/xray/handlers/ExtendedOperationParameterWhitelist.json";
  private static final Log logger = LogFactory.getLog(TracingHandlerExtended.class);
  
  /**
   * Create a url and if fails will log the exception and return null 
   * @param s - the string to use for the url
   * @return the new url or null if fails
   */
  private static URL newUrlWithLog(String s) {
    if(null == s || s.length() == 0) {
      return null;
    }
    try {
      URL res = TracingHandler.class.getResource(s);
      logger.debug("Whitelist url set to: " + res);
      return res;
    } catch (Exception e) {
      logger.error("Failed setting whitelist url with value: [" + s + "].", e);
    }
    return null;
  }
  
  /**
   * Will try to resolve in this order: 
   * - system property: alt.aws.xray.whitelist.url
   * - environment property: AWS_XRAY_WHITELIST_URL
   * - default: resource: /com/github/functionalone/xray/handlers/ExtendedOperationParameterWhitelist.json
   * @return the found url or null if there is a problem
   */
  private static URL getConfiguredWhitelistUrl() {    
    URL res = newUrlWithLog(System.getProperty(WHITELIST_URL_SYS_PROP_NAME));
    if(null != res) {
      return res;
    }
    res = newUrlWithLog(System.getenv(WHITELIST_URL_ENV_PROP_NAME));
    if(null != res) {
      return res;
    }
    return newUrlWithLog(DEFAULT_WHITELIST_RESOURCE_URL);    
  }
  
  private static final URL OPERATION_PARAMETER_WHITELIST = getConfiguredWhitelistUrl();

  public TracingHandlerExtended() {
    this(AWSXRay.getGlobalRecorder(), null, OPERATION_PARAMETER_WHITELIST);
  }

  public TracingHandlerExtended(AWSXRayRecorder recorder, String accountId, URL operationParameterWhitelist) {
    super(recorder, accountId, operationParameterWhitelist);
  }    
  
}
