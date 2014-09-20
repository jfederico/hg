package org.hg.engine.test;

import java.util.HashMap;
import java.util.Map;

import org.hg.domain.Type;
import org.hg.engine.CompletionContent;
import org.hg.engine.Engine;
import org.hg.engine.test.CommonCartridgeXML;

public class TestEngine extends Engine {
    public TestEngine(Map<String, String> params, Type config){
        super(params, config);
        if(this.grails_params.get(PARAM_ACT).equals("cc")){
            Map<String, String> definition = new HashMap<String, String>();
            definition.put("title", config.getName());
            definition.put("description", config.getDescription());
            definition.put("launch_url", "");
            definition.put("secure_launch_url", "");
            definition.put("icon", "");
            definition.put("secure_icon", "");
            definition.put("vendor_code", config.getCode());
            definition.put("vendor_name", "");
            definition.put("vendor_description", "");
            definition.put("vendor_url", "");
            definition.put("vendor_contact_email", "");
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
