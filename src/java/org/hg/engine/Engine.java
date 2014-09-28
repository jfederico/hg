package org.hg.engine;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.lti.api.LTIToolProvider;
import org.lti.api.LTIStore;
import org.lti.impl.LTIStoreImpl;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public abstract class Engine {
    private static final Logger log = Logger.getLogger(Engine.class);

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

    protected Map<String, String> params;
    protected Map<String, Object> config;
    protected Map<String, String> grails_params;
    protected String endpoint;

    protected LTIStore ltiStore;
    protected LTIToolProvider tp;
    protected JSONObject tpMeta;
    protected JSONObject tcMeta;

    public Engine(HttpServletRequest request, Map<String, String> params, Map<String, Object> config, String endpoint)
            throws Exception {
        this.config = config;
        this.grails_params = new HashMap<String, String>();
        for( int i=0; i < GRAILS_PARAMS.length; i++ ){
            if( params.containsKey(GRAILS_PARAMS[i]) ){
                grails_params.put(GRAILS_PARAMS[i], params.get(GRAILS_PARAMS[i]));
                params.remove(GRAILS_PARAMS[i]);
            }
        }
        this.params = params;
        this.endpoint = endpoint;

        try {
            this.tpMeta = new JSONObject();
            this.tcMeta = new JSONObject();

            ltiStore = LTIStoreImpl.getInstance();
            
            String _endpoint = (request.isSecure()? "https": "http") + "://" + this.endpoint + "/" + this.grails_params.get("application") + "/" + this.grails_params.get("tenant") + "/lti/" + this.grails_params.get("version"); 
            log.debug(_endpoint);

            Map<String, Object> lti_cfg = (Map<String, Object>)config.get("lti");
            this.tp = ltiStore.createToolProvider(_endpoint, (String)lti_cfg.get("key"), (String)lti_cfg.get("secret"), params, "1.0");
            if( !this.tp.hasValidSignature() )
                throw new Exception("OAuth signature is NOT valid");
            else
                log.debug("OAuth signature is valid");
            
            /*
            this.tp.overrideParameters(getJSONOverride());
            if( !this.tp.hasRequiredParameters(getJSONRequiredParameters()) )
                throw new AmbasadoroException("Missing required parameters", "OAuthError");
            else
                log.debug("All required parameters are included");
            */
            
        } catch( Exception e) {
            throw e;
        }

    }

    protected CompletionResponse completionResponse;

    public abstract Map<String, String> getCompletionResponse();
    public abstract void setCompletionResponseCommand(CompletionResponse completionResponse);

    public Map<String, Object> getConfig(String type) {
        return this.config;
    }
}
