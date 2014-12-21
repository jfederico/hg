package org.lti.v2;

import java.util.Map;

import org.apache.log4j.Logger;
import org.lti.LTIException;
import org.lti.ToolProvider;
import org.lti.LTIv2;

public class Registrant extends ToolProvider implements LTIv2 {

    private static final Logger log = Logger.getLogger(Registrant.class);

    public Registrant(String endpoint, String key, String secret, Map<String, String> params)
            throws LTIException, Exception {
        super(endpoint, key, secret, params);
        log.debug("XX: Instantiating Registrant() v2");

        try {
            validateParameters(LTIv2.TOOL_PROXY_REGISTRATION_REQUEST_PARAMETERS_REQUIRED);
            log.debug("XX: LTI required parameters are included");
        } catch (Exception e) {
            throw new LTIException(LTIException.MESSAGEKEY_MISSING_PARAMETERS, "LTI version " + LTIv2.VERSION + " parameters not included. " + e.getMessage());
        }
    }

    public String getLTIVersion(){
        return LTIv2.VERSION;
    }

}
