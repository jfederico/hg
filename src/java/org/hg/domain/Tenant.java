package org.hg.domain;

import java.util.ArrayList;
import java.util.List;

public class Tenant {
    Integer id = 0;
    String name = "tool";
    String[] aliases = {"tool.xml"};
    ToolProvider tp;
    List<Key> keys = new ArrayList<Key>();

    Tenant() throws Exception{
        try {
            this.tp = new ToolProvider();
            this.keys.add(new Key());
        } catch( Exception e ){
            throw e;
        }
    }
}
