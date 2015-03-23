package org.lti;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.OAuthValidator;
import net.oauth.ParameterStyle;
import net.oauth.SimpleOAuthValidator;
import net.oauth.client.OAuthClient;
import net.oauth.client.URLConnectionClient;
import net.oauth.client.OAuthResponseMessage;
import net.oauth.http.HttpMessage;
import net.oauth.signature.HMAC_SHA1;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ToolProviderNew implements LTI{

    private static final Logger log = Logger.getLogger(ToolProviderNew.class);

    protected Map<String, String> params;
    
    protected static final String[] OAUTH_REQUIERED_PARAMS = { 
        OAuth.OAUTH_CONSUMER_KEY, OAuth.OAUTH_NONCE, OAuth.OAUTH_CALLBACK, OAuth.OAUTH_SIGNATURE, OAuth.OAUTH_SIGNATURE_METHOD, OAuth.OAUTH_VERSION, OAuth.OAUTH_TIMESTAMP
        };

    private static final String VERSION_NUMBER_V1   = "1";
    private static final String VERSION_NUMBER_V2   = "2";
    private static final String VERSION_DEFAULT = VERSION_V1P0;

    protected String endpoint;
    protected String key;
    protected String secret;

    protected String version;

    protected ToolProviderProfile tp_profile;

    protected ActionService actionService;

    public ToolProviderNew(Map<String, String> params, String endpoint, String key, String secret)
            throws LTIException, Exception {
        log.debug("====== Creating object::ToolProviderNew()");
        log.debug(endpoint);
        log.debug(key);
        log.debug(secret);
        log.debug(params);
        this.endpoint = endpoint;
        this.key = key;
        this.secret = secret;
        this.params = new HashMap<String, String>(params);
        this.tp_profile = null;

        this.version = params.containsKey(LTI_VERSION)? params.get(LTI_VERSION): VERSION_DEFAULT;

        try {
            validateParameters(OAUTH_REQUIERED_PARAMS);
            log.debug("OAuth required parameters are included");
        } catch (Exception e) {
            throw new LTIException(LTIException.MESSAGEKEY_MISSING_PARAMETERS, "OAuth required parameters missing. " + e.getMessage());
        }

        if( VERSION_NUMBER_V2.equals(getVersionNumber()) ) {
            if( params.containsKey(LTI_MESSAGE_TYPE) && params.get(LTI_MESSAGE_TYPE).equals(LTI_MESSAGE_TYPE_TOOL_PROXY_REGISTRATION_REQUEST) ) {
                try {
                    validateParameters(LTI_V2_TOOL_PROXY_REGISTRATION_REQUEST_PARAMETERS_REQUIRED);
                    log.debug("LTI required parameters are included");
                } catch (Exception e) {
                    throw new LTIException(LTIException.MESSAGEKEY_MISSING_PARAMETERS, "LTI version " + this.version + " parameters not included. " + e.getMessage());
                }

                log.debug("Instantiating service LTI-2p0 for Registration");
                this.actionService = new org.lti.v2.Registration();

            } else {
                try {
                    validateParameters(LTI_V2_LAUNCH_REQUEST_PARAMETERS_REQUIRED);
                    log.debug("LTI required parameters are included");
                } catch (Exception e) {
                    throw new LTIException(LTIException.MESSAGEKEY_MISSING_PARAMETERS, "LTI version " + this.version + " parameters not included. " + e.getMessage());
                }

                log.debug("Instantiating service LTI-2p0 for Launch");
                this.actionService = new org.lti.v2.Launch();

                executeActionService();
            }

        } else {
            try {
                validateParameters(LTI_V1_LAUNCH_REQUEST_PARAMETERS_REQUIRED);
                log.debug("LTI required parameters are included");
            } catch (Exception e) {
                throw new LTIException(LTIException.MESSAGEKEY_MISSING_PARAMETERS, "LTI version " + this.version + " parameters not included. " + e.getMessage());
            }

            log.debug("Instantiating service LTI-1p0 for Launch");
            this.actionService = new org.lti.v1.Launch();

            // Not requires for LTI 1.x launches
            //executeActionService();
        }

        if( hasValidSignature() ) log.debug("OAuth signature is valid"); else throw new Exception("OAuth signature is NOT valid");
    }

    public String executeActionService()
            throws Exception {
        return this.actionService.execute(this);
    }

    private String getVersionNumber() {
        String versionNumber = VERSION_NUMBER_V1;

        String[] versionA = this.version.split("-"); 
        String[] versionB = versionA[1].split("p");
        versionNumber = versionB[0];

        return versionNumber;
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

    public void setToolProviderProfile(ToolProviderProfile tp_profile) {
        this.tp_profile = tp_profile;
    }

    public ToolProviderProfile getToolProviderProfile() {
        return this.tp_profile;
    }

    public String getLTIVersion() {
        return this.version;
    }

    public String getLTILaunchPresentationReturnURL() {
        return this.params.get(LAUNCH_PRESENTATION_RETURN_URL);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public JSONObject getToolConsumerProfile(String query) {
        JSONObject toolConsumerProfile = ltiProxyRequest(query);
        return toolConsumerProfile;
    }

    public Map<String, String> doRequestLti(String url, String regKey, String regPassword, String message) {
        //throws Exception {
        log.debug("Executing the doRequestLti");
        Map<String, String> returnValues = new LinkedHashMap<String, String>();

        OAuthAccessor acc;
        OAuthValidator oav;
        OAuthMessage oam;
        OAuthConsumer cons;
        OAuthClient oac;
        OAuthResponseMessage oar;

        log.debug("//2.- Prepare the message");
        String imsx_JSONRequest = message;
        log.debug("imsx_JSONRequest:\n" + imsx_JSONRequest);
        try {
            log.debug("//3.- Sign the message");
            oav = new SimpleOAuthValidator();
            cons = new OAuthConsumer("about:blank", regKey, regPassword, null);
            acc = new OAuthAccessor(cons);

            InputStream bodyAsStream = new ByteArrayInputStream(imsx_JSONRequest.getBytes());

            oam = new OAuthMessage("POST", url, null, bodyAsStream, "application/vnd.ims.lti.v2.toolproxy+json");
            oam.addRequiredParameters(acc);

            oav.validateMessage(oam,acc);

            log.debug("//4.- Send the message");
            oac = new OAuthClient(new URLConnectionClient());
            oar = oac.access(oam, ParameterStyle.AUTHORIZATION_HEADER);
            int responseCode = oar.getHttpResponse().getStatusCode();
            log.debug("//4.1 Validating the response...");
            Map<String, Object> thedump;
            if (responseCode == 200) {
                /* Parsing the InputStream for debugging purposes only */
                String responseAsString = getResponse(oar.getBodyAsStream());
                log.debug("Response:\n" + responseAsString);
            } else if (responseCode == 201) {
                /* Parsing the InputStream for debugging purposes only */
                String responseAsString = getResponse(oar.getBodyAsStream());
                log.debug("Response:\n" + responseAsString);
            } else if (responseCode == 202) {
                /* Parsing the InputStream for debugging purposes only */
                String responseAsString = getResponse(oar.getBodyAsStream());
                log.debug("Response:\n" + responseAsString);
            } else if (responseCode == 404) {
                //The resource was not found, the record must be deleted from the epcs server
                thedump = oar.getDump();
                log.debug("\nREQUEST=" + thedump.get(HttpMessage.REQUEST) + "\nRESPONSE=" + thedump.get(HttpMessage.RESPONSE));
                //throw new Exception("Error 404");
            } else {
                thedump = oar.getDump();
                log.debug("\nREQUEST\n" + thedump.get(HttpMessage.REQUEST) + "\nRESPONSE\n" + thedump.get(HttpMessage.RESPONSE));
                //throw new Exception("Error");
            }
        } catch(IOException e) {
            log.debug(e.toString());
            log.debug(e.getCause().toString());
        } catch(OAuthException e) {
            log.debug(e.toString());
            log.debug(e.getCause().toString());
        } catch(URISyntaxException e) {
            log.debug(e.toString());
            log.debug(e.getCause().toString());
        } catch(Exception e) {
            log.debug(e.toString());
            log.debug(e.getCause().toString());
        }

        return returnValues;
    }

    public String executeProxyRegistration(String url, String regKey, String regPassword, String message) {
        String response;
        try{
            doRequestLti(url, regKey, regPassword, message);
            response = "OK";
        } catch (Exception e){
            log.error("Error while executing the post for registration");
            response = "ERROR: " + e.getMessage();
        }

        return response;
    }

    private String getResponse(InputStream inputStream)
            throws IOException{
        InputStreamReader isr = null;
        BufferedReader reader = null;
        StringBuilder responseAsString = new StringBuilder();
        try {
            isr = new InputStreamReader(inputStream, "UTF-8");
            reader = new BufferedReader(isr);
            String line = reader.readLine();
            while (line != null) {
                responseAsString.append(line.trim());
                line = reader.readLine();
            }
        } finally {
            if (reader != null)
                reader.close();
        }

        return responseAsString.toString();
    }

    /** Make an API call */
    private JSONObject ltiProxyRequest(String query) {
        return ltiProxyRequest(query, "GET");
    }

    private JSONObject ltiProxyRequest(String query, String requestMethod) {
        JSONObject ltiProxyResponse = null;
        StringBuilder urlStr = new StringBuilder(query);

        try {
            // open connection
            log.debug("doLTICall.call: " + query );

            URL url = new URL(urlStr.toString());
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setUseCaches(false);
            httpConnection.setDoOutput(true);
            httpConnection.setRequestMethod(requestMethod);
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
                log.debug("ltiProxyRequest.HTTPERROR: Message=" + "Tool consumer responded with HTTP status code " + responseCode);
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

    public Map<String, Object> jsonToMap(JSONObject json)
            throws JSONException {
        Map<String, Object> retMap = new HashMap<String, Object>();

        if(json != JSONObject.NULL)
        {
            retMap = toMap(json);
        }
        return retMap;
    }

    public Map<String, Object> toMap(JSONObject object)
            throws JSONException {
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

    public List<Object> toList(JSONArray array)
            throws JSONException {
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
