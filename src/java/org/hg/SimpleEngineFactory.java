package org.hg;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.hg.engine.Engine;
import org.hg.engine.IEngine;
import org.hg.engine.bigbluebutton.BigBlueButtonEngine;
import org.hg.engine.test.TestEngine;

public class SimpleEngineFactory implements EngineFactory {
    private static final Logger log = Logger.getLogger(SimpleEngineFactory.class.getName());

    private static final EngineFactory INSTANCE = new SimpleEngineFactory();

    private SimpleEngineFactory() {}

    public static EngineFactory getInstance() {
        return INSTANCE;
    }

    public IEngine createEngine(HttpServletRequest request, Map<String, String> params, Map<String, Object> config, String endpoint, Map<String, String> session_params)
            throws Exception {
        IEngine engine = null;

        if( config == null ) {
            throw new Exception("There is no configuration for the tenant " + params.get(Engine.PARAM_TENANT));
        }

        if( params.get(Engine.PARAM_ENGINE).equals(Engine.ENGINE_TYPE_LAUNCH) ) {
            if ( !params.containsKey(Engine.PARAM_ACT) ) {
                if( request.getMethod().equals("GET") )
                    params.put(Engine.PARAM_ACT, Engine.ENGINE_ACT_CC);
                else
                    params.put(Engine.PARAM_ACT, Engine.ENGINE_ACT_SSO);
            }
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> vendor = (Map<String, Object>)config.get("product");
        String vendor_code = (String)vendor.get("code");

        log.debug("====== Creating object::SimpleEngineFactory() for [" + vendor_code + "]");
        System.out.println("====== Creating object::SimpleEngineFactory() for [" + vendor_code + "]");

        if( vendor_code.equals(ENGINE_TEST) ){
            engine = new TestEngine(request, params, config, endpoint, session_params);
        } else if( vendor_code.equals(ENGINE_BIGBLUEBUTTON) ){
            engine = new BigBlueButtonEngine(request, params, config, endpoint, session_params);
        } else {
            throw new Exception(vendor_code + " was not identified as a vendor code for an Engine");
        }

        return engine;
    }

    public Object getEngineClass(Map<String, Object> config) throws Exception {
        Object engineClass = null;

        @SuppressWarnings("unchecked")
        Map<String, Object> vendor = (Map<String, Object>)config.get("vendor");
        String vendor_code = (String)vendor.get("code");

        if(vendor_code.equals(ENGINE_TEST) ){
            engineClass = TestEngine.class;
        } else if( vendor_code.equals(ENGINE_BIGBLUEBUTTON) ){
            engineClass = BigBlueButtonEngine.class;
        } else {
            throw new Exception(vendor_code + " was not identified as a vendor code for an Engine");
        }

        return engineClass;
    }

}
