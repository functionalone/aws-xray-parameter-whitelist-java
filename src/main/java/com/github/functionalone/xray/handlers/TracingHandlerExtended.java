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
  
  private static final Log logger = LogFactory.getLog(TracingHandlerExtended.class);
  
  private static final URL OPERATION_PARAMETER_WHITELIST = TracingHandler.class.getResource("/com/github/functionalone/xray/handlers/ExtendedOperationParameterWhitelist.json");

  public TracingHandlerExtended() {
    this(AWSXRay.getGlobalRecorder(), null, OPERATION_PARAMETER_WHITELIST);
  }

  public TracingHandlerExtended(AWSXRayRecorder recorder, String accountId, URL operationParameterWhitelist) {
    super(recorder, accountId, operationParameterWhitelist);
  }    
  
}
