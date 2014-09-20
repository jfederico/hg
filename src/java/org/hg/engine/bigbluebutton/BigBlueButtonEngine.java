package org.hg.engine.bigbluebutton;

import java.util.Map;

import org.hg.domain.Type;
import org.hg.engine.CompletionContent;
import org.hg.engine.Engine;

public class BigBlueButtonEngine extends Engine {

    public BigBlueButtonEngine(Map<String, String> params, Type config){
        super(params, config);
    }

    @Override
    public Map<String, String> getCompletionContent() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setCompletionContentCommand(CompletionContent completionContent) {
        // TODO Auto-generated method stub
        
    }

}
