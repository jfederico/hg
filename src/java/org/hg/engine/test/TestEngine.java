package org.hg.engine.test;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hg.engine.CompletionResponse;
import org.hg.engine.Engine;
import org.hg.engine.test.CommonCartridgeXML;

public class TestEngine extends Engine {
    private static final Logger log = Logger.getLogger(TestEngine.class);

    public TestEngine(Map<String, String> params, Map<String, Object> config, String endpoint){
        super(params, config, endpoint);
        log.debug("instantiate TestEngine()");
        if(this.grails_params.get(PARAM_ACT).equals("cc")){
            Map<String, String> definition = new HashMap<String, String>();
            definition.put("title", (String)config.get("title"));
            definition.put("description", (String)config.get("description"));
            
            String url_path = grails_params.get("application") + "/" + grails_params.get("tenant") + "/lti/" + grails_params.get("version"); 
            definition.put("launch_url", "http://" + endpoint + "/" + url_path );
            definition.put("secure_launch_url", "https://" + endpoint + "/" + url_path );
            definition.put("icon", "http://" + endpoint + "/" );
            definition.put("secure_icon", "https://" + endpoint + "/" );
            Map<String, Object> vendor = (Map<String, Object>)config.get("vendor");
            definition.put("vendor_code", (String)vendor.get("code"));
            definition.put("vendor_name", (String)vendor.get("name"));
            definition.put("vendor_description", (String)vendor.get("description"));
            definition.put("vendor_url", (String)vendor.get("url"));
            definition.put("vendor_contact_email", (String)vendor.get("contact"));
            setCompletionResponseCommand(new CommonCartridgeXML(definition));
        } else {
            setCompletionResponseCommand(new SingleSignOnURL());
        }
    }

    @Override
    public Map<String, String> getCompletionResponse() {
        return completionResponse.get();
    }

    @Override
    public void setCompletionResponseCommand(CompletionResponse completionResponse) {
        this.completionResponse = completionResponse;
    }
}
