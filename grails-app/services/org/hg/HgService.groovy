package org.hg

import org.hg.HgService
import org.hg.domain.Type
import org.hg.domain.Tenant
import org.hg.domain.Key

class HgService {

    public static String CODE_ERROR = "error"
    public static String CODE_SUCCESS = "success"
    
    private List<Type> config
    
    private String jsonConfig = '' +
        '[' + "\n" +
        '   {"id": 0}' + "\n" +
        '   {"id": 0}' + "\n" +
        '   }' + "\n" +
        '' + "\n" +
        ']'
    
    public HgService(){
        config = new ArrayList<Type>()
        config.add(new Type())
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

    def getConfig(String type) {
        for( Type cfg : config){
            if( cfg.getCode() == type )
            	return cfg
        }
        return null
    }
}
