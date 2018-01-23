package com.github.functionalone.xray.handlers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class GenerateParameterWhitelist {

    public static final String USAGE = "This program will generate a parameter whitelise based upon reflection. Paramaters are read from the System properties:\n"
            + "gen.file: [output file name, default to whitelist.json]\n"
            + "gen.class: <class to generate whitelist for, example: 'com.amazonaws.services.s3.AmazonS3Client'>\n"
            + "gen.service: <service name, example: 'Amazon S3'>\n"
            + "gen.params: <parameter names comma separated, example: BucketName,Key,VersionId,Prefix>\n"
            + "gen.verbose: [true|false, default:false, will print more info on stdout regarding what is being done]";

    private static String capitalizeFirst(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
    
    private static boolean VERBOSE = false;
    
    private static void debug(String msg) {
        if(VERBOSE) {
            System.out.println(msg);
        }
    }

    /**
     * Uses system properties as parameters. See usage string.
     * 
     * @param args
     */
    public static void main(String[] args) throws Exception {
        String file = System.getProperty("gen.file", "whitelist.json"), serviceClass = System.getProperty("gen.class"),
                serviceName = System.getProperty("gen.service"), paramsStr = System.getProperty("gen.params");
        VERBOSE = Boolean.parseBoolean(System.getProperty("gen.verbose", "false"));
        if (null == serviceClass || null == serviceName || null == paramsStr) {
            System.err.println("\n\nMissing system parameters in order to run!!!\n\n" + USAGE);
            System.exit(1);
            return;
        }
        List<String> paramsList = Arrays.asList(paramsStr.split(","));
        File f = new File(file);        
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = (ObjectNode) mapper.createObjectNode();
        ObjectNode serviceNode = (ObjectNode) mapper.createObjectNode();
        root.put(serviceName, serviceNode);
        ObjectNode operations = (ObjectNode) mapper.createObjectNode();
        serviceNode.put("operations", operations);        
        List<String> noParamMethods = new ArrayList<String>();
        Class cls = Class.forName(serviceClass);
        Method[] methods = cls.getDeclaredMethods();
        for (Method method : methods) {
            if (Modifier.isPublic(method.getModifiers())) {
                Class[] params = method.getParameterTypes();
                if (params.length == 1 && params[0].getName().endsWith("Request")) {
                    Class p = params[0];
                    debug("Processing Method: " + method.getName() + " with param: " + p.getSimpleName());
                    Method[] paramMethods = p.getMethods();
                    ObjectNode paramsNode = (ObjectNode) mapper.createObjectNode();
                    ArrayNode request_parameters = (ArrayNode) mapper.createArrayNode();
                    paramsNode.put("request_parameters", request_parameters);
                    boolean hasParam = false;
                    for (Method paramMethod : paramMethods) {
                        String pName = paramMethod.getName();
                        if (pName.startsWith("get")) {
                            pName = pName.substring("get".length());
                            if (paramsList.contains(pName)) {
                                debug("\t param name: " + pName);
                                hasParam = true;
                                request_parameters.add(pName);
                            }
                        }
                    }
                    if (!hasParam) {
                        noParamMethods.add(method.getName());
                    } else {
                        operations.put(capitalizeFirst(method.getName()), paramsNode);
                    }
                }
            }
        }
        System.out.println("Methods without params: " + noParamMethods);
        BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(f));
        bout.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(root));
        bout.close();
        System.out.println("Json paramter whitelist written to file: " + f.getAbsolutePath());
    }

}
