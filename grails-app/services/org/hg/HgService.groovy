package org.hg

import org.hg.HgService
import org.hg.domain.Type
import org.hg.domain.Tenant
import org.hg.domain.Key

import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONException

class HgService {
    def endpoint

    public static String CODE_ERROR = "error"
    public static String CODE_SUCCESS = "success"

    private List<Object> config

    public HgService(){
        config = new ArrayList<Object>()
        config.add(getTenantTest())
    }

    def getTenantTest(){
        Map<String, Object> tTest
        tTest = new LinkedHashMap<String, Object>()
        tTest.put("id", "0")
        tTest.put("name", "test")
        List<Object> aliases = new ArrayList<Object>()
        Map<String, String> alias1 = new HashMap<String,String>()
        alias1.put("alias", "t1")
        aliases.add(alias1)
        Map<String, String> alias2 = new HashMap<String,String>()
        alias2.put("alias", "t2")
        aliases.add(alias2)
        tTest.put("aliases", aliases )
        tTest.put("title", "Hyper Gateway ")
        tTest.put("description", "Hyper Gateway is an LTI broker for implementing generic LTI tool providers")
        Map<String, Object> vendor = new LinkedHashMap<String, Object>()
        vendor.put("code", "hg_test")
        vendor.put("name", "HG Test")
        vendor.put("description", "Default LTI Gateway for processing test requests")
        vendor.put("url", "http://www.123it.ca/hg")
        vendor.put("contact", "admin@123it.ca")
        tTest.put("vendor", vendor)
        Map<String, Object> lti = new LinkedHashMap<String, Object>()
        lti.put("id", "0")
        lti.put("key", "test")
        lti.put("secret", "testtest")
        lti.put("profiles", new ArrayList<Object>())
        tTest.put("lti", lti)
        Map<String, Object> engine = new LinkedHashMap<String, Object>()
        engine.put("key", "test")
        engine.put("secret", "test")
        engine.put("endpoint", "test")
        tTest.put("engine", engine)
        return tTest
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

    def getJSONConfig(String tenant) {
        def config_home = grails.util.BuildSettingsHolder.settings.baseDir.toString() + "/grails-app/conf"
        // Initialise jsonTenants
        JSONArray jsonTenants = new JSONArray()
        // Add the predefined tenant "test" to the jsonTenants array
        jsonTenants.put( new JSONObject(getTenantTest()) )
        //log.debug jsonTenants

        // Load the configuration file (if exists)
        try {
            String fileConfig = new File(config_home + "/config.json").text
            JSONObject jsonConfig = new JSONObject(fileConfig)
            // Add existing tenant profiles to jsonTenants array
            JSONArray tenants = jsonConfig.getJSONArray("tenants")
            for (int i = 0; i < tenants.length(); i++) {
                jsonTenants.put(tenants.get(i));
            }
        } catch ( Exception e ) {
            // There is nothing to do, the jsonArray will contain only the tenant "test"
        }
        //log.debug jsonTenants

        for (int i = 0; i < jsonTenants.length(); i++) {
            try {
                JSONObject jsonTenant = jsonTenants.getJSONObject(i);
                if( tenant == jsonTenant.getString("id") || tenant == jsonTenant.getString("name") || jsonLookupAliases(tenant, jsonTenant.getJSONArray("aliases")) )
                    return jsonTenant
            } catch ( Exception e ) {
                log.debug e.message
            }
        }
    }

    def jsonLookupAliases(String tenant, JSONArray jsonAliases){
        for (int i = 0; i < jsonAliases.length(); i++) {
            JSONObject jsonAlias = jsonAliases.getJSONObject(i);
            if( tenant == jsonAlias.getString("alias") )
                return true
        }
        return false
    }

    public Map jsonToMap(JSONObject json) throws JSONException
    {
        Map<String, Object> retMap = new HashMap<String, Object>();

        if(json != JSONObject.NULL)
        {
            retMap = toMap(json);
        }
        return retMap;
    }

    public Map toMap(JSONObject object) throws JSONException
    {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext())
        {
            String key = keysItr.next();
            Object value = object.get(key);

            if(value instanceof JSONArray)
            {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject)
            {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public List toList(JSONArray array) throws JSONException
    {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.length(); i++)
        {
            Object value = array.get(i);
            if(value instanceof JSONArray)
            {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject)
            {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }
}
