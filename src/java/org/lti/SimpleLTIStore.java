package org.lti;

import java.util.Map;

import org.apache.log4j.Logger;
import org.lti.LTIv2;

public class SimpleLTIStore {

    private static final Logger log = Logger.getLogger(SimpleLTIStore.class);

    /**
     * lti_version=LTI-1p0
     * <p>
     * This indicates which version of the specification is being used for this
     * particular message. This parameter is required.
     */
    public static final String LTI_VERSION = "lti_version";

    private static final String LTI_VERSION_V1P0    = "LTI-1p0";
    //private static final String LTI_VERSION_V1P1    = "LTI-1p1";
    //private static final String LTI_VERSION_V1P1P1  = "LTI-1p1p1";
    //private static final String LTI_VERSION_V1P2    = "LTI-1p2";
    //private static final String LTI_VERSION_V2P0    = "LTI-2p0";
    //private static final String LTI_VERSION_V2P1    = "LTI-2p0";
    private static final String LTI_VERSION_DEFAULT = LTI_VERSION_V1P0;

    private static final String LTI_VERSION_V1   = "1";
    private static final String LTI_VERSION_V2   = "2";

    public static ToolProvider createToolProvider(Map<String, String> params, String endpoint, String key, String secret) 
            throws LTIException {
        log.info("Creating LTIToolProvider");

        ToolProvider tp = null;

        String version = getVersionNumber(params);
        log.debug("LTI version: " + version);

        try {
            if( version.equals(LTI_VERSION_V2)) {
                if( params.containsKey(LTIv2.LTI_MESSAGE_TYPE) && params.get(LTIv2.LTI_MESSAGE_TYPE).equals(LTIv2.LTI_MESSAGE_TYPE_TOOL_PROXY_REGISTRATION_REQUEST) ) {
                    log.debug("Instantiating LTI-2p0 ToolProvider->Registrant");
                    tp = new org.lti.v2.Registrant(endpoint, key, secret, params);
                } else {
                    log.debug("Instantiating LTI-2p0 ToolProvider->Launcher");
                    tp = new org.lti.v2.Launcher(endpoint, key, secret, params);
                }
            } else {
                log.debug("Instantiating LTI-1p0 ToolProvider->Launcher");
                tp = new org.lti.v1.Launcher(endpoint, key, secret, params);
            }

        } catch ( Exception e ){
            throw new LTIException(LTIException.MESSAGEKEY_INTERNALERROR, "The tool provider could not be instantiated", e.getCause());
        }

        return tp;
    }
    
    public static String getVersionNumber(Map<String, String> params) {
        String versionNumber = LTI_VERSION_V1;

        String version = params.containsKey(LTI_VERSION)? params.get(LTI_VERSION): LTI_VERSION_DEFAULT;
        String[] versionA = version.split("-"); 
        String[] versionB = versionA[1].split("p");
        versionNumber = versionB[0];

        return versionNumber;
    }
}
