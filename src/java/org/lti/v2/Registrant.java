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
        log.debug("====== Creating object::Registrant()");
        log.debug(endpoint);
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
    
    public String registerProxy() 
            throws LTIException {
        String proxy_registration_response = "ERROR";

        try {
            validateParameters(LTIv2.TOOL_PROXY_REGISTRATION_REQUEST_PARAMETERS_REQUIRED);
            //request the tool consumer profile
            String tc_profile = requestToolConsumerProfile(params.get(LTIv2.TC_PROFILE_URL));

            JSONObject tc_profile_json = new JSONObject(tc_profile);

            //JSONObject product_instance_json = tc_profile_json.getJSONObject("product_instance");

            JSONArray services_offered_json = tc_profile_json.getJSONArray("service_offered");

            boolean end_outer_for = false;
            for( int i=0; i < services_offered_json.length() && !end_outer_for; i++ ){
                JSONObject service_json = services_offered_json.getJSONObject(i);
                JSONArray formats = service_json.getJSONArray("format");
                for( int j=0; j < formats.length(); j++ ){
                    String format = formats.getString(j);
                    if( "application/vnd.ims.lti.v2.toolproxy+json".equals(format) ){
                        log.debug("Execute call to " + service_json.getString("endpoint"));

                        log.debug("registering Proxy");
                        String regKey = this.params.get(LTIv2.REG_KEY);
                        String regPassword = this.params.get(LTIv2.REG_PASSWORD);
                        JSONObject message = this.tp_profile.getIMSXJSONMessage();
                        log.debug(message.toString());
                        proxy_registration_response = executeProxyRegistration(service_json.getString("endpoint"), regKey, regPassword, message.toString());

                        log.debug(proxy_registration_response);
                        end_outer_for = true;
                        break;
                    }
                }
            }
            log.debug("**************************************************************");

        } catch (Exception e) {
            log.debug("Valio madre, hay un error");
            throw new LTIException(LTIException.MESSAGEKEY_MISSING_PARAMETERS, "LTI version " + LTIv2.VERSION + " parameters not included. " + e.getMessage());
        }
        return proxy_registration_response;
    }

}
