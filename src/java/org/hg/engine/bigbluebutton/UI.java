package org.hg.engine.bigbluebutton;

import java.util.HashMap;
import java.util.Map;

import org.hg.engine.CompletionResponse;

public class UI implements CompletionResponse {


    public UI(){
        
    }

    public Map<String, String> get() throws Exception {
        Map<String, String> completionResponse = new HashMap<String, String>();

        completionResponse.put("type", "html");
        completionResponse.put("content", "tool_ui");
        completionResponse.put("data", "");

        return completionResponse;
    }

}
