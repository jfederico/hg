package org.hg.engine;

public class TestEngine implements Engine {

    @Override
    public String getTarget(){
        String target = "http://www.test.com/";
        return target;
    }

}
