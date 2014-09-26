package org.hg.engine.test;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hg.engine.CompletionContent;
import org.hg.engine.Engine;
import org.hg.engine.test.CommonCartridgeXML;

public class TestEngine extends Engine {
    private static final Logger log = Logger.getLogger(TestEngine.class);

    public TestEngine(Map<String, String> params, Map<String, Object> config){
        super(params, config);
        log.debug("instantiate TestEngine()");
        if(this.grails_params.get(PARAM_ACT).equals("cc")){
            Map<String, String> definition = new HashMap<String, String>();
            definition.put("title", (String)config.get("title"));
            definition.put("description", (String)config.get("description"));
            definition.put("launch_url", "");
            definition.put("secure_launch_url", "");
            definition.put("icon", "");
            definition.put("secure_icon", "");
            Map<String, Object> vendor = (Map<String, Object>)config.get("vendor");
            definition.put("vendor_code", (String)vendor.get("code"));
            definition.put("vendor_name", (String)vendor.get("name"));
            definition.put("vendor_description", (String)vendor.get("description"));
            definition.put("vendor_url", (String)vendor.get("url"));
            definition.put("vendor_contact_email", (String)vendor.get("contact"));
            setCompletionContentCommand(new CommonCartridgeXML(definition));
        } else {
            setCompletionContentCommand(new SingleSignOnURL());
        }
    }

    @Override
    public Map<String, String> getCompletionContent() {
        return completionContent.get();
    }

    @Override
    public void setCompletionContentCommand(CompletionContent completionContent) {
        this.completionContent = completionContent;
    }
}
