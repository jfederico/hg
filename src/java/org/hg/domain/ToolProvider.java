package org.hg.domain;

import java.net.MalformedURLException;
import java.net.URL;

public class ToolProvider {
    String vendor_code = "test";
    String key = "";
    String secret = "";
    URL endpoint;
    String name = "Test";
    String description = "LTI Tool Provider for processing testing requests";
    
    ToolProvider() throws Exception{
        try {
            this.endpoint = new URL("http://hg.123it.ca/test");
        } catch( MalformedURLException e) {
            throw new Exception("Error creating ToolProvider", e);
        }
    }
}
