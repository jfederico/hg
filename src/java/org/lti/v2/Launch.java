package org.lti.v2;

import org.apache.log4j.Logger;
import org.lti.ActionService;
import org.lti.ToolProvider;

public class Launch implements ActionService {
    private static final Logger log = Logger.getLogger(Launch.class);

    public Launch(ToolProvider tpn)
            throws Exception {
        log.info("============================================================================================");
        log.info("LTILaunch v2p0");
    }

    public String execute(ToolProvider tpn)
            throws Exception {
        log.info("Executing LTILaunch v2p0");
        // TODO Auto-generated method stub
        if( tpn.hasValidSignature() ) log.debug("OAuth signature is valid"); else throw new Exception("OAuth signature is NOT valid");

        return null;
    }

}
