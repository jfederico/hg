package org.lti.v1;

import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lti.ValidateRequiredParameters;
import org.lti.LTIException;

public class ValidateRequiredParameters4Launch implements ValidateRequiredParameters {

    private static final Logger log = Logger.getLogger(ValidateRequiredParameters4Launch.class);

    public void execute(Map<String, String> params, JSONArray requiredParameters)
            throws LTIException {
        log.debug("Validating required parameters: " + requiredParameters.toString());

        boolean response = true;
        String missingParams = "";
        for (int i = 0; i < requiredParameters.length(); i++) {
            JSONObject requiredParam = requiredParameters.getJSONObject(i);
            String paramName = requiredParam.getString("name");
            log.debug(paramName);

            if( !params.containsKey(paramName) ){
                missingParams += (missingParams.length()>0)? ", ": "";
                missingParams += paramName;
                response = false;
            }
        }
        if(!response) 
            throw new LTIException("ToolProviderError", "Required Parameters [" + missingParams + "] not included");
        return;
    }

}
