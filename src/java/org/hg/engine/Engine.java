package org.hg.engine;

import java.util.HashMap;
import java.util.Map;

import org.hg.domain.Type;

public abstract class Engine {

    public static String PARAM_CONTROLLER   = "controller";
    public static String PARAM_ACTION       = "action";
    public static String PARAM_TYPE         = "type";
    public static String PARAM_ID           = "id";
    public static String PARAM_ACT          = "act";
    public static String[] GRAILS_PARAMS    = new String[] {PARAM_CONTROLLER, PARAM_ACTION, PARAM_TYPE, PARAM_ID, PARAM_ACT};
    
    protected Map<String, String> params;
    protected Map<String, String> grails_params;
    protected Type config;

    public Engine(Map<String, String> params, Type config){
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

    //public abstract String getSingleSignOnURL();
    //public abstract String getCommonCartridgeXML();
    //public abstract String getRSSFeedXML();
    //public abstract String getIconSourceURL();
    //public abstract String getConfigurationHTML();
    //public abstract String getFrontendHTML();
    public Type getConfig(String type) {
        return this.config;
    }
}
