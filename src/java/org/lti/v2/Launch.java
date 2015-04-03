package org.lti.v2;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lti.ActionService;
import org.lti.ToolProvider;

public class Launch implements ActionService {
    private static final Logger log = Logger.getLogger(Launch.class);

    Map<String, String> retrieved_params;

    public Launch(ToolProvider tpn) {
        log.info("LTILaunch v2p0");
        log.info("============================================================================================");
        retrieved_params = new LinkedHashMap<String, String>();

        //JSONObject tool_consumer_profile = ToolProvider.doAPICall(tpn.getToolConsumerProfile());
        //log.debug(tool_consumer_profile.toString());

        /*
        //Execute query to the LTI consumer
        try {
            //request the tool consumer profile
            String tc_profile = tpn.getToolConsumerProfile(tpn.getToolConsumerProfile()).toString();

            JSONObject tc_profile_json = new JSONObject(tc_profile);
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
        */

    }

    public String execute(ToolProvider tpn)
            throws Exception {
        log.info("Executing LTILaunch v2p0");
        // TODO Auto-generated method stub
        if( tpn.hasValidSignature() ) log.debug("OAuth signature is valid"); else throw new Exception("OAuth signature is NOT valid");

        return null;
    }

}
