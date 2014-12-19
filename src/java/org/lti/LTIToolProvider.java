package org.lti;

import java.util.Map;
import java.util.Properties;

import net.oauth.OAuthMessage;
import net.oauth.signature.HMAC_SHA1;

import org.apache.log4j.Logger;
import org.json.JSONArray;

public abstract class LTIToolProvider {

    private static final Logger log = Logger.getLogger(LTIToolProvider.class);

    protected Map<String, String> params;
    protected String oauth_consumer_key;
    protected String oauth_nonce;
    protected String oauth_callback;
    protected String oauth_signature;
    protected String oauth_signature_method;
    protected String oauth_version;
    protected String oauth_timestamp;
    
    protected String endpoint;
    protected String key;
    protected String secret;

    public LTIToolProvider(String endpoint, String key, String secret, Map<String, String> params)
            throws LTIException, Exception {
        this.endpoint = endpoint;
        this.key = key;
        this.secret = secret;
        this.params = params;
    }

    public boolean hasValidSignature() throws LTIException, Exception {
        boolean response = false;
        log.debug("Checking if the OAuth signature is valid. endpoint=" + this.endpoint + ", secret=" + this.secret );
        Object postProp = sanitizePrametersForBaseString();
        
        OAuthMessage oam = new OAuthMessage("POST", this.endpoint, ((Properties)postProp).entrySet());
        HMAC_SHA1 hmac = new HMAC_SHA1();
        hmac.setConsumerSecret(this.secret);
        String baseString = HMAC_SHA1.getBaseString(oam);
        log.debug("Base Message String = [ " + baseString + " ]\n");
        if( hmac.isValid(oauth_signature, baseString) )
            response = true;
        log.debug("Calculated: " + hmac.getSignature(baseString) + " Received: " + oauth_signature);

        return response;
    }

    protected Properties sanitizePrametersForBaseString() {
        Properties reqProp = new Properties();
        for (String key : this.params.keySet()) {
            if (key.equals("oauth_signature") ) {
                // We don't need this as part of the base string
                continue;
            }
            String value = this.params.get(key);
            reqProp.setProperty(key, value);
        }
        return reqProp;
    }

    protected ValidateRequiredParameters validateRequiredParameters;

    public void setValidateRequiredParametersCommand(ValidateRequiredParameters validateRequiredParameters) {
        this.validateRequiredParameters = validateRequiredParameters;
    }
    
    public abstract void validateRequiredParameters(JSONArray requiredParameters) throws LTIException, Exception;
    public abstract boolean hasRequiredParameters(JSONArray requiredParameters) throws LTIException, Exception;
    public abstract void overrideParameters(JSONArray overrides) throws Exception;
    public abstract Map<String, String> getParameters();
    public abstract String getParameter(String key);
    public abstract void putParameter(String key, String value);
    public abstract boolean hasParameter(String key);
    public abstract boolean isToolConsumerInfoProductFamilyCode(String code);
}
