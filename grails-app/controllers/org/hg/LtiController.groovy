package org.hg

import java.util.List;

import org.hg.EngineFactory
import org.hg.SimpleEngineFactory
import org.hg.domain.Type;
import org.hg.engine.IEngine
import org.hg.engine.test.TestEngine;

class LtiController {
    HgService hgService
    
    EngineFactory engineFactory
    
    LtiController() {
        this.engineFactory = SimpleEngineFactory.getInstance()     
    }
    
    def index() {
        log.info "###############${params.get('action')}###############"
        hgService.logParameters(params)

        try {
            def json_config = hgService.getJSONConfig(params.get("tenant"))
            def config = hgService.jsonToMap(json_config)

            IEngine engine = engineFactory.createEngine(request, params, config, hgService.endpoint)
            Object engineClass = engineFactory.getEngineClass(config)
            log.debug engineClass.ENGINE_CODE
            
            def ltiConstants = engine.getToolProvider()
            log.debug ltiConstants.TOOL_CONSUMER_INFO_PRODUCT_FAMILY_CODE

            def completionResponse = engine.getCompletionResponse()
            if( completionResponse == null ){
                log.debug "ERROR: "
                render(text: hgService.xmlResponse("completionResponse is null"), contentType: "text/xml", encoding: "UTF-8")
            } else {
                if( completionResponse.get("type") == "url" ) {
                    log.info "Redirecting to " + completionResponse
                    render(text: hgService.xmlResponse("Redirecting to " + completionResponse, hgService.CODE_SUCCESS), contentType: "text/xml", encoding: "UTF-8")
                    //redirect(url: completionResponse.get("content"))
                } else if( completionResponse.get("type") == "xml" ) {
                    log.info "Rendering XML\n" + completionResponse.get("content")
                    render(text: completionResponse.get("content"), contentType: "text/xml", encoding: "UTF-8")
                } else {
                    log.debug "ERROR: "
                    render(text: hgService.xmlResponse("completionResponse not identified. Only url and xml are registered"), contentType: "text/xml", encoding: "UTF-8")
                }
            }
        } catch (Exception e){
            log.debug "ERROR: " + e
            render(text: hgService.xmlResponse(e.getMessage()), contentType: "text/xml", encoding: "UTF-8")
        }
    }
}
