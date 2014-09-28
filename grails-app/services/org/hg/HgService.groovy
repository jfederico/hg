package org.hg

import org.hg.HgService
import org.hg.domain.Type
import org.hg.domain.Tenant
import org.hg.domain.Key

class HgService {
    def endpoint

    public static String CODE_ERROR = "error"
    public static String CODE_SUCCESS = "success"

    private List<Object> config

    public HgService(){
        config = new ArrayList<Object>()
        config.add(getTenantTest())
        config.add(getTenantBN())
    }

    def getTenantTest(){
        Map<String, Object> tTest
        tTest = new LinkedHashMap<String, Object>()
        tTest.put("id", "0")
        tTest.put("name", "test")
        List<Object> aliases = new ArrayList<Object>()
        aliases.add("t1")
        aliases.add("t2")
        tTest.put("aliases", aliases )
        Map<String, Object> vendor = new LinkedHashMap<String, Object>()
        vendor.put("code", "hg_test")
        vendor.put("name", "HG Test")
        vendor.put("description", "Default LTI Gateway for processing test requests")
        vendor.put("url", "http://www.123it.ca/hg")
        vendor.put("contact", "admin@123it.ca")
        tTest.put("vendor", vendor)
        Map<String, Object> lti = new LinkedHashMap<String, Object>()
        lti.put("key", "test")
        lti.put("secret", "testtest")
        tTest.put("lti", lti)
        Map<String, Object> engine = new LinkedHashMap<String, Object>()
        engine.put("key", "test")
        engine.put("secret", "test")
        engine.put("endpoint", "test")
        engine.put("profiles", new ArrayList<Object>())
        tTest.put("engine", engine)
        return tTest
    }
    
    def getTenantBN(){
        Map<String, Object> tBN
        tBN = new LinkedHashMap<String, Object>()
        tBN.put("id", "0")
        tBN.put("name", "bn")
        List<Object> aliases = new ArrayList<Object>()
        aliases.add("bbb")
        aliases.add("bigbluebutton")
        tBN.put("aliases", aliases )
        Map<String, Object> vendor = new LinkedHashMap<String, Object>()
        vendor.put("code", "big_blue_button")
        vendor.put("name", "BN Test Install")
        vendor.put("description", "Default LTI Gateway for processing test requests on the BigBlueButton test-install server")
        vendor.put("url", "http://www.blindsidenetworks.com/")
        vendor.put("contact", "admin@blindsidenetworks.com")
        tBN.put("vendor", vendor)
        Map<String, Object> lti = new LinkedHashMap<String, Object>()
        lti.put("key", "bbb")
        lti.put("secret", "welcome")
        tBN.put("lti", lti)
        Map<String, Object> engine = new LinkedHashMap<String, Object>()
        engine.put("key", "")
        engine.put("secret", "8cd8ef52e8e101574e400365b55e11a6")
        engine.put("endpoint", "http://test-install.blindsidenetworks.com/bigbluebutton/")
        engine.put("profiles", new ArrayList<Object>())
        tBN.put("engine", engine)
        return tBN
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
            log.debug cfg
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
