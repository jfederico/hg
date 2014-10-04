package org.hg.engine.test;

import java.util.LinkedHashMap;
import java.util.Map;

import org.hg.engine.CompletionResponse;

public class SingleSignOnURL implements CompletionResponse {

    public Map<String, String> get()
        throws Exception {
        Map<String, String> completionResponse = new LinkedHashMap<String, String>();

        completionResponse.put("type", "url");
        completionResponse.put("content", "http://www.test.com/");

        return completionResponse;
    }

}
