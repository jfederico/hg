package org.hg.engine.bigbluebutton;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bigbluebutton.api.BBBCommand;
import org.bigbluebutton.api.BBBException;
import org.bigbluebutton.api.BBBStore;
import org.bigbluebutton.api.BBBProxy;
import org.bigbluebutton.impl.BBBStoreImpl;
import org.bigbluebutton.impl.BBBCreateMeeting;
import org.hg.engine.CompletionResponse;

public class SingleSignOnURL implements CompletionResponse {
    private static final Logger log = Logger.getLogger(SingleSignOnURL.class);

    BBBProxy bbbProxy;
    BBBStore bbbStore = BBBStoreImpl.getInstance();
    Map<String, String> meeting_params;
    Map<String, String> session_params;
    
    public SingleSignOnURL(Map<String, String> engine, Map<String, String> meeting_params, Map<String, String> session_params)
        throws Exception {
        this.bbbProxy = this.bbbStore.createProxy(engine.get("endpoint"), engine.get("secret"));
        this.meeting_params = meeting_params;
        this.session_params = session_params;
    }

    public Map<String, Object> get()
        throws Exception{
        Map<String, Object> completionResponse = new LinkedHashMap<String, Object>();

        completionResponse.put("type", "url");

        String ssoURL = null;
        try {
            BBBCommand cmd = new BBBCreateMeeting(bbbProxy, meeting_params );
            cmd.execute();
            log.info("Meeting created");
            ssoURL = bbbProxy.getJoinURL(session_params);
            log.info("Joining [" + ssoURL + "]");
        } catch ( BBBException e){
            throw new Exception("Error executing SSO", e.getCause());
        }
        completionResponse.put("content", ssoURL);
        completionResponse.put("data", new LinkedHashMap<String, Object>());

        return completionResponse;
    }

}
