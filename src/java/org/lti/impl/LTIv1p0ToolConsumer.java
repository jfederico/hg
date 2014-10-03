package org.lti.impl;

import org.lti.api.LTIException;
import org.lti.api.LTIToolConsumer;

public class LTIv1p0ToolConsumer extends LTIToolConsumer implements LTIv1p0 {
    
    public String getLTIVersion(){
        return VERSION;
    }
}
