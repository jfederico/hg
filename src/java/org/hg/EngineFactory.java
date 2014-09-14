package org.hg;

import java.util.Map;

import org.hg.engine.Engine;

public interface EngineFactory {

    public static String ENGINE_TEST = "test";

    Engine getEngine(Map<String, String> params) throws Exception;

}
