package org.hg.engine;

import java.util.Map;

import org.lti.api.LTIToolProvider;

public interface IEngine {

    public static String PARAM_ENDPOINT     = "endpoint";
    public static String PARAM_APPLICATION  = "application";
    public static String PARAM_CONTROLLER   = "controller";
    public static String PARAM_ACTION       = "action";
    public static String PARAM_TENANT       = "tenant";
    public static String PARAM_ENGINE       = "engine";
    public static String PARAM_VERSION      = "version";
    public static String PARAM_ACT          = "act";
    public static String PARAM_CMD          = "cmd";
    public static String[] GRAILS_PARAMS    = new String[] { 
        PARAM_ENDPOINT, PARAM_APPLICATION, PARAM_CONTROLLER, PARAM_ACTION, PARAM_TENANT, PARAM_ENGINE, PARAM_VERSION, PARAM_ACT, PARAM_CMD };

    public static String COMPLETION_RESPONSE_TYPE_HTML  = "html";
    public static String COMPLETION_RESPONSE_TYPE_URL   = "url";
    public static String COMPLETION_RESPONSE_TYPE_XML   = "xml";

    public static String ENGINE_TYPE_LTI    = "lti";

    public static String ENGINE_ACTION_CC   = "cc";
    public static String ENGINE_ACTION_SSO  = "sso";
    public static String ENGINE_ACTION_UI   = "ui";

    abstract Map<String, String> getCompletionResponse() throws Exception;
    abstract void setCompletionResponseCommand(CompletionResponse completionResponse);
    abstract LTIToolProvider getToolProvider();
    abstract String getEndpoint();

}
