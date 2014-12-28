package org.lti.v1;

import java.util.Map;

import org.apache.log4j.Logger;
import org.lti.LTIException;
import org.lti.ToolProvider;
import org.lti.LTIv1;

public class Launcher extends ToolProvider implements LTIv1 {

    private static final Logger log = Logger.getLogger(Launcher.class);

    public Launcher(String endpoint, String key, String secret, Map<String, String> params)
            throws LTIException, Exception {
        super(endpoint, key, secret, params);

        try {
            validateParameters(OAUTH_REQUIERED_PARAMS);
            log.debug("OAuth required parameters are included");
        } catch (Exception e) {
            throw new LTIException(LTIException.MESSAGEKEY_MISSING_PARAMETERS, "OAuth required parameters missing. " + e.getMessage());
        }

        try {
            validateParameters(LTIv1.BASIC_LTI_LAUNCH_REQUEST_PARAMETERS_REQUIRED);
            log.debug("LTI required parameters are included");
        } catch (Exception e) {
            throw new LTIException(LTIException.MESSAGEKEY_MISSING_PARAMETERS, "LTI version " + LTIv1.VERSION + " parameters not included. " + e.getMessage());
        }

        if( hasValidSignature() ) log.debug("XX: OAuth signature is valid"); else throw new Exception("OAuth signature is NOT valid");
    }

    public String getLTIVersion(){
        return LTIv1.VERSION;
    }

    public String getLTILaunchPresentationReturnURL(){
        return this.params.get(LAUNCH_PRESENTATION_RETURN_URL);
    }
}
