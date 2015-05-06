package org.hg.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.oauth.OAuth;

import org.apache.log4j.Logger;
import org.lti.LTIException;
import org.lti.ToolProvider;

public class Engine implements IEngine {
    private static final Logger log = Logger.getLogger(Engine.class.getName());

    protected Map<String, String> params;
    protected Map<String, Object> config;
    protected Map<String, String> grails_params;
    protected String endpoint;
    protected String endpoint_url;

    protected ToolProvider tpn;

    public Engine(HttpServletRequest request, Map<String, String> params, Map<String, Object> config, String endpoint, Map<String, String> session_params)
            throws Exception {
        log.debug("====== Creating object::Engine()");
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
            this.endpoint_url = (request.isSecure()? "https": "http") + "://" + this.endpoint + "/" + this.grails_params.get(PARAM_APPLICATION) + "/" + this.grails_params.get(PARAM_TENANT) + "/" + type; 

            if ( this.grails_params.get(PARAM_ENGINE).equals(ENGINE_TYPE_LAUNCH) && this.grails_params.get(PARAM_ACT).equals(ENGINE_ACT_SSO) ||
                 this.grails_params.get(PARAM_ENGINE).equals(ENGINE_TYPE_LAUNCH) && this.grails_params.get(PARAM_ACT).equals(ENGINE_ACT_UI) )
            {
                 this.params = session_params;
                 String oauth_consumer_key = this.params.get(OAuth.OAUTH_CONSUMER_KEY);
                 log.debug(oauth_consumer_key);
                 Map<String, String> keypair = parseKeypair(oauth_consumer_key);

                 this.tpn = new ToolProvider(this.params, this.endpoint_url, keypair.get("key"), keypair.get("secret"), keypair.get("tc_profile_url"));
                 //TODO: If there is a TC, it should be loaded here.

                 Map<String, Object> profile = getProfile();
                 //aditionalParameters(profile);
                 overrideParameters(profile);
                 validateRequiredParameters(profile);

            } else if ( this.grails_params.get(PARAM_ENGINE).equals(ENGINE_TYPE_REGISTRATION) ) {
                this.params = session_params;
                this.tpn = new ToolProvider(this.params, this.endpoint_url);

            } else {
                this.params = params;
            }

        } catch( Exception e) {
            throw e;
        }
        log.debug("====== Created object::Engine()");
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
            if( this.tpn.hasParameter(source) )
                this.tpn.putParameter(target, this.tpn.getParameter(source));
            else
                this.tpn.putParameter(target, default_value);
        }
    }

    @SuppressWarnings("unchecked")
    private void validateRequiredParameters(Map<String, Object> full_profile)
            throws Exception {
        log.debug("Validate Engine required parameters");
        Map<String, Object> profile = (HashMap<String, Object>)full_profile.get("profile");
        ArrayList<Object> required_params = (ArrayList<Object>)profile.get("required_params");
        ArrayList<String> requiredParams = new ArrayList<String>();
        log.debug(required_params);
        for(Object required_param: required_params ){
            requiredParams.add( (String)((Map<String, Object>)required_param).get("name") );
        }

        String[] requiredParameters = requiredParams.toArray(new String[requiredParams.size()]);
        try {
            this.tpn.validateParameters(requiredParameters);
            log.debug("Engine required parameters are included");
        } catch ( Exception e ) {
            throw new LTIException(LTIException.MESSAGEKEY_MISSING_PARAMETERS, "Tool Provider required parameters missing. " + e.getMessage());
        }
    }

    private void validateEngineType(String type)
            throws Exception {
        for( int i=0; i < ENGINE_TYPES.length; i++ ) {
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

    private Map<String, String> parseKeypair(String _key) {
        Map<String, String> keypair = new HashMap<String, String>();
        @SuppressWarnings("unchecked")
        Map<String, Object> lti_cfg = (Map<String, Object>)this.config.get("lti");
        @SuppressWarnings("unchecked")
        List<Map<String, String>> keys = (List<Map<String, String>>)lti_cfg.get("keys");
        for ( Map<String, String> key : keys) {
            if( key.get("key").equals(_key) ) {
                keypair.put("id", (String)key.get("id"));
                keypair.put("key", (String)key.get("key"));
                keypair.put("secret", (String)key.get("secret"));
                keypair.put("tc_profile_url", (String)key.get("tc_profile_url"));
                break;
            }
        }

        return keypair;
    }
    
    public Map<String, String> getParameters() {
        return this.params;
    }
    
    public String getVerifiedUserFullName() {
        String userFullName;
        String userFirstName;
        String userLastName;

        log.debug("FullName for version1");
        userFullName = this.params.get("lis_person_name_full");
        if( userFullName == null || userFullName == "" ){
            userFirstName = this.params.get("lis_person_name_given");
            userLastName = this.params.get("lis_person_name_family");
            if( userFirstName != null && userFirstName != "" ){
                userFullName = userFirstName;
            }
            if( userLastName != null && userLastName != "" ){
                userFullName += userFullName.length() > 0? " ": "";
                userFullName += userLastName;
            }
        }

        return userFullName;
    }

    public String getVerifiedRoles() {
        String roles = "";

        log.debug("Roles for version1");
        roles = params.get("roles");

        return roles;
    }

    public String getVerifiedUserId() {
        String userId;

        log.debug("Roles for version1");
        userId = params.get("user_id");

        return userId;
    }

}
