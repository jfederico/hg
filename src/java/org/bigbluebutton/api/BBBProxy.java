/*
    BigBlueButton - http://www.bigbluebutton.org

    Copyright (c) 2008-2013 by respective authors (see below). All rights reserved.

    BigBlueButton is free software; you can redistribute it and/or modify it under the
    terms of the GNU Lesser General Public License as published by the Free Software
    Foundation; either version 2 of the License, or (at your option) any later
    version.

    BigBlueButton is distributed in the hope that it will be useful, but WITHOUT ANY
    WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
    PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License along
    with BigBlueButton; if not, If not, see <http://www.gnu.org/licenses/>.

    Author: Jesus Federico <jesus@blindsidenetworks.com>
*/
package org.bigbluebutton.api;

import java.util.Map;

public interface BBBProxy {

    // API Server Path
    public final static String API_SERVERPATH = "api/";

    // API Calls
    public final static String APICALL_CREATE            = "create";
    public final static String APICALL_JOIN              = "join";
    public final static String APICALL_ISMEETINGRUNNING  = "isMeetingRunning";
    public final static String APICALL_END               = "end";
    public final static String APICALL_GETMEETINGINFO    = "getMeetingInfo";
    public final static String APICALL_GETMEETINGS       = "getMeetings";
    public final static String APICALL_GETRECORDINGS     = "getRecordings";
    public final static String APICALL_PUBLISHRECORDINGS = "publishRecordings";
    public final static String APICALL_DELETERECORDINGS  = "deleteRecordings";

    // API Response Codes
    public final static String APIRESPONSE_SUCCESS = "SUCCESS";
    public final static String APIRESPONSE_FAILED = "FAILED";

    // API MesageKey Codes
    public final static String MESSAGEKEY_IDNOTUNIQUE = "idNotUnique";
    public final static String MESSAGEKEY_DUPLICATEWARNING = "duplicateWarning";

    public final static String PARAMETERENCODING = "UTF-8";

    public final static String DEFAULT_ENDPOINT = "http://test-install.blindsidenetworks.com/bigbluebutton/";
    public final static String DEFAULT_SECRET = "8cd8ef52e8e101574e400365b55e11a6";

    public String getVersionURL();
    public String getCreateURL(Map<String, String> params);
    public String getJoinURL(Map<String, String> params);
    public String getIsMeetingRunningURL(Map<String, String> params);
    public String getEndURL(Map<String, String> params);
    public String getGetMeetingInfoURL(Map<String, String> params);
    public String getGetMeetingsURL();
    public String getStringEncoded(String string);
    public String getGetRecordingsURL(Map<String, String> params);
    public String getPublishRecordingsURL(Map<String, String> params);
    public String getDeleteRecordingsURL(Map<String, String> params);
}
