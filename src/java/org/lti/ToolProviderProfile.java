package org.lti;

import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class ToolProviderProfile {
    private static final Logger log = Logger.getLogger(ToolProviderProfile.class);

    JSONObject id;

    String tool_guid;
    String tc_key;
    String tc_secret;
    String tc_profile_url;
    String lti_version;
    Map<String, String> config_product; 
    Map<String, String> config_vendor; 
    
    public ToolProviderProfile(String lti_version, String _tc_key, String _tc_secret, String _tc_profile_url, String config_tool_guid, Map<String, String> config_product, Map<String, String> config_vendor) {
        log.debug("====Creating ToolProviderProfile()");
        this.tool_guid = config_tool_guid;
        String[] tc_profile_url_segments = _tc_profile_url.split("\\?");
        String tc_profile_url = tc_profile_url_segments[0];
        this.tc_profile_url = tc_profile_url;
        this.tc_key = _tc_key;
        this.tc_secret = _tc_secret;
        //String[] lti_version_segments = tc_profile_url_segments[1].split("=");
        this.lti_version = lti_version;
        //this.lti_version = lti_version_segments[1];
        this.config_product = config_product;
        this.config_vendor = config_vendor;
    }
    
    public void setId(){
    }
    
    private JSONArray getContext() {
        JSONArray context = new JSONArray();
        context.put("http://purl.imsglobal.org/ctx/lti/v2/ToolProxy");
        context.put("http://purl.org/blackboard/ctx/v1/iconStyle");
            JSONObject context_tcp = new JSONObject();
            context_tcp.put("tcp", this.tc_profile_url);
        context.put(context_tcp);
        return context;
    }
    
    private JSONObject getToolProfile() {
        JSONObject tool_profile = new JSONObject();
        tool_profile.put("lti_version", this.lti_version);
        tool_profile.put("product_instance", getProductInstance());
        tool_profile.put("base_url_choice", getBaseURLChoice());
        tool_profile.put("resource_handler", getResourceHandler());
        return tool_profile;
    }

    private JSONObject getProductInstance() {
        JSONObject product_instance = new JSONObject();
        product_instance.put("guid", this.tool_guid);
        product_instance.put("product_info", getProductInfo());
        product_instance.put("support", getSupport());
        product_instance.put("service_provider", getServiceProvider());
        //product_instance.put("service_owner", getServiceOwner());
        return product_instance;
    }

    private JSONObject getProductInfo() {
        JSONObject product_info = new JSONObject();
        // Building objects for product_info starts here
            JSONObject product_info_name = new JSONObject();
            product_info_name.put("default_value", this.config_product.get("name"));
            product_info_name.put("key", "tool.name");
        product_info.put("product_name", product_info_name);
            JSONObject product_info_description = new JSONObject();
            product_info_description.put("default_value", this.config_product.get("description"));
            product_info_description.put("key", "tool.description");
        product_info.put("description", product_info_description);
        product_info.put("product_version", "1.0.0");
            JSONObject product_info_technical_description = new JSONObject();
            product_info_technical_description.put("default_value", "Full support for LTI 2.0");
            product_info_technical_description.put("key", "tool.technical");
        product_info.put("technical_description", product_info_technical_description);
        product_info.put("product_family", getProductFamily());
        // Building objects for product_info ends here
        return product_info;
    }

    private JSONObject getProductFamily() {
        JSONObject product_family = new JSONObject();
        product_family.put("@id", this.config_product.get("url"));
        product_family.put("code", this.config_product.get("code"));
        product_family.put("vendor", getVendor());
        return product_family;
    }

    private JSONObject getVendor() {
        JSONObject vendor = new JSONObject();
        vendor.put("code", "hg");
            JSONObject vendor_name = new JSONObject();
            vendor_name.put("default_value", "123IT");
            vendor_name.put("key", "product.vendor.name");
        vendor.put("vendor_name", vendor_name);
            JSONObject vendor_description = new JSONObject();
            vendor_description.put("default_value", "123IT offers consulting services for integrating applications");
            vendor_description.put("key", "product.vendor.description");
        vendor.put("description", vendor_description);
        vendor.put("website", "http://123it.ca");
        Date dt = new Date();
        vendor.put("timestamp", "" + dt.getTime());
            JSONObject vendor_contact = new JSONObject();
            vendor_contact.put("email", "contact@123it.ca");
        vendor.put("contact", vendor_contact);
        return vendor;
    }
    
    private JSONObject getSupport() {
        JSONObject support = new JSONObject();
        support.put("email", "support@123it.ca");
        return support;
    }

    private JSONObject getServiceProvider() {
        JSONObject service_provider = new JSONObject();
        return service_provider;
    }

    private JSONObject getServiceOwner() {
        JSONObject service_owner = new JSONObject();
        return service_owner;
    }

    private JSONArray getBaseURLChoice(){
        JSONArray base_url_choice = new JSONArray();
            JSONObject default_base_url = new JSONObject();
            default_base_url.put("default_base_url", "http://192.168.44.149:8888");
        base_url_choice.put(default_base_url);
        return base_url_choice;
    }

    private JSONArray getResourceHandler() {
        String[] tool_guid_segments = this.tool_guid.split("@");

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
                    message.put("path", "/hg/" + tool_guid_segments[0] + "/launch");
                        JSONArray enabled_capability_array = new JSONArray();
                            enabled_capability_array.put("Context.id");
                            enabled_capability_array.put("CourseSection.label");
                            enabled_capability_array.put("CourseSection.title");
                            enabled_capability_array.put("Membership.role");
                            enabled_capability_array.put("Person.email.primary");
                            enabled_capability_array.put("Person.name.family");
                            enabled_capability_array.put("Person.name.full");
                            enabled_capability_array.put("Person.name.given");
                            enabled_capability_array.put("Person.sourcedId");
                            enabled_capability_array.put("ResourceLink.id");
                            enabled_capability_array.put("ResourceLink.title");
                            enabled_capability_array.put("ResourceLink.sourcedId");
                            enabled_capability_array.put("User.id");
                            enabled_capability_array.put("User.username");
                            enabled_capability_array.put("ToolConsumerProfile.url");
                            enabled_capability_array.put("ToolProxy.custom.url");
                            enabled_capability_array.put("ToolProxyBinding.custom.url");
                            enabled_capability_array.put("LtiLink.custom.url");
                    message.put("enabled_capability", enabled_capability_array);
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
                            JSONObject parameter_cert_given_name = new JSONObject();
                            parameter_cert_given_name.put("name", "cert_given_name");
                            parameter_cert_given_name.put("variable", "Person.name.given");
                        parameter_array.put(parameter_cert_given_name);
                            JSONObject parameter_cert_family_name = new JSONObject();
                            parameter_cert_family_name.put("name", "cert_family_name");
                            parameter_cert_family_name.put("variable", "Person.name.family");
                        parameter_array.put(parameter_cert_family_name);
                            JSONObject parameter_cert_full_name = new JSONObject();
                            parameter_cert_full_name.put("name", "cert_full_name");
                            parameter_cert_full_name.put("variable", "Person.name.full");
                        parameter_array.put(parameter_cert_full_name);
                            JSONObject parameter_cert_email = new JSONObject();
                            parameter_cert_email.put("name", "cert_email");
                            parameter_cert_email.put("variable", "Person.email.primary");
                        parameter_array.put(parameter_cert_email);
                            JSONObject parameter_cert_userid = new JSONObject();
                            parameter_cert_userid.put("name", "cert_userid");
                            parameter_cert_userid.put("variable", "User.id");
                        parameter_array.put(parameter_cert_userid);
                            JSONObject parameter_cert_username = new JSONObject();
                            parameter_cert_username.put("name", "cert_username");
                            parameter_cert_username.put("variable", "User.username");
                        parameter_array.put(parameter_cert_username);
                            JSONObject parameter_roles = new JSONObject();
                            parameter_cert_username.put("name", "roles");
                            parameter_cert_username.put("variable", "Membership.role");
                        parameter_array.put(parameter_roles);
                    message.put("parameter", parameter_array);
                message_array.put(message);
            resource_handler.put("message", message_array);
        resource_handler_array.put(resource_handler);    
        return resource_handler_array;
    }

    private JSONObject getSecurityContract() {
        JSONObject security_contract = new JSONObject();
        security_contract.put("shared_secret", this.tc_secret);

            JSONArray tool_services = new JSONArray();
                JSONObject tool_consumer_profile = new JSONObject();
                tool_consumer_profile.put("@type", "RestServiceProfile");
                    JSONArray tool_consumer_profile_actions = new JSONArray();
                    tool_consumer_profile_actions.put("GET");
                    tool_consumer_profile.put("action", tool_consumer_profile_actions);
                    tool_consumer_profile.put("service", "tcp:ToolConsumerProfile"); //To be replaced
            tool_services.put(tool_consumer_profile);
                JSONObject tool_proxy_collection = new JSONObject();
                tool_proxy_collection.put("@type", "RestServiceProfile");
                    JSONArray tool_proxy_collection_actions = new JSONArray();
                    tool_proxy_collection_actions.put("POST");
                    tool_proxy_collection.put("action", tool_proxy_collection_actions);
                    tool_proxy_collection.put("service", "tcp:ToolProxy.collection"); //To be replaced
            tool_services.put(tool_proxy_collection);
                JSONObject tool_proxy_settings = new JSONObject();
                tool_proxy_settings.put("@type", "RestServiceProfile");
                    JSONArray tool_proxy_settings_actions = new JSONArray();
                    tool_proxy_settings_actions.put("GET");
                    tool_proxy_settings_actions.put("PUT");
                    tool_proxy_settings.put("action", tool_proxy_settings_actions);
                    tool_proxy_settings.put("service", "tcp:ToolProxySettings"); //To be replaced
            tool_services.put(tool_proxy_settings);
                JSONObject tool_proxy_binding_settings = new JSONObject();
                tool_proxy_binding_settings.put("@type", "RestServiceProfile");
                    JSONArray tool_proxy_binding_settings_actions = new JSONArray();
                    tool_proxy_binding_settings_actions.put("GET");
                    tool_proxy_binding_settings_actions.put("PUT");
                    tool_proxy_binding_settings.put("action", tool_proxy_binding_settings_actions);
                    tool_proxy_binding_settings.put("service", "tcp:ToolProxyBindingSettings"); //To be replaced
            tool_services.put(tool_proxy_binding_settings);
                JSONObject lti_link_settings = new JSONObject();
                lti_link_settings.put("@type", "RestServiceProfile");
                    JSONArray lti_link_settings_actions = new JSONArray();
                    lti_link_settings_actions.put("GET");
                    lti_link_settings_actions.put("PUT");
                    lti_link_settings.put("action", lti_link_settings_actions);
                    lti_link_settings.put("service", "tcp:LtiLinkSettings"); //To be replaced
            tool_services.put(lti_link_settings);
        //////////////////////////////////////////////////////////////////////////////////////////
        //// NOTE: The spec says tool_service, Moodle has implemented this using tool_services instead
        //////////////////////////////////////////////////////////////////////////////////////////
        security_contract.put("tool_services", tool_services);
        return security_contract;
    }

    public JSONObject getIMSXJSONMessage() {
        JSONObject imsx_JSONMessage = new JSONObject();

        imsx_JSONMessage.put("@context", getContext());
        imsx_JSONMessage.put("@type", "ToolProxy");
        //////// TODO: Replace @id
        //imsx_JSONMessage.put("@id", this.tc_key + ":" + this.tool_guid);
        //    JSONObject custom = new JSONObject();
        //    custom.put("id", this.tc_key);
        //imsx_JSONMessage.put("custom", custom);
        imsx_JSONMessage.put("lti_version", this.lti_version);
        imsx_JSONMessage.put("tool_consumer_profile", this.tc_profile_url + "?lti_version=" + this.lti_version);
        imsx_JSONMessage.put("tool_profile", getToolProfile());
        imsx_JSONMessage.put("security_contract", getSecurityContract());
        
        return imsx_JSONMessage;
    }
    
}
