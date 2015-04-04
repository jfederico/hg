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

        log.debug(tpn.getToolConsumerProfile());
        JSONObject tool_consumer_profile = ToolProvider.doAPICall(tpn.getToolConsumerProfile(), "GET");
        log.debug(tool_consumer_profile.toString());

        String endpoint;
        JSONArray services_offered = tool_consumer_profile.getJSONArray("service_offered");
        for( int i=0; i < services_offered.length(); i++ ) {
            JSONObject service_offered = services_offered.getJSONObject(i);
            String id = service_offered.getString("@id");
            if( id.equals("tcp:ToolProxy.collection") ) {
                endpoint = service_offered.getString("endpoint") + "?lti_version=LTI-2p0";
                log.debug(endpoint);
                //JSONObject tool_proxy_collection = ToolProvider.doAPICall(endpoint, "POST");
                //log.debug(tool_proxy_collection.toString());
            } else if( id.equals("tcp:ToolProxySettings") ) {
                endpoint = service_offered.getString("endpoint") + "?lti_version=LTI-2p0";
                log.debug(endpoint);
                //Replace {tool_proxy_id}
                //log.debug(tpn.getToolConsumerKey());
                //endpoint = endpoint.replace("{tool_proxy_id}", tpn.getToolConsumerKey());
                //log.debug(endpoint);
                //JSONObject tool_proxy_settings = ToolProvider.doAPICall(endpoint, "GET");
                //log.debug(tool_proxy_settings.toString());
            }
        }

    }

    public String execute(ToolProvider tpn)
            throws Exception {
        log.info("Executing LTILaunch v2p0");
        // TODO Auto-generated method stub
        if( tpn.hasValidSignature() ) log.debug("OAuth signature is valid"); else throw new Exception("OAuth signature is NOT valid");

        return null;
    }

}
