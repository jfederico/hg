package org.hg.engine.bigbluebutton;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bigbluebutton.api.BBBCommand;
import org.bigbluebutton.api.BBBException;
import org.bigbluebutton.impl.BBBDeleteRecordings;

public class DeleteRecording extends UI  {
    private static final Logger log = Logger.getLogger(DeleteRecording.class);

    Map<String, String> recording_params;

    public DeleteRecording(Map<String, String> engine, Map<String, String> meeting_params, Map<String, String> session_params, Map<String, String> recording_params)
        throws Exception {
        super(engine, meeting_params, session_params);
        this.recording_params = recording_params;
    }

    public Map<String, Object> get()
        throws Exception{
        Map<String, Object> completionResponse = new LinkedHashMap<String, Object>();

        completionResponse.put("type", "url");

        try{
            BBBCommand cmd = new BBBDeleteRecordings(bbbProxy, this.recording_params );
            cmd.execute();
            log.info("Recording deleted");
        } catch ( BBBException e){
            throw new Exception("Error executing deleteRecording", e.getCause());
        }

        completionResponse.put("type", "html");
        completionResponse.put("content", "bigbluebutton_tool_ui");
        completionResponse.put("data", getData());

        return completionResponse;
    }
}
