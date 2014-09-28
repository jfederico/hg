package org.hg.engine.bigbluebutton;

import java.util.LinkedHashMap;
import java.util.Map;

import org.hg.engine.CompletionResponse;

public class SingleSignOnURL implements CompletionResponse {

    public Map<String, String> get() {
        Map<String, String> completionResponse = new LinkedHashMap<String, String>();

        completionResponse.put("type", "url");
        completionResponse.put("content", "http://www.bigbluebutton.org/");

        return completionResponse;
    }

}
