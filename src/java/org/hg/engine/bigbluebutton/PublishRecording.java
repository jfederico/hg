package org.hg.engine.bigbluebutton;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bigbluebutton.api.BBBCommand;
import org.bigbluebutton.api.BBBException;
import org.bigbluebutton.api.BBBStore;
import org.bigbluebutton.api.BBBProxy;
import org.bigbluebutton.impl.BBBStoreImpl;
import org.bigbluebutton.impl.BBBPublishRecordings;
import org.hg.engine.CompletionResponse;

public class PublishRecording implements CompletionResponse {
    private static final Logger log = Logger.getLogger(PublishRecording.class);

    BBBProxy bbbProxy;
    BBBStore bbbStore = BBBStoreImpl.getInstance();
    Map<String, String> meeting_params;
    Map<String, String> session_params;
    
    public PublishRecording(Map<String, String> engine, Map<String, String> meeting_params, Map<String, String> session_params)
        throws Exception {
        this.bbbProxy = this.bbbStore.createProxy(engine.get("endpoint"), engine.get("secret"));
        this.meeting_params = meeting_params;
        this.session_params = session_params;
    }

    public Map<String, Object> get()
        throws Exception{
        Map<String, Object> completionResponse = new LinkedHashMap<String, Object>();

        completionResponse.put("type", "url");

        try{
            BBBCommand cmd = new BBBPublishRecordings(bbbProxy, meeting_params );
            cmd.execute();
            log.info("Recording published");

            completionResponse.put("type", "html");
            completionResponse.put("content", "bigbluebutton_tool_ui");
            completionResponse.put("data", getData().toString());
        } catch ( BBBException e){
            throw new Exception("Error executing publishRecording", e.getCause());
        }

        return completionResponse;
    }

    private Map<String, Object> getData() {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("key", "value");
        return data;
    }

}
