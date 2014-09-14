package org.hg

import org.hg.HgService

class HgService {

    def logParameters(Object params) {
        log.info "----------------------------------"
        for( param in params ) log.info "${param.getKey()}=${param.getValue()}"
        log.info "----------------------------------"
    }

    def xmlResponse(String msg) {
        def xml = '' +
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<response>\n" +
        "  <returncode>error</returncode>\n" +
        "  <messagekey>GeneralError</messagekey>\n" +
        "  <message>${(msg!=null? msg: 'No message')}</message>\n" +
        "</response>"
        return xml
    }
}
