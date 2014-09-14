package org.hg;

import java.util.Map;

import org.hg.engine.Engine;
import org.hg.engine.TestEngine;

public class SimpleEngineFactory implements EngineFactory {

    public Engine getEngine(Map<String, String> params)
            throws Exception {
        Engine engine = null;

        if( params == null ){
            throw new Exception("The request does not contain params");
        } else if( !params.containsKey("engine_type") ){
            throw new Exception("The request does not include engine_type as a parameter");
        }
        
        String engine_type = params.get("engine_type");
        if( engine_type.equals(ENGINE_TEST) ){
            engine = new TestEngine();
        }
        return engine;
    }

}
