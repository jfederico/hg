package org.hg;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.hg.engine.IEngine;

public interface EngineFactory {

    public static String ENGINE_TEST            = "hg_test";
    public static String ENGINE_BIGBLUEBUTTON   = "hg_bigbluebutton";
    public static String ENGINE_LIMESURVEY      = "hg_limesurvey";
    public static String ENGINE_YOUTUBE         = "hg_youtube";

    IEngine createEngine(HttpServletRequest request, Map<String, String> params, Map<String, Object> config, String endpoint, Map<String, String> session_params)
            throws Exception;
    Object getEngineClass(Map<String, Object> config)
            throws Exception;

}
