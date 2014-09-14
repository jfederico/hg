package org.hg;

import java.util.Map;

import org.hg.engine.Engine;
import org.hg.engine.TestEngine;
import org.hg.engine.BBBEngine;

public class SimpleEngineFactory implements EngineFactory {

    private static final EngineFactory INSTANCE = new SimpleEngineFactory();

    private SimpleEngineFactory() {}

    public static EngineFactory getInstance() {
        return INSTANCE;
    }

    public Engine getEngine(Map<String, String> params)
            throws Exception {
        Engine engine = null;

        if( params == null ){
            throw new Exception("The request does not contain params");
        } else if( !params.containsKey("engine_type") ){
            throw new Exception("The request does not include engine_type as a parameter");
        } else if ( !params.containsKey("id") ){
            params.put("id", "0");
        }

        String engine_type = params.get("engine_type");
        if( engine_type.equals(ENGINE_TEST) ){
            engine = new TestEngine();
        } else if( engine_type.equals(ENGINE_BBB) || engine_type.equals(ENGINE_BIGBLUEBUTTON) ){
            engine = new BBBEngine();
        } else {
            throw new Exception(engine_type + " was not identified as an Engine Type");
        }
        return engine;
    }

}
