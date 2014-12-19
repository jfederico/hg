package org.lti;

import java.util.Map;

import org.apache.log4j.Logger;
import org.lti.v1.LTIv1ToolProvider;
import org.lti.v2.LTIv2ToolProvider;

public class SimpleLTIStore {

    private static final Logger log = Logger.getLogger(SimpleLTIStore.class);

    private static final String LTI_V1P0 = "LTI-1p0";
    private static final String LTI_V1   = "1";
    private static final String LTI_V2   = "2";

    public static LTIToolProvider createToolProvider(Map<String, String> params, Map<String, Object> config, String endpoint) 
            throws LTIException {
        log.info("Creating LTIToolProvider");

        LTIToolProvider tp = null;

        String version = getVersionNumber(params);
        log.debug("LTI version: " + version);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> lti_cfg = (Map<String, Object>)config.get("lti");
            String key = (String)lti_cfg.get("key");
            String secret = (String)lti_cfg.get("secret");

            if( version.equals(LTI_V1)) {
                tp = new LTIv1ToolProvider(endpoint, key, secret, params);
                tp.setValidateRequiredParametersCommand( new org.lti.v1.ValidateRequiredParameters4Launch() );
            } else if( version.equals(LTI_V2)) {
                tp = new LTIv2ToolProvider(endpoint, key, secret, params);
                log.debug(config.toString());
                log.debug(params.toString());
                log.debug(tp.getParameters().toString());
                tp.setValidateRequiredParametersCommand( new org.lti.v2.ValidateRequiredParameters4Launch() );
                log.debug("HERE");
            } else {
                tp = new LTIv1ToolProvider(endpoint, key, secret, params);
                tp.setValidateRequiredParametersCommand( new org.lti.v1.ValidateRequiredParameters4Launch() );
            }

        } catch ( Exception e ){
            throw new LTIException(LTIException.MESSAGEKEY_INTERNALERROR, "The tool provider could not be instantiated", e.getCause());
        }
        return tp;
    }
    
    private static String getVersionNumber(Map<String, String> params) {
        String versionNumber = LTI_V1;

        String version = params.containsKey(LTI.LTI_VERSION)? params.get(LTI.LTI_VERSION): LTI_V1P0;
        String[] versionA = version.split("-"); 
        String[] versionB = versionA[1].split("p");
        versionNumber = versionB[0];

        return versionNumber;
    }
}
