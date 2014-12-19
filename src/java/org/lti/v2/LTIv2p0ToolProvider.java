package org.lti.v2;

import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lti.LTIException;
import org.lti.LTIToolProvider;
import org.lti.LTIv2;
import org.lti.v1.LTIv1p0ToolProvider;

public class LTIv2p0ToolProvider extends LTIToolProvider implements LTIv2 {

    private static final Logger log = Logger.getLogger(LTIv1p0ToolProvider.class);

    public LTIv2p0ToolProvider(String endpoint, String key, String secret,
            Map<String, String> params) throws LTIException, Exception {
        super(endpoint, key, secret, params);
    }

    public String getLTIVersion(){
        return LTIv2.VERSION;
    }

    @Override
    public void validateRequiredParameters(JSONArray requiredParameters)
            throws LTIException, Exception {
        log.debug("Validating required parameters");
        this.validateRequiredParameters.execute(this.params, requiredParameters);
    }

    @Override
    public boolean hasRequiredParameters(JSONArray requiredParameters)
            throws LTIException, Exception {
        boolean response = true;
        String missingParams = "";
        for (int i = 0; i < requiredParameters.length(); i++) {
            JSONObject requiredParam = requiredParameters.getJSONObject(i);
            String paramName = requiredParam.getString("name");

            if( !params.containsKey(paramName) ){
                missingParams += (missingParams.length()>0)? ", ": "";
                missingParams += paramName;
                response = false;
            }
        }
        if(!response) throw new LTIException("ToolProviderError", "Required Parameters [" + missingParams + "] not included");
        else return response;
    }

    @Override
    public void overrideParameters(JSONArray overrides) throws Exception {
        JSONObject override;
        String source;
        String target;

        for (int i = 0; i < overrides.length(); i++) {
            override = overrides.getJSONObject(i);
            source = override.getString("source");
            target = override.getString("target");
            if( params.containsKey(target) ){
                params.put(source, params.get(target));
            }
        }
        
    }

    @Override
    public Map<String, String> getParameters(){
        return params;
    }
    
    @Override
    public String getParameter(String key){
        return params.get(key);
    }

    @Override
    public void putParameter(String key, String value){
        params.put(key, value);
    }
    
    @Override
    public boolean hasParameter(String key){
        return params.containsKey(key);
    }

    @Override
    public boolean isToolConsumerInfoProductFamilyCode(String code) {
        return this.params.get(TOOL_CONSUMER_INFO_PRODUCT_FAMILY_CODE).equals(code);
    }
}
