package org.hg

import org.hg.EngineFactory
import org.hg.SimpleEngineFactory
import org.hg.engine.Engine
import org.hg.engine.TestEngine

class LtiController {
    HgService hgService
    
    EngineFactory engineFactory = SimpleEngineFactory.getInstance()
    
    def index() { 
        log.info "###############${params.get('action')}###############"
        hgService.logParameters(params)
        
        try {
            Engine engine = engineFactory.getEngine(params)
            
            def target = engine.getTarget();
            log.info "Redirecting to " + target
            render(text: hgService.xmlResponse("Redirecting to " + target), contentType: "text/xml", encoding: "UTF-8")
            //redirect(url: target)
        } catch (Exception e){
            log.debug "ERROR: " + e 
            render(text: hgService.xmlResponse(e.getMessage()), contentType: "text/xml", encoding: "UTF-8")
        }
    }
}
