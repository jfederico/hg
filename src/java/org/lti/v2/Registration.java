package org.lti.v2;

import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lti.ActionService;
import org.lti.LTIException;
import org.lti.LTI;
import org.lti.ToolProvider;
import org.lti.ToolProviderProfile;

public class Registration implements ActionService {
    private static final Logger log = Logger.getLogger(Registration.class);

    public String execute(ToolProvider tpn)
            throws Exception {
        log.info("LTIRegistration v2p0");
        String proxy_registration_response = "ERROR";

        Map<String, String> params = tpn.getParameters();
        ToolProviderProfile tp_profile = tpn.getToolProviderProfile();

        try {
            //request the tool consumer profile
            String tc_profile = tpn.getToolConsumerProfile(params.get(LTI.TC_PROFILE_URL)).toString();

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

                        log.debug("Registering Proxy");
                        String regKey = params.get(LTI.REG_KEY);
                        String regPassword = params.get(LTI.REG_PASSWORD);
                        JSONObject message = tp_profile.getIMSXJSONMessage();
                        proxy_registration_response = tpn.executeProxyRegistration(service_json.getString("endpoint"), regKey, regPassword, message.toString());

                        log.debug("Proxy registration response:");
                        log.debug(proxy_registration_response);
                        end_outer_for = true;
                        break;
                    }
                }
            }

        } catch (Exception e) {
            log.debug("Valio madre, hay un error");
            throw new LTIException(LTIException.MESSAGEKEY_MISSING_PARAMETERS, "LTI version " + tpn.getLTIVersion() + " parameters not included. " + e.getMessage());
        }

        return proxy_registration_response;
    }
}
