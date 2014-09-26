package org.hg.engine;

import java.util.HashMap;
import java.util.Map;

public abstract class Engine {

    public static String PARAM_CONTROLLER   = "controller";
    public static String PARAM_ACTION       = "action";
    public static String PARAM_TENANT       = "tenant";
    public static String PARAM_ENGINE       = "engine";
    public static String PARAM_VERSION      = "version";
    public static String PARAM_ACT          = "act";
    public static String PARAM_CMD          = "cmd";
    public static String[] GRAILS_PARAMS    = new String[] { PARAM_CONTROLLER,
                                                             PARAM_ACTION,
                                                             PARAM_TENANT,
                                                             PARAM_ENGINE,
                                                             PARAM_VERSION,
                                                             PARAM_ACT,
                                                             PARAM_CMD
                                                           };
    
    protected Map<String, String> params;
    protected Map<String, String> grails_params;
    protected Map<String, Object> config;

    public Engine(Map<String, String> params, Map<String, Object> config){
        this.config = config;
        this.grails_params = new HashMap<String, String>();
        for( int i=0; i < GRAILS_PARAMS.length; i++ ){
            if( params.containsKey(GRAILS_PARAMS[i]) ){
                grails_params.put(GRAILS_PARAMS[i], params.get(GRAILS_PARAMS[i]));
                params.remove(GRAILS_PARAMS[i]);
            }
        }
        this.params = params;
    }

    protected CompletionContent completionContent;

    public abstract Map<String, String> getCompletionContent();
    public abstract void setCompletionContentCommand(CompletionContent completionContent);

    public Map<String, Object> getConfig(String type) {
        return this.config;
    }
}
