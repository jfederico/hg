package org.hg;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import org.hg.domain.Type;
import org.hg.engine.Engine;
import org.hg.engine.bigbluebutton.BigBlueButtonEngine;
import org.hg.engine.test.TestEngine;

public class SimpleEngineFactory implements EngineFactory {

    private static final Logger log = Logger.getLogger(SimpleEngineFactory.class);

    private static final EngineFactory INSTANCE = new SimpleEngineFactory();

    private SimpleEngineFactory() {}

    public static EngineFactory getInstance() {
        return INSTANCE;
    }

    public Engine createEngine(HttpServletRequest request, Map<String, String> params, Type config)
            throws Exception {
        Engine engine = null;

        log.debug("createEngine()");
        if( config == null ) {
            throw new Exception("There is no configuration for this engine");
        }
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
                    params.put(Engine.PARAM_ACT, "cc");
                else
                    params.put(Engine.PARAM_ACT, "sso");
            }
        }

        String type = params.get("type");
        if( type.equals(ENGINE_TEST) ){
            engine = new TestEngine(params, config);
        } else if( type.equals(ENGINE_BBB) || type.equals(ENGINE_BIGBLUEBUTTON) ){
            engine = new BigBlueButtonEngine(params, config);
        } else {
            throw new Exception(type + " was not identified as a Engine Type");
        }
        return engine;
    }

}
