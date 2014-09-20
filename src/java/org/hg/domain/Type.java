package org.hg.domain;

import java.util.ArrayList;
import java.util.List;

public class Type {
    private Integer id = 0;
    private String code = "test";
    private String name = "Test";
    private String description = "LTI Gateway for processing testing requests";
    private List<Tenant> tenants = new ArrayList<Tenant>();
    
    public Type() throws Exception {
        try {
            this.tenants.add(new Tenant());
        } catch( Exception e ){
            throw e;
        }
    }
    
    public Integer getId(){
        return this.id;
    }

    public String getCode(){
        return this.code;
    }

    public String getName(){
        return this.name;
    }

    public String getDescription(){
        return this.description;
    }

    public List<Tenant> getTenants(){
        return this.tenants;
    }

    @Override
    public String toString(){
        return "Configuration type is [" + code + "]";
    }
}
