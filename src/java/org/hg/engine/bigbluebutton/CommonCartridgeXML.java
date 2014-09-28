package org.hg.engine.bigbluebutton;

import java.util.Map;
import java.util.HashMap;

import org.hg.engine.CompletionResponse;

public class CommonCartridgeXML implements CompletionResponse {
    protected Map<String, String> definition;

    public CommonCartridgeXML(Map<String, String> definition){
        this.definition = definition;
    }

    public Map<String, String> get() {
        Map<String, String> completionResponse = new HashMap<String, String>();

        completionResponse.put("type", "xml");
        completionResponse.put("content", getCommonCartridgeXML());

        return completionResponse;
    }
    
    private String getCommonCartridgeXML(){
        String commonCartridge = "" +
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<cartridge_basiclti_link xmlns=\"http://www.imsglobal.org/xsd/imslticc_v1p0\"\n" +
        "       xmlns:blti = \"http://www.imsglobal.org/xsd/imsbasiclti_v1p0\"\n" +
        "       xmlns:lticm =\"http://www.imsglobal.org/xsd/imslticm_v1p0\"\n" +
        "       xmlns:lticp =\"http://www.imsglobal.org/xsd/imslticp_v1p0\"\n" +
        "       xmlns:xsi = \"http://www.w3.org/2001/XMLSchema-instance\"\n" +
        "       xsi:schemaLocation = \"http://www.imsglobal.org/xsd/imslticc_v1p0 http://www.imsglobal.org/xsd/lti/ltiv1p0/imslticc_v1p0.xsd\n" +
        "                             http://www.imsglobal.org/xsd/imsbasiclti_v1p0 http://www.imsglobal.org/xsd/lti/ltiv1p0/imsbasiclti_v1p0.xsd\n" +
        "                             http://www.imsglobal.org/xsd/imslticm_v1p0 http://www.imsglobal.org/xsd/lti/ltiv1p0/imslticm_v1p0.xsd\n" +
        "                             http://www.imsglobal.org/xsd/imslticp_v1p0 http://www.imsglobal.org/xsd/lti/ltiv1p0/imslticp_v1p0.xsd\">\n" +
        "    <blti:title>" + this.definition.get("title") + "</blti:title>\n" +
        "    <blti:description>" + this.definition.get("description") + "</blti:description>\n" +
        "    <blti:launch_url>" + this.definition.get("launch_url") + "</blti:launch_url>\n" +
        "    <blti:secure_launch_url>" + this.definition.get("secure_launch_url") + "</blti:secure_launch_url>\n" +
        "    <blti:icon>" + this.definition.get("icon") + "</blti:icon>\n" +
        "    <blti:secure_icon>" + this.definition.get("secure_icon") + "</blti:secure_icon>\n" +
        "    <blti:vendor>\n" +
        "        <lticp:code>" + this.definition.get("vendor_code") + "</lticp:code>\n" +
        "        <lticp:name>" + this.definition.get("vendor_name") + "</lticp:name>\n" +
        "        <lticp:description>" + this.definition.get("vendor_description") + "</lticp:description>\n" +
        "        <lticp:url>" + this.definition.get("vendor_url") + "</lticp:url>\n" +
        "        <lticp:contact>\n" +
        "            <lticp:email>" + this.definition.get("vendor_contact_email") + "</lticp:email>\n" +
        "        </lticp:contact>\n" +
        "    </blti:vendor>\n" +
        "    <cartridge_bundle identifierref=\"BLTI001_Bundle\"/>\n" +
        "    <cartridge_icon identifierref=\"BLTI001_Icon\"/>\n" +
        "</cartridge_basiclti_link>";
        return commonCartridge;
    }

}
