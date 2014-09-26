package org.hg

import java.util.List;

import org.hg.EngineFactory
import org.hg.SimpleEngineFactory
import org.hg.domain.Type;
import org.hg.engine.Engine
import org.hg.engine.test.TestEngine;

class LtiController {
    HgService hgService
    
    EngineFactory engineFactory = SimpleEngineFactory.getInstance()

    def index() {
        log.info "###############${params.get('action')}###############"
        hgService.logParameters(params)

        try {
            render(text: hgService.xmlResponse("All good", hgService.CODE_SUCCESS), contentType: "text/xml", encoding: "UTF-8")
        } catch (Exception e){
            log.debug "ERROR: " + e
            render(text: hgService.xmlResponse(e.getMessage()), contentType: "text/xml", encoding: "UTF-8")
        }
    }

    def index2() { 
        log.info "###############${params.get('action')}###############"
        hgService.logParameters(params)

        try {
            Type config = hgService.getConfig(params.get("type"))
            log.debug config
            Engine engine = engineFactory.createEngine(request, params, config)

            def completionContent = engine.getCompletionContent()
            if( completionContent == null ){
                log.debug "ERROR: "
                render(text: hgService.xmlResponse("completionContent is null"), contentType: "text/xml", encoding: "UTF-8")
            } else {
                if( completionContent.get("type") == "url" ) {
                    log.info "Redirecting to " + completionContent
                    render(text: hgService.xmlResponse("Redirecting to " + completionContent, hgService.CODE_SUCCESS), contentType: "text/xml", encoding: "UTF-8")
                    //redirect(url: completionContent)
                } else if( completionContent.get("type") == "xml" ) {
                    log.info "Rendering XML\n" + completionContent.get("content")
                    render(text: completionContent.get("content"), contentType: "text/xml", encoding: "UTF-8")
                } else {
                    log.debug "ERROR: "
                    render(text: hgService.xmlResponse("completionContent not identified. Only url and xml are registered"), contentType: "text/xml", encoding: "UTF-8")
                }
            }
        } catch (Exception e){
            log.debug "ERROR: " + e 
            render(text: hgService.xmlResponse(e.getMessage()), contentType: "text/xml", encoding: "UTF-8")
        }
    }
}
