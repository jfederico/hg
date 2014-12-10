package org.hg.engine.test;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.hg.EngineFactory;
import org.hg.engine.CompletionResponse;
import org.hg.engine.Engine;

public class TestEngine extends Engine {
    private static final Logger log = Logger.getLogger(TestEngine.class);

    public static final String ENGINE_CODE = EngineFactory.ENGINE_TEST;
    public static final String ENGINE_NAME = "BigBlueButton";
    public static final String ENGINE_DESCRIPTION = "Open source web conferencing system for distance learning.";
    public static final String ENGINE_URL = "http://www.bigbluebutton.org/";
    public static final String ENGINE_CONTACT_EMAIL = "bigbluebutton-users@googlegroups.com";

    public TestEngine(HttpServletRequest request, Map<String, String> params, Map<String, Object> config, String endpoint, Map<String, String> session_params)
        throws Exception {
        super(request, params, config, endpoint, session_params);

        log.debug("instantiate TestEngine()");
        if(this.grails_params.get(PARAM_ACT).equals(ENGINE_ACT_CC)){
            Map<String, String> definition = new HashMap<String, String>();
            definition.put("title", (String)config.get("title"));
            definition.put("description", (String)config.get("description"));

            String launch_url_path = grails_params.get("application") + "/" + grails_params.get("tenant") + "/" + ENGINE_TYPE_LAUNCH + "/" + grails_params.get("version"); 
            definition.put( "launch_url", "http://" + endpoint + "/" + launch_url_path );
            definition.put( "secure_launch_url", "https://" + endpoint + "/" + launch_url_path );

            String icon_path = grails_params.get("application") + "/" + grails_params.get("tenant") + "/" + ENGINE_TYPE_RESOURCE + "/v1/?a=ico";
            definition.put( "icon", "http://" + endpoint + "/" + icon_path );
            definition.put( "secure_icon", "https://" + endpoint + "/" + icon_path );

            @SuppressWarnings("unchecked")
            Map<String, Object> vendor = (Map<String, Object>)config.get("vendor");
            String vendor_code = (String)vendor.get("code");
            definition.put( "vendor_code", (vendor_code != null && !vendor_code.equals(""))? vendor_code: ENGINE_CODE );
            String vendor_name = (String)vendor.get("name");
            definition.put( "vendor_name", (vendor_name != null && !vendor_name.equals(""))? vendor_name: ENGINE_NAME );
            String vendor_description = (String)vendor.get("description");
            definition.put( "vendor_description", (vendor_description != null && !vendor_description.equals(""))? vendor_description: ENGINE_DESCRIPTION );
            String vendor_url = (String)vendor.get("url");
            definition.put( "vendor_url", (vendor_url != null && !vendor_url.equals(""))? vendor_url: ENGINE_URL );
            String vendor_contact_email = (String)vendor.get("contact");
            definition.put( "vendor_contact_email", (vendor_contact_email != null && !vendor_contact_email.equals(""))? vendor_contact_email: ENGINE_CONTACT_EMAIL );

            setCompletionResponseCommand( new CommonCartridgeXML(definition) );
        } else {
            setCompletionResponseCommand( new SingleSignOnURL() );
        }
    }

    @Override
    public Map<String, Object> getCompletionResponse()
        throws Exception {
        return completionResponse.get();
    }

    @Override
    public void setCompletionResponseCommand(CompletionResponse completionResponse) {
        this.completionResponse = completionResponse;
    }
}
