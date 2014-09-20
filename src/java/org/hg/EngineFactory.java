package org.hg;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.hg.domain.Type;
import org.hg.engine.Engine;

public interface EngineFactory {

    public static String ENGINE_TEST = "test";
    public static String ENGINE_BBB = "bbb";
    public static String ENGINE_BIGBLUEBUTTON = "bigbluebutton";
    public static String ENGINE_CW = "cw";
    public static String ENGINE_CHALKANDWIRE = "chalkandwire";

    Engine createEngine(HttpServletRequest request, Map<String, String> params, Type config) throws Exception;

}
