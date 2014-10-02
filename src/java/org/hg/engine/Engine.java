package org.hg.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.lti.api.LTIToolProvider;
import org.lti.api.LTIStore;
import org.lti.impl.LTIStoreImpl;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class Engine implements IEngine {
    private static final Logger log = Logger.getLogger(Engine.class);

    protected Map<String, String> params;
    protected Map<String, Object> config;
    protected Map<String, String> grails_params;
    protected String endpoint;

    protected LTIStore ltiStore;
    protected LTIToolProvider tp;
    protected JSONObject tpMeta;
    protected JSONObject tcMeta;

    public Engine(HttpServletRequest request, Map<String, String> params, Map<String, Object> config, String endpoint)
            throws Exception {
        this.config = config;
        this.grails_params = new HashMap<String, String>();
        for( int i=0; i < GRAILS_PARAMS.length; i++ ){
            if( params.containsKey(GRAILS_PARAMS[i]) ){
                grails_params.put(GRAILS_PARAMS[i], params.get(GRAILS_PARAMS[i]));
                params.remove(GRAILS_PARAMS[i]);
            }
        }
        this.params = params;
        this.endpoint = endpoint;

        if( request.getMethod().equals("POST") ) {
            try {
                this.tpMeta = new JSONObject();
                this.tcMeta = new JSONObject();

                ltiStore = LTIStoreImpl.getInstance();
                
                String _endpoint = (request.isSecure()? "https": "http") + "://" + this.endpoint + "/" + this.grails_params.get("application") + "/" + this.grails_params.get("tenant") + "/lti/" + this.grails_params.get("version"); 
                log.debug(_endpoint);

                Map<String, Object> lti_cfg = (Map<String, Object>)config.get("lti");
                this.tp = ltiStore.createToolProvider(_endpoint, (String)lti_cfg.get("key"), (String)lti_cfg.get("secret"), params, "1.0");
                if( !this.tp.hasValidSignature() )
                    throw new Exception("OAuth signature is NOT valid");
                else
                    log.debug("OAuth signature is valid");


                List<Map<String, Object>> profiles = (List<Map<String, Object>>)lti_cfg.get("profiles");
                Map<String, Object> profile = null;
                for( Map<String, Object> _profile : profiles ){
                    log.debug(_profile.get("name"));
                    
                    //Object constants = this.tp.constants; 
                    Class cls;
                    cls = this.tp.getClass();
                    log.debug(cls);
                    log.debug(org.lti.impl.LTIv1p0ToolProvider.TOOL_CONSUMER_INFO_PRODUCT_FAMILY_CODE);
                    Class clsSuper = cls.getSuperclass();
                    log.debug(clsSuper);
                    log.debug(org.lti.impl.LTIv1p0.TOOL_CONSUMER_INFO_PRODUCT_FAMILY_CODE);
                    //log.debug(clsSuper.VERSION);
                    ////log.debug(constants.TOOL_CONSUMER_INFO_PRODUCT_FAMILY_CODE);
                    
                    //org.lti.impl.LTIv1p0 xx = new org.lti.impl.LTIv1p0ToolProvider();
                    //log.debug(xx.VERSION);
                    //org.lti.api.LTIToolProvider yy = new org.lti.impl.LTIv1p0ToolProvider();
                    //log.debug(yy.VERSION);

                    //log.debug(clsSuper.TOOL_CONSUMER_INFO_PRODUCT_FAMILY_CODE);
                    
                    //def ltiConstants = engine.getToolProvider()
                    //log.debug ltiConstants.LIS_OUTCOME_SERVICE_URL

                    //LTIToolProvider ltiConstants = getToolProvider();
                    //log.debug(ltiConstants.TOOL_CONSUMER_INFO_PRODUCT_FAMILY_CODE);

                    if( this.params.get(org.lti.impl.LTIv1p0.TOOL_CONSUMER_INFO_PRODUCT_FAMILY_CODE).equals(_profile.get("name")) ){
                        profile = _profile;
                        log.debug(profile.get("name"));
                        break;
                    }
                }
                if( profile == null )
                    profile = new HashMap<String, Object>();
                
                JSONArray json_override_parameters = new JSONArray((ArrayList<Object>)profile.get("overrides"));
                log.debug(json_override_parameters.toString());
                this.tp.overrideParameters(json_override_parameters);

                JSONArray json_required_parameters = new JSONArray((ArrayList<Object>)profile.get("required"));
                if( !this.tp.hasRequiredParameters(json_required_parameters) )
                    throw new Exception("Missing required parameters");
                else
                    log.debug("All required parameters are included");
                
            } catch( Exception e) {
                throw e;
            }
        }
    }

    protected CompletionResponse completionResponse;

    public Map<String, Object> getConfig(String type) {
        return this.config;
    }

    @Override
    public Map<String, String> getCompletionResponse() {
        return null;
    }

    @Override
    public void setCompletionResponseCommand(CompletionResponse completionResponse) {
    }

    @Override
    public LTIToolProvider getToolProvider() {
        return this.tp;
    }
}
