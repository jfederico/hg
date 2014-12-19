package org.lti.v1;

import java.util.Map;

import net.oauth.OAuth;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lti.LTIException;
import org.lti.LTIToolProvider;
import org.lti.LTIv1;

public class LTIv1p0ToolProvider extends LTIToolProvider implements LTIv1 {

    private static final Logger log = Logger.getLogger(LTIv1p0ToolProvider.class);

    public LTIv1p0ToolProvider(String endpoint, String key, String secret, Map<String, String> params)
            throws LTIException, Exception {
        super(endpoint, key, secret, params);
        
        if( params.containsKey(OAuth.OAUTH_CONSUMER_KEY)) oauth_consumer_key = params.get(OAuth.OAUTH_CONSUMER_KEY); else throw new LTIException("OAuthError", "Parameter [" + OAuth.OAUTH_CONSUMER_KEY + "] not included");
        if( params.containsKey(OAuth.OAUTH_SIGNATURE)) oauth_signature = params.get(OAuth.OAUTH_SIGNATURE); else throw new LTIException("OAuthError", "Parameter [" + OAuth.OAUTH_SIGNATURE + "] not included");
        if( hasValidSignature() ) log.debug("OAuth signature is valid"); else throw new Exception("OAuth signature is NOT valid");
    }

    public String getLTIVersion(){
        return VERSION;
    }
    
    public void validateRequiredParameters(JSONArray requiredParameters)
            throws LTIException, Exception {
        log.debug("Validating required parameters: " + requiredParameters.toString());
        this.validateRequiredParameters.execute(this.params, requiredParameters);
    }

    public boolean hasRequiredParameters(JSONArray requiredParameters)
            throws LTIException, Exception {
        boolean response = true;
        String missingParams = "";
        for (int i = 0; i < requiredParameters.length(); i++) {
            JSONObject requiredParam = requiredParameters.getJSONObject(i);
            String paramName = requiredParam.getString("name");

            if( !params.containsKey(paramName) ){
                missingParams += (missingParams.length()>0)? ", ": "";
                missingParams += paramName;
                response = false;
            }
        }
        if(!response) throw new LTIException("ToolProviderError", "Required Parameters [" + missingParams + "] not included");
        else return response;
    }

    public void overrideParameters(JSONArray overrides)
            throws Exception {
        JSONObject override;
        String source;
        String target;

        for (int i = 0; i < overrides.length(); i++) {
            override = overrides.getJSONObject(i);
            source = override.getString("source");
            target = override.getString("target");
            if( params.containsKey(target) ){
                params.put(source, params.get(target));
            }
        }
        
    }

    public Map<String, String> getParameters(){
        return params;
    }
    
    public String getParameter(String key){
        return params.get(key);
    }

    public void putParameter(String key, String value){
        params.put(key, value);
    }
    
    public boolean hasParameter(String key){
        return params.containsKey(key);
    }
    
    public boolean isToolConsumerInfoProductFamilyCode(String code) {
        return this.params.get(TOOL_CONSUMER_INFO_PRODUCT_FAMILY_CODE).equals(code);
    }

    public static Logger getLog() {
        return log;
    }
}
