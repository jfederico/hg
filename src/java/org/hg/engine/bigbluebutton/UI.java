package org.hg.engine.bigbluebutton;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bigbluebutton.api.BBBCommand;
import org.bigbluebutton.api.BBBException;
import org.bigbluebutton.api.BBBStore;
import org.bigbluebutton.api.BBBProxy;
import org.bigbluebutton.impl.BBBStoreImpl;
import org.bigbluebutton.impl.BBBGetRecording;
import org.hg.engine.CompletionResponse;

//import org.json.JSONArray;
import org.json.JSONObject;
//import org.json.JSONException;

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

    public Map<String, String> get()
            throws Exception {
        Map<String, String> completionResponse = new HashMap<String, String>();

        completionResponse.put("type", "html");
        completionResponse.put("content", "bigbluebutton_tool_ui");
        completionResponse.put("data", getData().toString());

        return completionResponse;
    }

    private JSONObject getData()
            throws Exception {
        JSONObject data = new JSONObject();
        try {
            BBBCommand cmd = new BBBGetRecording(bbbProxy, meeting_params );
            cmd.execute();
            log.info("Recordings retrieved");
        } catch ( BBBException e){
            throw new Exception("Error executing getRecordings", e.getCause());
        }

        data.put("key", "value");
        return data;
    }
}
