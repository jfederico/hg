package org.hg.engine.bigbluebutton;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bigbluebutton.api.BBBCommand;
import org.bigbluebutton.api.BBBException;
import org.bigbluebutton.api.BBBStore;
import org.bigbluebutton.api.BBBProxy;
import org.bigbluebutton.impl.BBBProxyImpl;
import org.bigbluebutton.impl.BBBStoreImpl;
import org.bigbluebutton.impl.BBBCreateMeeting;
import org.hg.engine.CompletionResponse;

public class SingleSignOnURL implements CompletionResponse {
    private static final Logger log = Logger.getLogger(SingleSignOnURL.class);

    BBBStore bbbStore;
    BBBProxy bbbProxy;
    Map<String, String> meeting_params;
    Map<String, String> session_params;

    public SingleSignOnURL(Map<String, String> engine, Map<String, String> meeting_params, Map<String, String> session_params)
        throws Exception {
        log.debug("====== Creating object::SingleSignOnURL()");
        this.bbbStore = BBBStoreImpl.getInstance();
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
            BBBCommand create_meeting = new BBBCreateMeeting(this.bbbProxy, this.meeting_params );
            log.debug("Creating meeting");
            create_meeting.execute();
            log.debug("Meeting created");
            ssoURL = this.bbbProxy.getJoinURL(session_params);
            log.info("Joining [" + ssoURL + "]");
        } catch ( BBBException e){
            log.error("Error executing SSO: " + e.getMessage());
            throw new Exception("Error executing SSO: " + e.getMessage(), e.getCause());
        } catch ( Exception e){
            log.error("Error executing SSO: " + e.getMessage());
            throw new Exception("Error executing SSO: " + e.getMessage(), e.getCause());
        }
        completionResponse.put("content", ssoURL);
        completionResponse.put("data", new LinkedHashMap<String, Object>());

        return completionResponse;
    }

}
