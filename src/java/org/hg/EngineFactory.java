package org.hg;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.hg.engine.Engine;

public interface EngineFactory {

    public static String ENGINE_TEST = "hg_test";
    public static String ENGINE_BN = "blindside_networks";
    public static String ENGINE_BIGBLUEBUTTON = "bigbluebutton";
    public static String ENGINE_CW = "cw";
    public static String ENGINE_CHALKANDWIRE = "chalk_and_wire";

    Engine createEngine(HttpServletRequest request, Map<String, String> params, Map<String, Object> config, String endpoint) throws Exception;

}
