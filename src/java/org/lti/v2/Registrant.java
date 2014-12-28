package org.lti.v2;

import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONArray;
import org.lti.LTIException;
import org.lti.ToolProvider;
import org.lti.LTIv2;

public class Registrant extends ToolProvider implements LTIv2 {

    private static final Logger log = Logger.getLogger(Registrant.class);

    public Registrant(String endpoint, String key, String secret, Map<String, String> params)
            throws LTIException, Exception {
        super(endpoint, key, secret, params);

        try {
            validateParameters(LTIv2.TOOL_PROXY_REGISTRATION_REQUEST_PARAMETERS_REQUIRED);
            //request the tool consumer profile
            String tc_profile = requestToolConsumerProfile(params.get(LTIv2.TC_PROFILE_URL));
            log.debug("************************");
            log.debug(tc_profile);

            JSONObject tc_profile_json = new JSONObject(tc_profile);
            log.debug("************************");
            log.debug(tc_profile_json);

            JSONObject product_instance_json = tc_profile_json.getJSONObject("product_instance");
            log.debug("************************");
            log.debug(product_instance_json);

            JSONArray services_offered_json = tc_profile_json.getJSONArray("service_offered");
            log.debug("************************");
            log.debug(services_offered_json);

            boolean end_outer_for = false;
            for( int i=0; i < services_offered_json.length() && !end_outer_for; i++ ){
                JSONObject service_json = services_offered_json.getJSONObject(i);
                log.debug("************************ service_json");
                log.debug(service_json);
                JSONArray formats = service_json.getJSONArray("format");
                for( int j=0; j < formats.length(); j++ ){
                    String format = formats.getString(j);
                    log.debug(format);
                    if( "application/vnd.ims.lti.v2.toolproxy+json".equals(format) ){
                        log.debug("Execute call to " + service_json.getString("endpoint"));
                        String proxy_registration_response = registerProxy(service_json.getString("endpoint"), params);
                        log.debug("************************ proxy_registration_response");
                        log.debug(proxy_registration_response);
                        end_outer_for = true;
                        break;
                    }
                }
            }
            log.debug("------------------------------------------------------------------------");

        } catch (Exception e) {
            log.debug("Valio madre, hay un error");
            throw new LTIException(LTIException.MESSAGEKEY_MISSING_PARAMETERS, "LTI version " + LTIv2.VERSION + " parameters not included. " + e.getMessage());
        }
    }

    public String getLTIVersion(){
        return LTIv2.VERSION;
    }

    public String getLTILaunchPresentationReturnURL(){
        return this.params.get(LAUNCH_PRESENTATION_RETURN_URL);
    }

    protected String requestToolConsumerProfile(String query) 
        throws LTIException{
        String response = "";
        response = getToolConsumerProfile(query).toString();
        return response;
    }
    
    protected String registerProxy(String query, Map<String, String> params) 
            throws LTIException{
        log.debug("registering Proxy");
        String regKey = params.get(LTIv2.REG_KEY);
        String regPassword = params.get(LTIv2.REG_PASSWORD);
        String response = "";
        JSONObject message = getRegistrationJSONMessage(params);
        response = executeProxyRegistration(query, regKey, regPassword, message.toString());
        return response;
    }

    private JSONObject getRegistrationJSONMessage(Map<String, String> params) {
        JSONObject imsx_JSONRequest = new JSONObject();

        String tc_profile_url = params.get(LTIv2.TC_PROFILE_URL);
        log.debug("--------------> " + tc_profile_url);
        String[] tc_profile_url_segments = tc_profile_url.split("\\?");
        log.debug("--------------> " + tc_profile_url_segments);
        tc_profile_url = tc_profile_url_segments[0];
        log.debug("--------------> " + tc_profile_url);

            JSONArray context = new JSONArray();
            context.put("http://purl.imsglobal.org/ctx/lti/v2/ToolProxy");
            context.put("http://purl.org/blackboard/ctx/v1/iconStyle");
                JSONObject context_tcp = new JSONObject();
                context_tcp.put("tcp", tc_profile_url);
            context.put(context_tcp);
        imsx_JSONRequest.put("@context", context);
        imsx_JSONRequest.put("@type", "ToolProxy");
        imsx_JSONRequest.put("@id", "xxxx-yyyy-zzzz");
        imsx_JSONRequest.put("lti_version", "LTI-2p0");
        imsx_JSONRequest.put("tool_consumer_profile", tc_profile_url + "?lti_version=LTI-2p0");
            JSONObject tool_profile = new JSONObject();
            tool_profile.put("lti_version", "LTI-2p0");
                //Prepare product_instance
                JSONObject product_instance = new JSONObject();
                product_instance.put("guid", "192.168.44.149");
                    JSONObject product_info = new JSONObject();
                        JSONObject product_info_name = new JSONObject();
                        product_info_name.put("default_value", "HG Test");
                        product_info_name.put("key", "tool.name");
                    product_info.put("product_name", product_info_name);
                        JSONObject product_info_description = new JSONObject();
                        product_info_description.put("default_value", "HG is a broker that enables web application to act as LTI 2.0 tool providers");
                        product_info_description.put("key", "tool.description");
                    product_info.put("description", product_info_description);
                    product_info.put("product_version", "1.0.0");
                        JSONObject product_info_technical_description = new JSONObject();
                        product_info_technical_description.put("default_value", "Full support for LTI 2.0 tool providers");
                        product_info_technical_description.put("key", "tool.technical");
                    product_info.put("technical_description", product_info_technical_description);
                        JSONObject product_family = new JSONObject();
                        product_family.put("@id", "http://hg.123it.ca/vendor/product/hg_bigbluebutton");
                        product_family.put("code", "hg_bigbluebutton");
                            JSONObject vendor = new JSONObject();
                            vendor.put("code", "hg");
                                JSONObject vendor_name = new JSONObject();
                                vendor_name.put("default_value", "123IT");
                                vendor_name.put("key", "product.vendor.name");
                            vendor.put("vendor_name", vendor_name);
                                JSONObject vendor_description = new JSONObject();
                                vendor_description.put("default_value", "123IT offers consulting services for integrationg applications");
                                vendor_description.put("key", "product.vendor.description");
                            vendor.put("description", vendor_description);
                            vendor.put("website", "http://123it.ca");
                            Date dt = new Date();
                            vendor.put("timestamp", "" + dt.getTime());
                                JSONObject vendor_contact = new JSONObject();
                                vendor_contact.put("email", "contact@123it.ca");
                            vendor.put("contact", vendor_contact);
                        product_family.put("vendor", vendor);
                    product_info.put("product_family", product_family);
                product_instance.put("product_info", product_info);
                    JSONObject support = new JSONObject();
                    support.put("email", "support@123it.ca");
                product_instance.put("support", support);
                product_instance.put("service_provider", new JSONObject());
                product_instance.put("service_owner", new JSONObject());
            tool_profile.put("product_instance", product_instance);
            //Prepare base_url_choice
                JSONArray base_url_choice = new JSONArray();
                    JSONObject default_base_url = new JSONObject();
                    default_base_url.put("default_base_url", "http://192.168.44.149:8888");
                base_url_choice.put(default_base_url);
            tool_profile.put("base_url_choice", base_url_choice);
            //Prepare resource_handler
                JSONArray resource_handler_array = new JSONArray();
                    JSONObject resource_handler = new JSONObject();
                        JSONObject resource_handler_resource_type = new JSONObject();
                        resource_handler_resource_type.put("code", "sso");
                    resource_handler.put("resource_type", resource_handler_resource_type);
                        JSONObject resource_handler_resource_name = new JSONObject();
                        resource_handler_resource_name.put("default_value", "BigBlueButton Rooms");
                        resource_handler_resource_name.put("key", "sso.resource.name");
                    resource_handler.put("resource_name", resource_handler_resource_name);
                        JSONObject resource_handler_description = new JSONObject();
                        resource_handler_description.put("default_value", "BigBlueButton Rooms allows to create and use rooms");
                        resource_handler_description.put("key", "sso.resource.description");
                    resource_handler.put("description", resource_handler_description);
                        JSONArray message_array = new JSONArray();
                            JSONObject message = new JSONObject();
                            message.put("message_type", "basic-lti-launch-request");
                            message.put("path", "/hg/1/launch/v2p0");
                            message.put("enabled_capability", new JSONArray());
                                JSONArray parameter_array = new JSONArray();
                                    JSONObject parameter_system_setting_url = new JSONObject();
                                    parameter_system_setting_url.put("name", "system_setting_url");
                                    parameter_system_setting_url.put("variable", "ToolProxy.custom.url");
                                parameter_array.put(parameter_system_setting_url);
                                    JSONObject parameter_context_setting_url = new JSONObject();
                                    parameter_context_setting_url.put("name", "context_setting_url");
                                    parameter_context_setting_url.put("variable", "ToolProxyBinding.custom.url");
                                parameter_array.put(parameter_context_setting_url);
                                    JSONObject parameter_link_setting_url = new JSONObject();
                                    parameter_link_setting_url.put("name", "link_setting_url");
                                    parameter_link_setting_url.put("variable", "LtiLink.custom.url");
                                parameter_array.put(parameter_link_setting_url);
                                    JSONObject parameter_tc_profile_url = new JSONObject();
                                    parameter_tc_profile_url.put("name", "tc_profile_url");
                                    parameter_tc_profile_url.put("variable", "ToolConsumerProfile.url");
                                parameter_array.put(parameter_tc_profile_url);
                                    JSONObject parameter_cert_full_name = new JSONObject();
                                    parameter_cert_full_name.put("name", "cert_full_name");
                                    parameter_cert_full_name.put("variable", "Person.name.full");
                                parameter_array.put(parameter_cert_full_name);
                                    JSONObject parameter_cert_userid = new JSONObject();
                                    parameter_cert_userid.put("name", "cert_userid");
                                    parameter_cert_userid.put("variable", "User.id");
                                parameter_array.put(parameter_cert_userid);
                                    JSONObject parameter_cert_username = new JSONObject();
                                    parameter_cert_username.put("name", "cert_username");
                                    parameter_cert_username.put("variable", "User.name");
                                parameter_array.put(parameter_cert_username);
                            message.put("parameter", parameter_array);
                        message_array.put(message);
                    resource_handler.put("message", message_array);
                resource_handler_array.put(resource_handler);    
            tool_profile.put("resource_handler", resource_handler_array);
        imsx_JSONRequest.put("tool_profile", tool_profile);
            JSONObject security_contract = new JSONObject();
            security_contract.put("shared_secret", "secret");
        imsx_JSONRequest.put("security_contract", security_contract);

        log.debug("+++++++++++++++++++++++++++++++++++++++");
        log.debug(imsx_JSONRequest);
        log.debug("+++++++++++++++++++++++++++++++++++++++");
        return imsx_JSONRequest;
    }


}
