package org.hg.engine.bigbluebutton;

import java.util.HashMap;
import java.util.Map;

//import org.json.JSONArray;
import org.json.JSONObject;
//import org.json.JSONException;

import org.hg.engine.CompletionResponse;

public class UI implements CompletionResponse {

    public UI(){
        
    }

    public Map<String, String> get() throws Exception {
        Map<String, String> completionResponse = new HashMap<String, String>();

        completionResponse.put("type", "html");
        completionResponse.put("content", "bigbluebutton_tool_ui");
        completionResponse.put("data", getData().toString());

        return completionResponse;
    }

    private JSONObject getData() {
        JSONObject data = new JSONObject();
        data.put("key", "value");
        return data;
    }
}
