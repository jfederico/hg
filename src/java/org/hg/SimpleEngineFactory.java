package org.hg;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
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

    public Engine createEngine(HttpServletRequest request, Map<String, String> params, Map<String, Object> config, String endpoint)
            throws Exception {
        Engine engine = null;

        log.debug("createEngine()");
        if( config == null ) {
            throw new Exception("There is no configuration for the tenant " + params.get(Engine.PARAM_TENANT));
        }

        if( params.get("engine").equals("lti") ) {
            if ( !params.containsKey(Engine.PARAM_ACT) ) {
                if( request.getMethod().equals("GET") )
                    params.put(Engine.PARAM_ACT, "cc");
                else
                    params.put(Engine.PARAM_ACT, "sso");
            }
        }

        Map<String, Object> vendor = (Map<String, Object>)config.get("vendor");
        String vendor_code = (String)vendor.get("code");
        log.debug(vendor_code);

        if( vendor_code.equals(ENGINE_TEST) ){
            engine = new TestEngine(params, config, endpoint);
        } else if( vendor_code.equals(ENGINE_BIGBLUEBUTTON) ){
            engine = new BigBlueButtonEngine(params, config, endpoint);
        } else {
            throw new Exception(vendor_code + " was not identified as a vendor code for an Engine");
        }

        return engine;
    }

}
