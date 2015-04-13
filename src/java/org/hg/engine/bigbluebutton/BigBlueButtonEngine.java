package org.hg.engine.bigbluebutton;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.oauth.OAuth;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.bigbluebutton.api.BBBProxy;
import org.hg.EngineFactory;
import org.hg.engine.CompletionResponse;
import org.hg.engine.Engine;
import org.lti.LTI;
import org.lti.RolesValidator;
import org.lti.ToolProviderProfile;

public class BigBlueButtonEngine extends Engine {
    private static final Logger log = Logger.getLogger(BigBlueButtonEngine.class.getName());

    public static final String ENGINE_CODE = EngineFactory.ENGINE_BIGBLUEBUTTON;
    public static final String ENGINE_NAME = "BigBlueButton";
    public static final String ENGINE_DESCRIPTION = "Open source web conferencing system for distance learning.";
    public static final String ENGINE_URL = "http://www.bigbluebutton.org/";
    public static final String ENGINE_CONTACT_EMAIL = "bigbluebutton-users@googlegroups.com";

    public static final String PARAM_CUSTOM_RECORD          = "custom_record";
    public static final String PARAM_CUSTOM_DURATION        = "custom_duration";
    public static final String PARAM_CUSTOM_VOICEBRIDGE     = "custom_voicebridge";
    public static final String PARAM_CUSTOM_WELCOME         = "custom_welcome";

    public static final String PARAM_BBB_RECORDING_ID       = "bbb_recording_id";
    public static final String PARAM_BBB_RECORDING_PUBLISHED= "bbb_recording_published";

    public static final String BBB_CMD_MEETING_JOIN         = "join";
    public static final String BBB_CMD_RECORDING_PUBLISH    = "publish";
    public static final String BBB_CMD_RECORDING_UNPUBLISH  = "unpublish";
    public static final String BBB_CMD_RECORDING_DELETE     = "delete";

    public static final String BBB_ROLE_MODERATOR   = "moderator";
    public static final String BBB_ROLE_VIEWER      = "viewer";

    public BigBlueButtonEngine(HttpServletRequest request, Map<String, String> params, Map<String, Object> config, String endpoint, Map<String, String> session_params)
        throws Exception {
        super(request, params, config, endpoint, session_params);
        log.debug("====== Creating object::BigBlueButtonEngine()");
        if ( this.grails_params.get(PARAM_ENGINE).equals(ENGINE_TYPE_CONFIG) ) {
            if(this.grails_params.get(PARAM_ACT).equals(ENGINE_ACT_CC)){
                Map<String, Object> definition = new HashMap<String, Object>();
                
                @SuppressWarnings("unchecked")
                Map<String, String> config_vendor = (Map<String, String>)this.config.get("vendor"); 
                definition.put("title", config_vendor.get("name"));
                definition.put("description", config_vendor.get("description"));

                String launch_url_path = grails_params.get(PARAM_APPLICATION) + "/" + grails_params.get(PARAM_TENANT) + "/" + ENGINE_TYPE_LAUNCH; 
                definition.put( "launch_url", "http://" + endpoint + "/" + launch_url_path );
                definition.put( "secure_launch_url", "https://" + endpoint + "/" + launch_url_path );

                String icon_path = grails_params.get(PARAM_APPLICATION) + "/" + grails_params.get(PARAM_TENANT) + "/" + ENGINE_TYPE_RESOURCE + "/?a=ico";
                definition.put( "icon", "http://" + endpoint + "/" + icon_path );
                definition.put( "secure_icon", "https://" + endpoint + "/" + icon_path );

                @SuppressWarnings("unchecked")
                Map<String, String> config_product = (Map<String, String>)this.config.get("vendor"); 
                String vendor_code = config_product.get("code");
                definition.put( "vendor_code", (vendor_code != null && !vendor_code.equals(""))? vendor_code: ENGINE_CODE );
                String vendor_name = config_product.get("name");
                definition.put( "vendor_name", (vendor_name != null && !vendor_name.equals(""))? vendor_name: ENGINE_NAME );
                String vendor_description = config_product.get("description");
                definition.put( "vendor_description", (vendor_description != null && !vendor_description.equals(""))? vendor_description: ENGINE_DESCRIPTION );
                String vendor_url = config_product.get("url");
                definition.put( "vendor_url", (vendor_url != null && !vendor_url.equals(""))? vendor_url: ENGINE_URL );
                String vendor_contact_email = config_product.get("contact");
                definition.put( "vendor_contact_email", (vendor_contact_email != null && !vendor_contact_email.equals(""))? vendor_contact_email: ENGINE_CONTACT_EMAIL );

                setCompletionResponseCommand( new CommonCartridgeXML(definition) );
            }
        } else if( this.grails_params.get(PARAM_ENGINE).equals(ENGINE_TYPE_LAUNCH) ) {
                @SuppressWarnings("unchecked")
                Map<String, String> config_engine = (Map<String, String>)config.get("engine");
                log.debug("HERE");
                this.tpn.InitToolProvider();

                Map<String, String> bbb_meeting_params = getBBBMeetingParams();
                log.debug(bbb_meeting_params);
                Map<String, String> bbb_session_params = getBBBSessionParams();
                log.debug(bbb_session_params);

                if( this.params.containsKey(PARAM_CUSTOM_RECORD) && Boolean.parseBoolean(this.params.get(PARAM_CUSTOM_RECORD)) ){
                    if( this.grails_params.containsKey(PARAM_ACT) && this.grails_params.get(PARAM_ACT).equals(ENGINE_ACT_UI) ){
                        if( this.grails_params.containsKey(PARAM_CMD) && this.grails_params.get(PARAM_CMD).equals(BBB_CMD_MEETING_JOIN) ) {
                            setCompletionResponseCommand( new SingleSignOnURL(config_engine, bbb_meeting_params, bbb_session_params) );
                        } else if( this.grails_params.containsKey(PARAM_CMD) && this.grails_params.get(PARAM_CMD).equals(BBB_CMD_RECORDING_PUBLISH) ) {
                            this.tpn.putParameter(PARAM_BBB_RECORDING_ID, params.get(PARAM_BBB_RECORDING_ID));
                            this.tpn.putParameter(PARAM_BBB_RECORDING_PUBLISHED, params.get(PARAM_BBB_RECORDING_PUBLISHED));
                            setCompletionResponseCommand( new PublishRecording(config_engine, bbb_meeting_params, bbb_session_params, getRecordingParams()) );
                        } else if( this.grails_params.containsKey(PARAM_CMD) && this.grails_params.get(PARAM_CMD).equals(BBB_CMD_RECORDING_UNPUBLISH) ) {
                            this.tpn.putParameter(PARAM_BBB_RECORDING_ID, params.get(PARAM_BBB_RECORDING_ID));
                            this.tpn.putParameter(PARAM_BBB_RECORDING_PUBLISHED, params.get(PARAM_BBB_RECORDING_PUBLISHED));
                            setCompletionResponseCommand( new UnpublishRecording(config_engine, bbb_meeting_params, bbb_session_params, getRecordingParams()) );
                        } else if( this.grails_params.containsKey(PARAM_CMD) && this.grails_params.get(PARAM_CMD).equals(BBB_CMD_RECORDING_DELETE) ) {
                            this.tpn.putParameter(PARAM_BBB_RECORDING_ID, params.get(PARAM_BBB_RECORDING_ID));
                            this.tpn.putParameter(PARAM_BBB_RECORDING_PUBLISHED, params.get(PARAM_BBB_RECORDING_PUBLISHED));
                            setCompletionResponseCommand( new DeleteRecording(config_engine, bbb_meeting_params, bbb_session_params, getRecordingParams()) );
                        }
                    } else {
                        setCompletionResponseCommand( new UI(config_engine, bbb_meeting_params, bbb_session_params) );
                        this.tpn.executeActionService();
                    }

                } else {
                    setCompletionResponseCommand( new SingleSignOnURL(config_engine, bbb_meeting_params, bbb_session_params) );
                    this.tpn.executeActionService();
                }

        } else if( this.grails_params.get(PARAM_ENGINE).equals(ENGINE_TYPE_REGISTRATION) ){
            if( this.tpn.getLTIVersion().equals(LTI.VERSION_V2P0) ) {
                this.tpn.setToolProviderProfile(buildToolProviderProfile());
                this.tpn.InitToolProvider();

                setCompletionResponseCommand( new RegistrationURL(this.tpn) );
                this.tpn.executeActionService();
            }
        }
    }

    @Override
    public Map<String, Object> getCompletionResponse()
            throws Exception {
        return completionResponse.get();
    }

    @Override
    public void setCompletionResponseCommand(CompletionResponse completionResponse) {
        this.completionResponse = completionResponse;
    }

    private Map<String, String> getBBBMeetingParams(){
        Map<String, String> params = this.tpn.getParameters();
        Map<String, String> meetingParams = new HashMap<String, String>();
        // Map ToolProvider parameters with Meeting parameters
        String oauth_consumer_key = this.params.get(OAuth.OAUTH_CONSUMER_KEY);
        String resource_link_id = this.params.get(LTI.RESOURCE_LINK_ID);
        String resource_link_title = this.params.get(LTI.RESOURCE_LINK_TITLE);
        meetingParams.put(BBBProxy.PARAM_NAME, getValidatedMeetingName(resource_link_title));
        meetingParams.put(BBBProxy.PARAM_MEETING_ID, getValidatedMeetingId(resource_link_id, oauth_consumer_key));
        meetingParams.put(BBBProxy.PARAM_ATTENDEE_PW, DigestUtils.shaHex("ap" + resource_link_id + oauth_consumer_key));
        meetingParams.put(BBBProxy.PARAM_MODERATOR_PW, DigestUtils.shaHex("mp" + resource_link_id + oauth_consumer_key));
        try {
            meetingParams.put(BBBProxy.PARAM_WELCOME, URLEncoder.encode(params.containsKey(PARAM_CUSTOM_WELCOME)? params.get(PARAM_CUSTOM_WELCOME): "Welcome to <b>" + params.get("resource_link_title") + "</b>", "UTF-8") );
        } catch (UnsupportedEncodingException e) {
            log.debug("Error encoding meetingName: " + e.getMessage());
            meetingParams.put(BBBProxy.PARAM_WELCOME, "");
        }
        meetingParams.put(BBBProxy.PARAM_VOICE_BRIDGE, params.containsKey(PARAM_CUSTOM_VOICEBRIDGE)? params.get(PARAM_CUSTOM_VOICEBRIDGE): "0");
        if(params.containsKey(PARAM_CUSTOM_RECORD)){
            meetingParams.put(BBBProxy.PARAM_RECORD, Boolean.valueOf(params.get(PARAM_CUSTOM_RECORD)).toString());
            try {
                meetingParams.put(BBBProxy.PARAM_WELCOME, meetingParams.get(BBBProxy.PARAM_WELCOME) + URLEncoder.encode("<br><br>This meeting is being recorded", "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                log.debug("Error encoding meetingName: " + e.getMessage());
            }
        }
        if(params.containsKey(PARAM_CUSTOM_DURATION)){
            meetingParams.put(BBBProxy.PARAM_DURATION, params.get(PARAM_CUSTOM_DURATION));
            try {
                meetingParams.put(BBBProxy.PARAM_WELCOME, meetingParams.get(BBBProxy.PARAM_WELCOME) + URLEncoder.encode("<br><br>The maximum duration for this meeting is ", "UTF-8") + params.get("extra_duration") + URLEncoder.encode(" minutes.", "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                log.debug("Error encoding meetingName: " + e.getMessage());
            }
        }
        if(params.containsKey("launch_presentation_return_url"))
            meetingParams.put(BBBProxy.PARAM_LOGOUT_URL, getValidatedLogoutURL(params.get("launch_presentation_return_url")));

        return meetingParams;
    }

    private Map<String, String> getBBBSessionParams(){
        Map<String, String> sessionParams = new HashMap<String, String>();
        // Map LtiUser parameters with Session parameters
        String oauth_consumer_key = this.params.get(OAuth.OAUTH_CONSUMER_KEY);
        String resource_link_id = this.params.get(LTI.RESOURCE_LINK_ID);
        sessionParams.put(BBBProxy.PARAM_FULL_NAME, getValidatedUserFullName());
        sessionParams.put(BBBProxy.PARAM_MEETING_ID, getValidatedMeetingId(resource_link_id, oauth_consumer_key));
        log.debug("It is null or empty");
        String roles = tpn.getVerifiedRoles();
        log.debug("Roles=" + roles);
        if( roles == null || roles.equals("") || RolesValidator.isStudent(roles) || RolesValidator.isLearner(roles) ) {
            sessionParams.put(BBBProxy.PARAM_PASSWORD, DigestUtils.shaHex("ap" + resource_link_id + oauth_consumer_key));
            sessionParams.put("role", BBB_ROLE_VIEWER);
        } else {
            sessionParams.put(BBBProxy.PARAM_PASSWORD, DigestUtils.shaHex("mp" + resource_link_id + oauth_consumer_key));
            sessionParams.put("role", BBB_ROLE_MODERATOR);
        }
        ////sessionParams.put("createTime", "");
        sessionParams.put(BBBProxy.PARAM_USER_ID, (roles == null || roles.equals(""))? "": DigestUtils.shaHex( tpn.getVerifiedUserId() + oauth_consumer_key) );

        return sessionParams;
    }

    private String getValidatedMeetingName(String _meetingName){
        String meetingName;
        meetingName = (_meetingName == null || _meetingName == "")? "Meeting": _meetingName; 
        try {
            meetingName = URLEncoder.encode(meetingName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.debug("Error encoding meetingName: " + e.getMessage());
            meetingName = "Meeting";
        }
        return meetingName;
    }

    private String getValidatedMeetingId(String resourceId, String consumerId){
        return DigestUtils.shaHex(resourceId + consumerId);
    }

    private String getValidatedLogoutURL(String logoutURL){
        try {
            URL url = new URL(logoutURL);
            URLConnection conn = url.openConnection();
            conn.connect();
            return URLEncoder.encode(logoutURL, "UTF-8");
        } catch (MalformedURLException e) {
            // the URL is not in a valid form
        } catch (IOException e) {
            // the connection couldn't be established
        } catch (Exception e) {
            // the URL could not be URLEncoded
        }
        return "";
    }

    private String getValidatedUserFullName(){
        String userFullName;

        userFullName = tpn.getVerifiedUserFullName();
        if( userFullName == null || userFullName == "" ) {
            log.debug("It is null or empty");
            String roles = tpn.getVerifiedRoles();
            log.debug("Roles=" + roles);
            userFullName = ( roles.equals("") || RolesValidator.isStudent(roles, true) || RolesValidator.isLearner(roles, true) )? "Viewer" : "Moderator";
            log.debug("Now it is " + userFullName);
        }
        try {
            userFullName = URLEncoder.encode(userFullName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.debug("Error encoding userFullName: " + e.getMessage());
            userFullName = "User";
        }

        return userFullName;
    }

    private Map<String, String> getRecordingParams(){
        Map<String, String> params = this.tpn.getParameters();
        Map<String, String> recordingParams = new HashMap<String, String>();

        recordingParams.put(BBBProxy.PARAM_RECORD_ID, params.get(PARAM_BBB_RECORDING_ID));
        recordingParams.put(BBBProxy.PARAM_PUBLISH, Boolean.toString(!Boolean.parseBoolean(params.get(PARAM_BBB_RECORDING_PUBLISHED))) );

        return recordingParams;
    }

    private ToolProviderProfile buildToolProviderProfile() {
        log.debug("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        log.debug(this.endpoint);
        log.debug(this.config);
        log.debug(this.grails_params);
        log.debug(this.params);
        
        String tc_key = this.tpn.getToolConsumerKey();
        String tc_secret = this.tpn.getToolConsumerSecret();
        String tc_profile_url = this.tpn.getToolConsumerProfile();
        String lti_version = this.tpn.getLTIVersion();
        //String config_tool_guid = (String)this.config.get("id") + "@" + this.endpoint;
        String config_tool_guid = this.grails_params.get(PARAM_TENANT) + "@" + this.endpoint;
        @SuppressWarnings("unchecked")
        Map<String, String> config_product = (Map<String, String>)this.config.get("product"); 
        @SuppressWarnings("unchecked")
        Map<String, String> config_vendor = (Map<String, String>)this.config.get("vendor"); 
        log.debug(tc_profile_url);
        log.debug(config_tool_guid);
        log.debug(config_product);
        log.debug(config_vendor);
        ToolProviderProfile tp_profile = new ToolProviderProfile(lti_version, tc_key, tc_secret, tc_profile_url, config_tool_guid, config_product, config_vendor);

        log.debug("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        log.debug(tp_profile.getIMSXJSONMessage().toString());
        tp_profile.setId();
        return tp_profile;
    }

}
