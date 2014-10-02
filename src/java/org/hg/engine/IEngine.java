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

    public abstract Map<String, String> getCompletionResponse();
    public abstract void setCompletionResponseCommand(CompletionResponse completionResponse);
    public abstract LTIToolProvider getToolProvider();

}
