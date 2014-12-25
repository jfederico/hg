package org.lti;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.oauth.OAuth;
import net.oauth.OAuthMessage;
import net.oauth.signature.HMAC_SHA1;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    public abstract String getLTILaunchPresentationReturnURL();
    
    public JSONObject getToolConsumerProfile(String query) {
        JSONObject toolConsumerProfile = ltiProxyRequest(query);
        log.debug(toolConsumerProfile);
        return toolConsumerProfile;
    }
    
    /** Make an API call */
    private JSONObject ltiProxyRequest(String query) {
        JSONObject ltiProxyResponse = null;
        StringBuilder urlStr = new StringBuilder(query);
        
        try {
            // open connection
            log.debug("doAPICall.call: " + query );

            URL url = new URL(urlStr.toString());
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setUseCaches(false);
            httpConnection.setDoOutput(true);
            httpConnection.setRequestMethod("GET");
            httpConnection.connect();

            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // read response
                InputStreamReader isr = null;
                BufferedReader reader = null;
                StringBuilder json = new StringBuilder();
                try {
                    isr = new InputStreamReader(httpConnection.getInputStream(), "UTF-8");
                    reader = new BufferedReader(isr);
                    String line;
                    while ( (line = reader.readLine()) != null ) {
                        json.append(line);
                    }
                } finally {
                    if (reader != null)
                        reader.close();
                    if (isr != null)
                        isr.close();
                }
                httpConnection.disconnect();

                String jsonString = json.toString();
                ltiProxyResponse = new JSONObject(jsonString); 
            } else {
                log.debug("ltiProxyRequest.HTTPERROR: Message=" + "BBB server responded with HTTP status code " + responseCode);
            }
        } catch(IOException e) {
            log.debug("ltiProxyRequest.IOException: Message=" + e.getMessage());
        } catch(IllegalArgumentException e) {
            log.debug("ltiProxyRequest.IllegalArgumentException: Message=" + e.getMessage());
        } catch(Exception e) {
            log.debug("ltiProxyRequest.Exception: Message=" + e.getMessage());
        }

        return ltiProxyResponse;
    }

    public Map<String, Object> jsonToMap(JSONObject json) throws JSONException
    {
        Map<String, Object> retMap = new HashMap<String, Object>();

        if(json != JSONObject.NULL)
        {
            retMap = toMap(json);
        }
        return retMap;
    }

    public Map<String, Object> toMap(JSONObject object) throws JSONException
    {
        Map<String, Object> map = new HashMap<String, Object>();

        @SuppressWarnings("unchecked")
        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext())
        {
            String key = keysItr.next();
            Object value = object.get(key);

            if(value instanceof JSONArray)
            {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject)
            {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public List<Object> toList(JSONArray array) throws JSONException
    {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.length(); i++)
        {
            Object value = array.get(i);
            if(value instanceof JSONArray)
            {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject)
            {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }
}
