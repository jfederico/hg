package org.lti;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import net.oauth.OAuth;
import net.oauth.OAuthMessage;
import net.oauth.signature.HMAC_SHA1;

import org.apache.log4j.Logger;

public abstract class ToolProvider {

    private static final Logger log = Logger.getLogger(ToolProvider.class);

    protected Map<String, String> params;
    
    protected static final String[] OAUTH_REQUIERED_PARAMS = { 
        OAuth.OAUTH_CONSUMER_KEY, OAuth.OAUTH_NONCE, OAuth.OAUTH_CALLBACK, OAuth.OAUTH_SIGNATURE, OAuth.OAUTH_SIGNATURE_METHOD, OAuth.OAUTH_VERSION, OAuth.OAUTH_TIMESTAMP
        };

    protected String endpoint;
    protected String key;
    protected String secret;

    public ToolProvider(String endpoint, String key, String secret, Map<String, String> params)
            throws LTIException, Exception {
        log.debug("XX: Instantiating ToolProvider()");
        this.endpoint = endpoint;
        this.key = key;
        this.secret = secret;
        this.params = new HashMap<String, String>(params);
    }

    public void validateParameters(String[] requiredParameters)
            throws LTIException, Exception {
        boolean success = true;

        String missingParams = "";
        for( String requiredParameter: requiredParameters ) {
            if( !this.params.containsKey(requiredParameter) ){
                missingParams += (missingParams.length()>0)? ", ": "";
                missingParams += requiredParameter;
                success = false;
            }
        }

        if(!success) throw new LTIException(LTIException.MESSAGEKEY_MISSING_PARAMETERS, "Parameters [" + missingParams + "] not included.");
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

    protected boolean hasValidSignature() throws LTIException, Exception {
        boolean response = false;
        log.debug("Checking if the OAuth signature is valid. endpoint=" + this.endpoint + ", secret=" + this.secret );
        Object postProp = sanitizePrametersForBaseString();
        
        OAuthMessage oam = new OAuthMessage("POST", this.endpoint, ((Properties)postProp).entrySet());
        HMAC_SHA1 hmac = new HMAC_SHA1();
        hmac.setConsumerSecret(this.secret);
        String baseString = HMAC_SHA1.getBaseString(oam);
        log.debug("Base Message String = [ " + baseString + " ]\n");
        if( hmac.isValid(this.params.get(OAuth.OAUTH_SIGNATURE), baseString) )
            response = true;
        log.debug("Calculated: " + hmac.getSignature(baseString) + " Received: " + this.params.get(OAuth.OAUTH_SIGNATURE));

        return response;
    }

    private Properties sanitizePrametersForBaseString() {
        Properties reqProp = new Properties();
        for (String key : this.params.keySet()) {
            if (key.equals(OAuth.OAUTH_SIGNATURE) ) {
                // We don't need this as part of the base string
                continue;
            }
            String value = this.params.get(key);
            reqProp.setProperty(key, value);
        }
        return reqProp;
    }

    public abstract String getLTIVersion();
}
