package org.hg.engine.bigbluebutton;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hg.engine.CompletionResponse;
import org.lti.ToolProvider;

public class RegistrationURL implements CompletionResponse {
    private static final Logger log = Logger.getLogger(RegistrationURL.class);

    ToolProvider tpn;

    public RegistrationURL(ToolProvider tpn)
        throws Exception {
        log.debug("====== Creating object::RegistrationURL()");
        this.tpn = tpn;
    }

    public Map<String, Object> get()
        throws Exception{
        Map<String, Object> completionResponse = new LinkedHashMap<String, Object>();

        completionResponse.put("type", "url");

        String registrationURL = null;
        try {
            registrationURL = this.tpn.getLTILaunchPresentationReturnURL();
        } catch ( Exception e){
            throw new Exception("Error after registration", e.getCause());
        }
        completionResponse.put("content", registrationURL);
        completionResponse.put("data", new LinkedHashMap<String, Object>());

        return completionResponse;
    }

}
