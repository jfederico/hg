package org.hg.engine.test;

import java.util.LinkedHashMap;
import java.util.Map;

import org.hg.engine.CompletionResponse;

public class SingleSignOnURL implements CompletionResponse {

    public Map<String, Object> get()
        throws Exception {
        Map<String, Object> completionResponse = new LinkedHashMap<String, Object>();

        completionResponse.put("type", "url");
        completionResponse.put("content", "http://www.test.com/");
        completionResponse.put("data", new LinkedHashMap<String, Object>());

        return completionResponse;
    }

}
