package org.hg.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.lti.api.LTIToolProvider;
import org.lti.api.SimpleLTIStore;
import org.apache.log4j.Logger;
import org.json.JSONArray;

public class Engine implements IEngine {
    private static final Logger log = Logger.getLogger(Engine.class);

    protected Map<String, String> params;
    protected Map<String, Object> config;
    protected Map<String, String> grails_params;
    protected String endpoint;

    protected LTIToolProvider tp;

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

        if( request.getMethod().equals("POST") ) {
            try {
                String endpoint_url = (request.isSecure()? "https": "http") + "://" + this.endpoint + "/" + this.grails_params.get("application") + "/" + this.grails_params.get("tenant") + "/lti/" + this.grails_params.get("version"); 
                
                this.tp = SimpleLTIStore.createToolProvider(this.params, this.config, endpoint_url);
                
                Map<String, Object> profile = getProfile();
                overrideParameters(profile);
                validateRequiredParameters(profile);

            } catch( Exception e) {
                throw e;
            }
        }
    }

    protected CompletionResponse completionResponse;

    public Map<String, Object> getConfig(String type) {
        return this.config;
    }

    public Map<String, String> getCompletionResponse() 
        throws Exception {
        return null;
    }

    public void setCompletionResponseCommand(CompletionResponse completionResponse) {
    }

    public LTIToolProvider getToolProvider() {
        return this.tp;
    }
    
    private Map<String, Object> getProfile(){
        Map<String, Object> return_profile = new HashMap<String, Object>();

        @SuppressWarnings("unchecked")
        Map<String, Object> lti_cfg = (Map<String, Object>)config.get("lti");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> profiles = (List<Map<String, Object>>)lti_cfg.get("profiles");
        for( Map<String, Object> profile : profiles ){
            if( tp.isToolConsumerInfoProductFamilyCode((String)profile.get("name")) ) {
                return_profile = profile;
                break;
            }
        }

        log.debug(return_profile);
        return return_profile;
    }

    private void overrideParameters(Map<String, Object> profile)
            throws Exception {
        @SuppressWarnings("unchecked")
        JSONArray json_override_parameters = new JSONArray((ArrayList<Object>)profile.get("overrides"));
        log.debug(json_override_parameters.toString());
        this.tp.overrideParameters(json_override_parameters);
    }

    private void validateRequiredParameters(Map<String, Object> profile)
        throws Exception {
        @SuppressWarnings("unchecked")
        JSONArray json_required_parameters = new JSONArray((ArrayList<Object>)profile.get("required"));
        if( !this.tp.hasRequiredParameters(json_required_parameters) )
            throw new Exception("Missing required parameters");
        else
            log.debug("All required parameters are included");
    }
}
