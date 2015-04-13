package org.hg.engine;

import java.util.Map;

public interface IEngine {
    public static String PARAM_ENDPOINT     = "endpoint";
    public static String PARAM_APPLICATION  = "application";
    public static String PARAM_CONTROLLER   = "controller";
    public static String PARAM_ACTION       = "action";
    public static String PARAM_TENANT       = "tenant";
    public static String PARAM_ENGINE       = "engine";
    public static String PARAM_ACT          = "act";
    public static String PARAM_CMD          = "cmd";
    public static String[] GRAILS_PARAMS    = new String[] { 
        PARAM_ENDPOINT, PARAM_APPLICATION, PARAM_CONTROLLER, PARAM_ACTION, PARAM_TENANT, PARAM_ENGINE, PARAM_ACT, PARAM_CMD
    };

    public static String COMPLETION_RESPONSE_TYPE_HTML  = "html";
    public static String COMPLETION_RESPONSE_TYPE_URL   = "url";
    public static String COMPLETION_RESPONSE_TYPE_XML   = "xml";

    public static String ENGINE_TYPE_CONFIG         = "config";
    public static String ENGINE_TYPE_LAUNCH         = "launch";
    public static String ENGINE_TYPE_REGISTRATION   = "registration";
    public static String ENGINE_TYPE_RESOURCE       = "resource";
    public static String ENGINE_TYPE_API            = "api";
    public static String[] ENGINE_TYPES    = new String[] {
        ENGINE_TYPE_CONFIG, ENGINE_TYPE_LAUNCH, ENGINE_TYPE_REGISTRATION, ENGINE_TYPE_RESOURCE, ENGINE_TYPE_API
    };

    public static String ENGINE_ACT_CC   = "cc";
    public static String ENGINE_ACT_SSO  = "sso";
    public static String ENGINE_ACT_UI   = "ui";

    abstract Map<String, Object> getCompletionResponse() throws Exception;
    abstract void setCompletionResponseCommand(CompletionResponse completionResponse);
    abstract String getEndpointURL();

}
