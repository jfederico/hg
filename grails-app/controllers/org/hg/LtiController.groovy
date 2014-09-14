package org.hg

import org.hg.EngineFactory
import org.hg.SimpleEngineFactory
import org.hg.engine.Engine
import org.hg.engine.TestEngine

class LtiController {
    HgService hgService
    
    def index() { 
        log.info "###############${params.get('action')}###############"
        hgService.logParameters(params)
        
        try {
            EngineFactory engineFactory = new SimpleEngineFactory()
            Engine engine = engineFactory.getEngine(params)
            
            def target = engine.getTarget();
            log.info "Redirecting to " + target
            redirect(url: target)
        } catch (Exception e){
            log.debug "ERROR: " + e 
            render(text: hgService.xmlError(e), contentType: "text/xml", encoding: "UTF-8")
        }
    }
}
