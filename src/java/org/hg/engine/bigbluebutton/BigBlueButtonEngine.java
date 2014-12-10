package org.hg.engine.bigbluebutton;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.hg.EngineFactory;
import org.hg.engine.CompletionResponse;
import org.hg.engine.Engine;
import org.lti.api.LTIRoles;

public class BigBlueButtonEngine extends Engine {
    private static final Logger log = Logger.getLogger(BigBlueButtonEngine.class);

    public static final String ENGINE_CODE = EngineFactory.ENGINE_BIGBLUEBUTTON;
    public static final String ENGINE_NAME = "BigBlueButton";
    public static final String ENGINE_DESCRIPTION = "Open source web conferencing system for distance learning.";
    public static final String ENGINE_URL = "http://www.bigbluebutton.org/";
    public static final String ENGINE_CONTACT_EMAIL = "bigbluebutton-users@googlegroups.com";

    public static final String PARAM_CUSTOM_RECORD      = "custom_record";
    public static final String PARAM_CUSTOM_DURATION    = "custom_duration";
    public static final String PARAM_CUSTOM_VOICEBRIDGE = "custom_voicebridge";
    public static final String PARAM_CUSTOM_WELCOME     = "custom_welcome";

    public static final String BBB_CMD_MEETING_JOIN           = "join";
    public static final String BBB_CMD_RECORDING_PUBLISH      = "publish";
    public static final String BBB_CMD_RECORDING_UNPUBLISH    = "unpublish";
    public static final String BBB_CMD_RECORDING_DELETE       = "delete";

    public static final String BBB_ROLE_MODERATOR   = "moderator";
    public static final String BBB_ROLE_VIEWER      = "viewer";

    public BigBlueButtonEngine(HttpServletRequest request, Map<String, String> params, Map<String, Object> config, String endpoint, Map<String, String> session_params)
        throws Exception {
        super(request, params, config, endpoint, session_params);
        log.debug("instantiate BigBlueButtonEngine()");

        if(this.grails_params.get(PARAM_ACT).equals(ENGINE_ACT_CC)){
            Map<String, Object> definition = new HashMap<String, Object>();
            definition.put("title", (String)config.get("title"));
            definition.put("description", (String)config.get("description"));

            String launch_url_path = grails_params.get("application") + "/" + grails_params.get("tenant") + "/" + ENGINE_TYPE_LAUNCH + "/" + grails_params.get("version"); 
            definition.put( "launch_url", "http://" + endpoint + "/" + launch_url_path );
            definition.put( "secure_launch_url", "https://" + endpoint + "/" + launch_url_path );

            String icon_path = grails_params.get("application") + "/" + grails_params.get("tenant") + "/" + ENGINE_TYPE_RESOURCE + "/v1/?a=ico";
            definition.put( "icon", "http://" + endpoint + "/" + icon_path );
            definition.put( "secure_icon", "https://" + endpoint + "/" + icon_path );

            @SuppressWarnings("unchecked")
            Map<String, Object> vendor = (Map<String, Object>)config.get("vendor");
            String vendor_code = (String)vendor.get("code");
            definition.put( "vendor_code", (vendor_code != null && !vendor_code.equals(""))? vendor_code: ENGINE_CODE );
            String vendor_name = (String)vendor.get("name");
            definition.put( "vendor_name", (vendor_name != null && !vendor_name.equals(""))? vendor_name: ENGINE_NAME );
            String vendor_description = (String)vendor.get("description");
            definition.put( "vendor_description", (vendor_description != null && !vendor_description.equals(""))? vendor_description: ENGINE_DESCRIPTION );
            String vendor_url = (String)vendor.get("url");
            definition.put( "vendor_url", (vendor_url != null && !vendor_url.equals(""))? vendor_url: ENGINE_URL );
            String vendor_contact_email = (String)vendor.get("contact");
            definition.put( "vendor_contact_email", (vendor_contact_email != null && !vendor_contact_email.equals(""))? vendor_contact_email: ENGINE_CONTACT_EMAIL );

            setCompletionResponseCommand( new CommonCartridgeXML(definition) );
        } else {
            @SuppressWarnings("unchecked")
            Map<String, String> engine = (Map<String, String>)config.get("engine");

            if( this.params.containsKey(PARAM_CUSTOM_RECORD) && Boolean.parseBoolean(this.params.get(PARAM_CUSTOM_RECORD)) ){
                if( this.grails_params.containsKey(PARAM_ACT) && this.grails_params.get(PARAM_ACT).equals(ENGINE_ACT_UI) ){
                    if( this.grails_params.containsKey(PARAM_CMD) && this.grails_params.get(PARAM_CMD).equals(BBB_CMD_MEETING_JOIN) ) {
                        setCompletionResponseCommand( new SingleSignOnURL(engine, getMeetingParams(), getSessionParams()) );
                    } else if( this.grails_params.containsKey(PARAM_CMD) && this.grails_params.get(PARAM_CMD).equals(BBB_CMD_RECORDING_PUBLISH) ) {
                        setCompletionResponseCommand( new PublishRecording(engine, getMeetingParams(), getSessionParams(), getRecordingParams()) );
                    } else if( this.grails_params.containsKey(PARAM_CMD) && this.grails_params.get(PARAM_CMD).equals(BBB_CMD_RECORDING_UNPUBLISH) ) {
                        setCompletionResponseCommand( new UnpublishRecording(engine, getMeetingParams(), getSessionParams(), getRecordingParams()) );
                    } else if( this.grails_params.containsKey(PARAM_CMD) && this.grails_params.get(PARAM_CMD).equals(BBB_CMD_RECORDING_DELETE) ) {
                        setCompletionResponseCommand( new DeleteRecording(engine, getMeetingParams(), getSessionParams()) );
                    }
                } else {
                    setCompletionResponseCommand( new UI(engine, getMeetingParams(), getSessionParams()) );
                }
            } else {
                setCompletionResponseCommand( new SingleSignOnURL(engine, getMeetingParams(), getSessionParams()) );
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

    private Map<String, String> getMeetingParams(){
        Map<String, String> params = tp.getParameters();
        Map<String, String> meetingParams = new HashMap<String, String>();
        // Map ToolProvider parameters with Meeting parameters
        meetingParams.put("name", getValidatedMeetingName(params.get("resource_link_title")));
        meetingParams.put("meetingID", getValidatedMeetingId(params.get("resource_link_id"), params.get("oauth_consumer_key")));
        meetingParams.put("attendeePW", DigestUtils.shaHex("ap" + params.get("resource_link_id") + params.get("oauth_consumer_key")));
        meetingParams.put("moderatorPW", DigestUtils.shaHex("mp" + params.get("resource_link_id") + params.get("oauth_consumer_key")));
        try {
            meetingParams.put("welcome", URLEncoder.encode(params.containsKey(PARAM_CUSTOM_WELCOME)? params.get(PARAM_CUSTOM_WELCOME): "Welcome to <b>" + params.get("resource_link_title") + "</b>", "UTF-8") );
        } catch (UnsupportedEncodingException e) {
            log.debug("Error encoding meetingName: " + e.getMessage());
            meetingParams.put("welcome", "");
        }
        meetingParams.put("voiceBridge", params.containsKey(PARAM_CUSTOM_VOICEBRIDGE)? params.get(PARAM_CUSTOM_VOICEBRIDGE): "0");
        if(params.containsKey(PARAM_CUSTOM_RECORD)){
            meetingParams.put("record", Boolean.valueOf(params.get(PARAM_CUSTOM_RECORD)).toString());
            try {
                meetingParams.put("welcome", meetingParams.get("welcome") + URLEncoder.encode("<br><br>This meeting is being recorded", "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                log.debug("Error encoding meetingName: " + e.getMessage());
            }
        }
        if(params.containsKey(PARAM_CUSTOM_DURATION)){
            meetingParams.put("duration", params.get(PARAM_CUSTOM_DURATION));
            try {
                meetingParams.put("welcome", meetingParams.get("welcome") + URLEncoder.encode("<br><br>The maximum duration for this meeting is ", "UTF-8") + params.get("extra_duration") + URLEncoder.encode(" minutes.", "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                log.debug("Error encoding meetingName: " + e.getMessage());
            }
        }
        if(params.containsKey("launch_presentation_return_url"))
            meetingParams.put("logoutURL", getValidatedLogoutURL(params.get("launch_presentation_return_url")));

        return meetingParams;
    }

    private Map<String, String> getSessionParams(){
        Map<String, String> params = tp.getParameters();
        Map<String, String> sessionParams = new HashMap<String, String>();
        // Map LtiUser parameters with Session parameters
        sessionParams.put("fullName", getValidatedUserFullName(params));
        sessionParams.put("meetingID", getValidatedMeetingId(params.get("resource_link_id"), params.get("oauth_consumer_key")));
        if( LTIRoles.isStudent(params.get("roles")) || LTIRoles.isLearner(params.get("roles")) )
            sessionParams.put("password", DigestUtils.shaHex("ap" + params.get("resource_link_id") + params.get("oauth_consumer_key")));
        else
            sessionParams.put("password", DigestUtils.shaHex("mp" + params.get("resource_link_id") + params.get("oauth_consumer_key")));
        //Set the role
        if( LTIRoles.isStudent(params.get("roles")) || LTIRoles.isLearner(params.get("roles")) )
            sessionParams.put("role", BBB_ROLE_VIEWER);
        else
            sessionParams.put("role", BBB_ROLE_MODERATOR);
        ////sessionParams.put("createTime", "");
        sessionParams.put("userID", DigestUtils.shaHex( params.get("user_id") + params.get("oauth_consumer_key")));

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
        return (logoutURL == null)? "": logoutURL;
    }

    private String getValidatedUserFullName(Map<String, String> params){
        String userFullName = params.get("lis_person_name_full");
        String userFirstName = params.get("lis_person_name_given");
        String userLastName = params.get("lis_person_name_family");
        if( userFullName == null || userFullName == "" ){
            if( userFirstName != null && userFirstName != "" ){
                userFullName = userFirstName;
            }
            if( userLastName != null && userLastName != "" ){
                userFullName += userFullName.length() > 0? " ": "";
                userFullName += userLastName;
            }
            if( userFullName == null || userFullName == "" ){
                userFullName = ( LTIRoles.isStudent(params.get("roles"), true) || LTIRoles.isLearner(params.get("roles"), true) )? "Viewer" : "Moderator";
            }
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
        Map<String, String> params = tp.getParameters();
        Map<String, String> meetingParams = new HashMap<String, String>();
        // Map ToolProvider parameters with Meeting parameters
        meetingParams.put("name", getValidatedMeetingName(params.get("resource_link_title")));
        meetingParams.put("meetingID", getValidatedMeetingId(params.get("resource_link_id"), params.get("oauth_consumer_key")));
        meetingParams.put("attendeePW", DigestUtils.shaHex("ap" + params.get("resource_link_id") + params.get("oauth_consumer_key")));
        meetingParams.put("moderatorPW", DigestUtils.shaHex("mp" + params.get("resource_link_id") + params.get("oauth_consumer_key")));
        try {
            meetingParams.put("welcome", URLEncoder.encode(params.containsKey(PARAM_CUSTOM_WELCOME)? params.get(PARAM_CUSTOM_WELCOME): "Welcome to <b>" + params.get("resource_link_title") + "</b>", "UTF-8") );
        } catch (UnsupportedEncodingException e) {
            log.debug("Error encoding meetingName: " + e.getMessage());
            meetingParams.put("welcome", "");
        }
        meetingParams.put("voiceBridge", params.containsKey(PARAM_CUSTOM_VOICEBRIDGE)? params.get(PARAM_CUSTOM_VOICEBRIDGE): "0");
        if(params.containsKey(PARAM_CUSTOM_RECORD)){
            meetingParams.put("record", Boolean.valueOf(params.get(PARAM_CUSTOM_RECORD)).toString());
            try {
                meetingParams.put("welcome", meetingParams.get("welcome") + URLEncoder.encode("<br><br>This meeting is being recorded", "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                log.debug("Error encoding meetingName: " + e.getMessage());
            }
        }
        if(params.containsKey(PARAM_CUSTOM_DURATION)){
            meetingParams.put("duration", params.get(PARAM_CUSTOM_DURATION));
            try {
                meetingParams.put("welcome", meetingParams.get("welcome") + URLEncoder.encode("<br><br>The maximum duration for this meeting is ", "UTF-8") + params.get("extra_duration") + URLEncoder.encode(" minutes.", "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                log.debug("Error encoding meetingName: " + e.getMessage());
            }
        }
        if(params.containsKey("launch_presentation_return_url"))
            meetingParams.put("logoutURL", getValidatedLogoutURL(params.get("launch_presentation_return_url")));

        return meetingParams;
    }

}
