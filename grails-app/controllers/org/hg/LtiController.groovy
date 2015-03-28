package org.hg

import java.util.List

import net.oauth.OAuth

import org.hg.EngineFactory
import org.hg.SimpleEngineFactory
import org.hg.domain.Type
import org.hg.engine.IEngine
import org.hg.engine.Engine
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

            if( session["params"] == null ||
                params.containsKey(Engine.PARAM_ENGINE) && params.get(Engine.PARAM_ENGINE) != Engine.ENGINE_TYPE_LAUNCH ||
                params.containsKey(OAuth.OAUTH_NONCE) && params.get(OAuth.OAUTH_NONCE) != session["params"].get(OAuth.OAUTH_NONCE) ) {
                session["params"] = params
            }

            engine = engineFactory.createEngine(request, params, config, hgService.endpoint, session["params"])
            //Here complete the setting and execute the action

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
