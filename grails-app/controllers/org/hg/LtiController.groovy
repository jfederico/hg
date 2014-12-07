package org.hg

import java.util.List

import org.hg.EngineFactory
import org.hg.SimpleEngineFactory
import org.hg.domain.Type
import org.hg.engine.IEngine
import org.hg.engine.test.TestEngine

class LtiController {
    HgService hgService

    EngineFactory engineFactory

    LtiController() {
        this.engineFactory = SimpleEngineFactory.getInstance()
    }

    def index() {
        log.info "###############${params.get('action')}###############"
        //def basePath = grailsAttributes.getApplicationContext().getResource("/files/").getFile().toString()
        //def basePath = grails.util.BuildSettingsHolder.settings.baseDir
        hgService.logParameters(params)

        IEngine engine = null
        try {
            def json_config = hgService.getJSONConfig(params.get("tenant"))
            def config = hgService.jsonToMap(json_config)

            if( session["params"] == null || params.containsKey("oauth_nonce") && session["params"].get("oauth_nonce") != params.get("oauth_nonce") ) {
                session["params"] = params
            }

            engine = engineFactory.createEngine(request, params, config, hgService.endpoint, session["params"])
            //Object engineClass = engineFactory.getEngineClass(config)
            //log.debug engineClass.ENGINE_CODE
            //def ltiConstants = engine.getToolProvider()
            //log.debug ltiConstants.TOOL_CONSUMER_INFO_PRODUCT_FAMILY_CODE

            def completionResponse = engine.getCompletionResponse()
            if( completionResponse == null ){
                log.debug "ERROR: "
                render(text: hgService.xmlResponse("completionResponse is null"), contentType: "text/xml", encoding: "UTF-8")
            } else {
                if( completionResponse.get("type") == engine.COMPLETION_RESPONSE_TYPE_URL ) {
                    log.info "Redirecting to " + completionResponse
                    redirect(url: completionResponse.get("content"))
                } else if( completionResponse.get("type") == engine.COMPLETION_RESPONSE_TYPE_XML ) {
                    log.info "Rendering XML\n" + completionResponse.get("content")
                    render(text: completionResponse.get("content"), contentType: "text/xml", encoding: "UTF-8")
                } else if( completionResponse.get("type") == engine.COMPLETION_RESPONSE_TYPE_HTML ) {
                    log.info "Rendering HTML [" + completionResponse.get("content") + "]"
                    render(view: completionResponse.get("content"), model: [endpoint_url: engine.getEndpointURL(), data: completionResponse.get("data")])
                } else {
                    String message = "completionResponse not identified. Only actions [url | xml | html] are registered"
                    log.debug "ERROR: " + message
                    if( params.containsKey("launch_presentation_return_url") ) {
                        redirect(url: params.get("launch_presentation_return_url") + "&lti_errormsg=" + URLEncoder.encode(message, "UTF-8"))
                    } else {
                        render(view: "error", model: ['resultMessageKey': 'GeneralError', 'resultMessage': message])
                        flash.error = message
                    }
                }
            }
        } catch (Exception e){
            log.debug "ERROR: " + e.message
            if( params.containsKey("launch_presentation_return_url") ) {
                redirect(url: params.get("launch_presentation_return_url") + "&lti_errormsg=" + e.message)
            } else {
                render(view: "error", model: ['resultMessageKey': 'GeneralError', 'resultMessage': e.message])
                flash.error = e.message
            }
        }
        return
    }
}
