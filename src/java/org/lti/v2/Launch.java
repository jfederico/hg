package org.lti.v2;

import org.apache.log4j.Logger;
import org.lti.ActionService;
import org.lti.ToolProviderNew;

public class Launch implements ActionService {
    private static final Logger log = Logger.getLogger(Launch.class);

    public Launch() {
        log.info("LTILaunch v2p0");
    }

    public String execute(ToolProviderNew tpn)
            throws Exception {
        log.info("Executing LTILaunch v2p0");
        // TODO Auto-generated method stub
        return null;
    }

}
