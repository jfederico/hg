package org.hg;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.hg.engine.Engine;
import org.hg.engine.bigbluebutton.BigBlueButtonEngine;
import org.hg.engine.test.TestEngine;

public class SimpleEngineFactory implements EngineFactory {

    private static final EngineFactory INSTANCE = new SimpleEngineFactory();

    private SimpleEngineFactory() {}

    public static EngineFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public Engine getEngine(HttpServletRequest request, Map<String, String> params)
            throws Exception {
        Engine engine = null;

        if( params == null ){
            throw new Exception("The request does not contain params");
        } else if( !params.containsKey(Engine.PARAM_TYPE) ){
                throw new Exception("The request does not include [type] as a parameter");
        } else {
            if ( !params.containsKey(Engine.PARAM_ID) ){
                params.put(Engine.PARAM_ID, "0");
            }
            if ( !params.containsKey(Engine.PARAM_ACT) ){
                if( request.getMethod().equals("GET") )
                    params.put(Engine.PARAM_ACT, "sso");
                else
                    params.put(Engine.PARAM_ACT, "cc");
            }
        }

        String type = params.get("type");
        if( type.equals(ENGINE_TEST) ){
            engine = new TestEngine(params);
        } else if( type.equals(ENGINE_BBB) || type.equals(ENGINE_BIGBLUEBUTTON) ){
            engine = new BigBlueButtonEngine(params);
        } else {
            throw new Exception(type + " was not identified as a Engine Type");
        }
        return engine;
    }

}
