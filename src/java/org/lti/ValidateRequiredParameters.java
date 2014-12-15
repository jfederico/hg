package org.lti;

import java.util.Map;

import org.json.JSONArray;

public interface ValidateRequiredParameters {
    public void execute(Map<String, String> params, JSONArray requiredParameters) throws LTIException;
}
