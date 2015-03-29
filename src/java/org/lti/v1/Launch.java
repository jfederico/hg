package org.lti.v1;

import org.apache.log4j.Logger;
import org.lti.ActionService;
import org.lti.ToolProvider;

public class Launch implements ActionService {
    private static final Logger log = Logger.getLogger(Launch.class);

    public Launch(ToolProvider tpn) {
        log.info("LTILaunch v1p0");
        log.info("============================================================================================");
    }

    public String execute(ToolProvider tpn)
            throws Exception {
        log.info("Executing LTILaunch v1p0");
        // TODO Auto-generated method stub
        if( tpn.hasValidSignature() ) log.debug("OAuth signature is valid"); else throw new Exception("OAuth signature is NOT valid");

        return null;
    }

}
