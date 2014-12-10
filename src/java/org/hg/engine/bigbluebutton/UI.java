package org.hg.engine.bigbluebutton;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bigbluebutton.api.BBBCommand;
import org.bigbluebutton.api.BBBException;
import org.bigbluebutton.api.BBBStore;
import org.bigbluebutton.api.BBBProxy;
import org.bigbluebutton.impl.BBBStoreImpl;
import org.bigbluebutton.impl.BBBGetRecordings;
import org.hg.engine.CompletionResponse;

public class UI implements CompletionResponse {
    private static final Logger log = Logger.getLogger(UI.class);

    BBBProxy bbbProxy;
    BBBStore bbbStore = BBBStoreImpl.getInstance();
    Map<String, String> meeting_params;
    Map<String, String> session_params;

    public UI(Map<String, String> engine, Map<String, String> meeting_params, Map<String, String> session_params)
        throws Exception {
        this.bbbProxy = this.bbbStore.createProxy(engine.get("endpoint"), engine.get("secret"));
        this.meeting_params = meeting_params;
        this.session_params = session_params;
    }

    public Map<String, Object> get()
            throws Exception {
        Map<String, Object> completionResponse = new LinkedHashMap<String, Object>();

        completionResponse.put("type", "html");
        completionResponse.put("content", "bigbluebutton_tool_ui");
        completionResponse.put("data", getData());

        return completionResponse;
    }

    private Map<String, Object> getData()
            throws Exception {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        try {
            BBBCommand cmd = new BBBGetRecordings(bbbProxy, meeting_params );
            Map<String, Object> recordings = cmd.execute();
            
            List<Object> recordingList = (List<Object>)recordings.get("recordings");
            for(Object recording: recordingList){
                /// Calculate duration
                Map<String, Object> map = (Map<String, Object>)recording;
                long endTime = Long.parseLong((String)map.get("endTime"));
                endTime -= (endTime % 1000);
                long startTime = Long.parseLong((String)map.get("startTime"));
                startTime -= (startTime % 1000);
                int duration = (int)(endTime - startTime) / 60000;
                /// Add duration
                map.put("duration", duration );
            }

            System.out.println(recordings.toString());
            log.info("Recordings retrieved");
            data = recordings;
        } catch ( BBBException e){
            throw new Exception("Error executing getRecordings", e.getCause());
        }

        data.put("ismoderator", BigBlueButtonEngine.BBB_ROLE_MODERATOR.equals(session_params.get("role")) );
        return data;
    }
}
