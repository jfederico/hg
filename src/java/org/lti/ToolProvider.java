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
import java.util.Date;
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

public abstract class ToolProvider {

    private static final Logger log = Logger.getLogger(ToolProvider.class);

    protected Map<String, String> params;
    
    protected static final String[] OAUTH_REQUIERED_PARAMS = { 
        OAuth.OAUTH_CONSUMER_KEY, OAuth.OAUTH_NONCE, OAuth.OAUTH_CALLBACK, OAuth.OAUTH_SIGNATURE, OAuth.OAUTH_SIGNATURE_METHOD, OAuth.OAUTH_VERSION, OAuth.OAUTH_TIMESTAMP
        };

    protected String endpoint;
    protected String key;
    protected String secret;

    protected ToolProviderProfile tp_profile;

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

    public void setToolProviderProfile(ToolProviderProfile tp_profile) {
        this.tp_profile = tp_profile;
    }

    public abstract String getLTIVersion();
    public abstract String getLTILaunchPresentationReturnURL();
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public JSONObject getToolConsumerProfile(String query) {
        JSONObject toolConsumerProfile = ltiProxyRequest(query);
        return toolConsumerProfile;
    }

    private JSONObject getIMSXJSONRequest() {
        JSONObject imsx_JSONRequest = new JSONObject();

        JSONObject product_instance = new JSONObject();
        product_instance.put("guid", "192.168.44.149");
            JSONObject product_info = new JSONObject();
            product_info.put("product_version", "1.0.0");
                JSONObject product_family = new JSONObject();
                    JSONObject vendor = new JSONObject();
                    Date dt = new Date();
                    vendor.put("timestamp", "" + dt.getTime());
                    vendor.put("code", "hg");
                        JSONObject vendor_name = new JSONObject();
                        vendor_name.put("default_value", "123it.ca");
                        vendor_name.put("key", "product.vendor.name");
                        vendor.put("vendor_name", vendor_name);
                product_family.put("vendor", vendor);
                product_family.put("code", "hg_bigbluebutton");
            product_info.put("product_family", product_family);
                JSONObject product_name = new JSONObject();
                product_name.put("default_value", "hg");
                product_name.put("key", "product.name");
        product_instance.put("support", "{}");
        product_instance.put("service_provider", "{}");
        product_instance.put("service_owner", "{}");
            JSONObject service_offered = new JSONObject();
        product_instance.put("service_offered", service_offered);

        imsx_JSONRequest.put("lti_version", "LTI-2p0");
        imsx_JSONRequest.put("product_instance", product_instance);
        return imsx_JSONRequest;
    }

    public Map<String, String> doRequestLti(String url, String regKey, String regPassword){
        log.debug("Executing the doRequestLti");
        Map<String, String> returnValues = new LinkedHashMap<String, String>();

        OAuthAccessor acc;
        OAuthValidator oav;
        OAuthMessage oam;
        OAuthConsumer cons;
        OAuthClient oac;
        OAuthResponseMessage oar;

        log.debug("//2.- Prepare the message");
        String imsx_JSONRequest = getIMSXJSONRequest().toString();
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
            } else {
                thedump = oar.getDump();
                log.debug("\nREQUEST\n" + thedump.get(HttpMessage.REQUEST) + "\nRESPONSE\n" + thedump.get(HttpMessage.RESPONSE));
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

    public String executeProxyRegistration(String url, String regKey, String regPassword) {
        try{
            doRequestLti(url, regKey, regPassword);
        } catch (Exception e){
            log.error("Error while executing the post for registration");
        }
        return "OK";
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

/*
    private void sendPost(String url, String urlParameters) throws Exception {

        String USER_AGENT = "Mozilla/5.0";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream()) );
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());

    }
 */

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
