package org.hg

import org.hg.HgService
import org.hg.domain.Type
import org.hg.domain.Tenant
import org.hg.domain.Key

class HgService {

    public static String CODE_ERROR = "error"
    public static String CODE_SUCCESS = "success"
    
    private List<Object> config
    
    public HgService(){
        Map<String, Object> tTest
        tTest = new HashMap<String, Object>()
        tTest.put("id", "0")
        tTest.put("name", "test")
        List<Object> aliases = new ArrayList<Object>()
        aliases.add("t1")
        aliases.add("t2")
        tTest.put("aliases", aliases )
        Map<String, Object> vendor = new HashMap<String, Object>()
        vendor.put("code", "hg_test")
        vendor.put("name", "HG Test")
        vendor.put("description", "Default LTI Gateway for processing test requests")
        vendor.put("url", "http://www.123it.ca/hg")
        vendor.put("contact", "admin@123it.ca")
        tTest.put("vendor", vendor)

        config = new ArrayList<Object>()
        config.add(tTest)
    }

    def logParameters(params) {
        log.info "----------------------------------"
        for( param in params ) log.info "${param.getKey()}=${param.getValue()}"
        log.info "----------------------------------"
    }

    def xmlResponse(String msg='No message', String code=CODE_ERROR, String key='GenericResponse') {
        def xml = '' +
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<response>\n" +
                "  <returncode>${code}</returncode>\n" +
                "  <messagekey>${key}</messagekey>\n" +
                "  <message>${msg}</message>\n" +
                "</response>"
        return xml
    }

    def getConfig(String tenant) {
        for( Map<String, Object> cfg : config){
            if( tenant == cfg.get("id") || tenant == cfg.get("name") || lookupAliases(tenant, cfg.get("aliases")) )
                return cfg
        }
        return null
    }

    def lookupAliases(String tenant, List<Object> aliases){
        for( String alias : aliases ){
            if( alias == tenant )
                return true
        }
        return false
    }
}
