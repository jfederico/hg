package org.hg.engine.bigbluebutton;

import java.util.Map;

import org.hg.engine.CompletionResponse;
import org.hg.engine.Engine;

public class BigBlueButtonEngine extends Engine {

    public BigBlueButtonEngine(Map<String, String> params, Map<String, Object> config, String endpoint){
        super(params, config, endpoint);
    }

    @Override
    public Map<String, String> getCompletionResponse() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setCompletionResponseCommand(CompletionResponse completionResponse) {
        // TODO Auto-generated method stub
        
    }

}
