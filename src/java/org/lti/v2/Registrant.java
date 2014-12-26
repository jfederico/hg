package org.lti.v2;

import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONArray;
import org.lti.LTIException;
import org.lti.ToolProvider;
import org.lti.LTIv2;

public class Registrant extends ToolProvider implements LTIv2 {

    private static final Logger log = Logger.getLogger(Registrant.class);

    public Registrant(String endpoint, String key, String secret, Map<String, String> params)
            throws LTIException, Exception {
        super(endpoint, key, secret, params);

        try {
            validateParameters(LTIv2.TOOL_PROXY_REGISTRATION_REQUEST_PARAMETERS_REQUIRED);
            //request the tool consumer profile
            String tc_profile = requestToolConsumerProfile(params.get(LTIv2.TC_PROFILE_URL));
            log.debug("************************");
            log.debug(tc_profile);

            JSONObject tc_profile_json = new JSONObject(tc_profile);
            log.debug("************************");
            log.debug(tc_profile_json);

            JSONObject product_instance_json = tc_profile_json.getJSONObject("product_instance");
            log.debug("************************");
            log.debug(product_instance_json);

            JSONArray services_offered_json = tc_profile_json.getJSONArray("service_offered");
            log.debug("************************");
            log.debug(services_offered_json);

            for( int i=0; i < services_offered_json.length(); i++ ){
                JSONObject service_json = services_offered_json.getJSONObject(i);
                log.debug("************************ service_json");
                log.debug(service_json);
                JSONArray formats = service_json.getJSONArray("format");
                for( int j=0; j < formats.length(); j++ ){
                    String format = formats.getString(j);
                    log.debug(format);
                    if( "application/vnd.ims.lti.v2.toolproxy+json".equals(format) ){
                        log.debug("Execute call to " + service_json.getString("endpoint"));
                        String proxy_registration_response = registerProxy(service_json.getString("endpoint"), params.get(LTIv2.REG_KEY), params.get(LTIv2.REG_PASSWORD));
                        log.debug("************************ proxy_registration_response");
                        log.debug(proxy_registration_response);
                        break;
                    }
                }
            }
            log.debug("------------------------------------------------------------------------");

        } catch (Exception e) {
            log.debug("Valio madre, hay un error");
            throw new LTIException(LTIException.MESSAGEKEY_MISSING_PARAMETERS, "LTI version " + LTIv2.VERSION + " parameters not included. " + e.getMessage());
        }
    }

    public String getLTIVersion(){
        return LTIv2.VERSION;
    }

    public String getLTILaunchPresentationReturnURL(){
        return this.params.get(LAUNCH_PRESENTATION_RETURN_URL);
    }

    protected String requestToolConsumerProfile(String query) 
        throws LTIException{
        String response = "";
        response = getToolConsumerProfile(query).toString();
        return response;
    }
    
    protected String registerProxy(String query, String regKey, String regPassword) 
            throws LTIException{
        log.debug("registering Proxy");
        String response = "";
        response = executeProxyRegistration(query, regKey, regPassword);
        return response;
    }


}
