package org.lti.api;

import java.util.Map;

public abstract class LTIStore {
    public abstract LTIToolProvider createToolProvider(Map<String, String> params, Map<String, Object> config, String endpoint) throws LTIException;
}
