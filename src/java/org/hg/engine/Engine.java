package org.hg.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.lti.LTIException;
import org.lti.ToolProvider;
import org.lti.SimpleLTIStore;

public class Engine implements IEngine {
    private static final Logger log = Logger.getLogger(Engine.class);

    protected Map<String, String> params;
    protected Map<String, Object> config;
    protected Map<String, String> grails_params;
    protected String endpoint;
    protected String endpoint_url;

    protected ToolProvider tp;

    public Engine(HttpServletRequest request, Map<String, String> params, Map<String, Object> config, String endpoint, Map<String, String> session_params)
            throws Exception {
        log.debug("XX: Instantiating Engine()");

        this.config = config;
        this.grails_params = new HashMap<String, String>();
        this.endpoint = endpoint;

        try {
            String type = params.get(PARAM_ENGINE);
            validateEngineType(type);

            for( int i=0; i < GRAILS_PARAMS.length; i++ ){
                if( params.containsKey(GRAILS_PARAMS[i]) ){
                    this.grails_params.put(GRAILS_PARAMS[i], params.get(GRAILS_PARAMS[i]));
                    params.remove(GRAILS_PARAMS[i]);
                }
            }
            this.endpoint_url = (request.isSecure()? "https": "http") + "://" + this.endpoint + "/" + this.grails_params.get("application") + "/" + this.grails_params.get("tenant") + "/" + type + "/" + this.grails_params.get("version"); 

            /*
            //Temporary working params
            Map<String, String> _params;
            if( request.getMethod().equals("POST") ) {
                _params = params;
            } else {
                _params = session_params;
            }
            this.params = _params;
            */
            if ( this.grails_params.get(PARAM_ENGINE).equals(ENGINE_TYPE_LAUNCH) && this.grails_params.get(PARAM_ACT).equals(ENGINE_ACT_SSO) ||
                 this.grails_params.get(PARAM_ENGINE).equals(ENGINE_TYPE_LAUNCH) && this.grails_params.get(PARAM_ACT).equals(ENGINE_ACT_UI) )
            {
                 this.params = session_params;
                 log.debug("XX: It is going to create the tool provider");
                 this.tp = SimpleLTIStore.createToolProvider(this.params, this.config, this.endpoint_url);
                 log.debug("XX: The tool provider was created");

                 Map<String, Object> profile = getProfile();
                 log.debug("XX: Overriding tool parameters");
                 overrideParameters(profile);
                 log.debug("XX: Validating tool required parameters");
                 validateRequiredParameters(profile);
                 log.debug("XX: The tool required parameters have been validated");
            } else if ( this.grails_params.get(PARAM_ENGINE).equals(ENGINE_TYPE_REGISTRATION) ) {
                this.params = session_params;
                this.tp = SimpleLTIStore.createToolProvider(this.params, this.config, this.endpoint_url);

                Map<String, Object> profile = getProfile();
                validateRequiredParameters(profile);
            } else {
                this.params = params;
            }
        } catch( Exception e) {
            throw e;
        }
    }

    protected CompletionResponse completionResponse;

    public Map<String, Object> getConfig(String type) {
        return this.config;
    }

    public Map<String, Object> getCompletionResponse()
            throws Exception {
        return null;
    }

    public void setCompletionResponseCommand(CompletionResponse completionResponse) {
    }

    public ToolProvider getToolProvider() {
        return this.tp;
    }
    
    private Map<String, Object> getProfile() {
        Map<String, Object> return_profile = new HashMap<String, Object>();

        @SuppressWarnings("unchecked")
        Map<String, Object> lti_cfg = (Map<String, Object>)this.config.get("lti");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> profiles = lti_cfg != null? (List<Map<String, Object>>)lti_cfg.get("profiles"): null;

        String profile_name = "moodle"; //this.tp.getProductFamilyCode();
        if( profiles != null ) {
            for( Map<String, Object> profile : profiles ){
                if( profile_name.equals((String)profile.get("name")) ) {
                    return_profile = profile;
                    break;
                }
            }
        }

        return return_profile;
    }

    @SuppressWarnings("unchecked")
    private void overrideParameters(Map<String, Object> full_profile)
            throws Exception {
        Map<String, Object> profile = (HashMap<String, Object>)full_profile.get("profile");
        ArrayList<Object> overrides = (ArrayList<Object>)profile.get("overrides");
        for( Object override: overrides ) {
            String source = (String)((Map<String, Object>)override).get("source");
            String target = (String)((Map<String, Object>)override).get("target");
            String default_value = (String)((Map<String, Object>)override).get("default_value");
            if( this.tp.hasParameter(source) )
                this.tp.putParameter(source, this.tp.getParameter(target));
            else
                this.tp.putParameter(source, default_value);
        }
    }

    @SuppressWarnings("unchecked")
    private void validateRequiredParameters(Map<String, Object> full_profile)
            throws Exception {
        Map<String, Object> profile = (HashMap<String, Object>)full_profile.get("profile");
        ArrayList<Object> required_params = (ArrayList<Object>)profile.get("required_params");
        ArrayList<String> requiredParams = new ArrayList<String>();
        log.debug(required_params);
        for(Object required_param: required_params ){
            log.debug((String)((Map<String, Object>)required_param).get("name"));
            requiredParams.add( (String)((Map<String, Object>)required_param).get("name") );
        }

        String[] requiredParameters = requiredParams.toArray(new String[requiredParams.size()]);
        log.debug("XX: Validation starting");
        log.debug(requiredParameters);
        try {
            this.tp.validateParameters(requiredParameters);
        } catch ( Exception e ) {
            throw new LTIException(LTIException.MESSAGEKEY_MISSING_PARAMETERS, "Tool Provider required parameters missing. " + e.getMessage());
        }
    }

    private void validateEngineType(String type)
            throws Exception {
        for( int i=0; i < ENGINE_TYPES.length; i++ ){
            if( type.equals(ENGINE_TYPES[i]) ){
                return;
            }
        }
        log.debug("Engine type is not valid");
        Exception e = new java.lang.Exception("Engine type [" + type + "] is not valid");
        throw e;
    }

    public String getEndpointURL() {
        return this.endpoint_url;
    }
}
