package org.lti.v2;

import java.util.Map;

import org.apache.log4j.Logger;
import org.lti.LTIException;
import org.lti.ToolProvider;
import org.lti.LTIv2;

public class Launcher extends ToolProvider implements LTIv2 {

    private static final Logger log = Logger.getLogger(Launcher.class);

    public Launcher(String endpoint, String key, String secret, Map<String, String> params)
            throws LTIException, Exception {
        super(endpoint, key, secret, params);
        log.debug("XX: Instantiating Launcher() v2");

        try {
            validateParameters(OAUTH_REQUIERED_PARAMS);
            log.debug("XX: OAuth required parameters are included");
        } catch (Exception e) {
            throw new LTIException(LTIException.MESSAGEKEY_MISSING_PARAMETERS, "OAuth required parameters missing. " + e.getMessage());
        }

        try {
            validateParameters(LTIv2.BASIC_LTI_LAUNCH_REQUEST_PARAMETERS_REQUIRED);
            log.debug("XX: LTI required parameters are included");
        } catch (Exception e) {
            throw new LTIException(LTIException.MESSAGEKEY_MISSING_PARAMETERS, "LTI version " + LTIv2.VERSION + " parameters not included. " + e.getMessage());
        }

        if( hasValidSignature() ) log.debug("XX: OAuth signature is valid"); else throw new Exception("OAuth signature is NOT valid");
    }

    public String getLTIVersion(){
        return LTIv2.VERSION;
    }

}
