package org.lti.v2;

import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;
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
            log.debug(tc_profile);
            
            JSONObject tc_profile_json = new JSONObject(tc_profile);
            
            
            

        } catch (Exception e) {
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
    
    protected String requestToolConsumerProfile2(String query) 
            throws LTIException{
            String response = "";
            response = getToolConsumerProfile(query).toString();
            return response;
    }


}
