package org.lti.api;

import java.util.Map;

import org.apache.log4j.Logger;
import org.lti.impl.LTIv1p0ToolProvider;
import org.lti.impl.LTIv1p1ToolProvider;

public class SimpleLTIStore {

    private static final Logger log = Logger.getLogger(SimpleLTIStore.class);

    private static final String LTI_V1P0   = "LTI-1p0";
    private static final String LTI_V1P1   = "LTI-1p1";
    private static final String LTI_V1P1P1 = "LTI-1p1p1";
    private static final String LTI_V1P2   = "LTI-1p2";
    private static final String LTI_V2P0   = "LTI-2p0";

    public static LTIToolProvider createToolProvider(Map<String, String> params, Map<String, Object> config, String endpoint) 
            throws LTIException {
        log.info("Creating LTIToolProvider");

        LTIToolProvider tp = null;

        String version = LTI_V1P0;
        if( params.containsKey(LTI.LTI_VERSION) ) 
            version = params.get(LTI.LTI_VERSION);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> lti_cfg = (Map<String, Object>)config.get("lti");
            String key = (String)lti_cfg.get("key");
            String secret = (String)lti_cfg.get("secret");

            log.debug("LTI version: " + version);
            if( version.equals(LTI_V1P1)) {
                tp = new LTIv1p1ToolProvider(endpoint, key, secret, params);
            } else if( version.equals(LTI_V1P1P1)) {
            //    tp = new LTIv1p1p1ToolProvider(endpoint, key, secret, params);
            } else if( version.equals(LTI_V1P2)) {
            //    tp = new LTIv1p2ToolProvider(endpoint, key, secret, params);
            } else if( version.equals(LTI_V2P0)) {
            //    tp = new LTIv2p0ToolProvider(endpoint, key, secret, params);
            } else {
                tp = new LTIv1p0ToolProvider(endpoint, key, secret, params);
            }

        } catch ( Exception e ){
            throw new LTIException(LTIException.MESSAGEKEY_INTERNALERROR, "The tool provider could not be instantiated", e.getCause());
        }
        return tp;
    }
}
