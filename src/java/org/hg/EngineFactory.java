package org.hg;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.hg.engine.IEngine;

public interface EngineFactory {

    public static String ENGINE_TEST = "hg_test";
    public static String ENGINE_BN = "blindside_networks";
    public static String ENGINE_BIGBLUEBUTTON = "big_blue_button";
    public static String ENGINE_CW = "cw";
    public static String ENGINE_CHALKANDWIRE = "chalk_and_wire";

    IEngine createEngine(HttpServletRequest request, Map<String, String> params, Map<String, Object> config, String endpoint) throws Exception;
    Object getEngineClass(Map<String, Object> config) throws Exception;

}
