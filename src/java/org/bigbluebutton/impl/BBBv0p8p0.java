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
package org.bigbluebutton.impl;

import java.util.Map;
import java.util.Random;

public class BBBv0p8p0 extends BBBProxyImpl {

    public BBBv0p8p0() {
        super();
    }

    public BBBv0p8p0(String endpoint, String secret) {
        super(endpoint, secret);
    }

    public String getCreateURL(Map<String, String> params) {
        String qs = "";

        qs += PARAM_NAME +"=" + params.get(PARAM_NAME);
        qs += "&" + PARAM_MEETING_ID + "=" + params.get(PARAM_MEETING_ID);
        qs += "&" + PARAM_MODERATOR_PW + "=" + params.get(PARAM_MODERATOR_PW);
        qs += "&" + PARAM_ATTENDEE_PW + "=" + params.get(PARAM_ATTENDEE_PW);
        qs += params.containsKey(PARAM_WELCOME)? "&" + PARAM_WELCOME + "=" + params.get(PARAM_WELCOME): "";
        qs += params.containsKey(PARAM_LOGOUT_URL)? "&" + PARAM_LOGOUT_URL + "=" + params.get(PARAM_LOGOUT_URL): "";
        Integer voiceBridge = Integer.valueOf(params.containsKey(PARAM_VOICE_BRIDGE)? params.get(PARAM_VOICE_BRIDGE): "0");
        voiceBridge = ( voiceBridge == null || voiceBridge == 0 )? 70000 + new Random(System.currentTimeMillis()).nextInt(10000): voiceBridge;
        qs += "&" + PARAM_VOICE_BRIDGE + "=" + voiceBridge.toString();
        qs += params.containsKey(PARAM_DIAL_NUMBER)? "&" + PARAM_DIAL_NUMBER + "=" + params.get(PARAM_DIAL_NUMBER): "";
        qs += params.containsKey(PARAM_DURATION)? "&" + PARAM_DURATION + "=" + params.get(PARAM_DURATION): "";
        qs += params.containsKey(PARAM_META)? "&" + params.get(PARAM_META): "";
        qs += getCheckSumParameterForQuery(APICALL_CREATE, qs);

        return this.endpoint + API_SERVERPATH + APICALL_CREATE + "?" + qs;
    }

    public String getGetMeetingsURL() {
        String qs;

        qs = "";
        qs = getCheckSumParameterForQuery(APICALL_GETMEETINGS, qs);

        return this.endpoint + API_SERVERPATH + APICALL_GETMEETINGS + "?" + qs;
    }

    public String getGetRecordingsURL(Map<String, String> params) {
        String qs = "";

        qs += PARAM_MEETING_ID + "=" + params.get(PARAM_MEETING_ID);
        qs += getCheckSumParameterForQuery(APICALL_GETRECORDINGS, qs);

        return this.endpoint + API_SERVERPATH + APICALL_GETRECORDINGS + "?" + qs;
    }

    public String getPublishRecordingsURL(Map<String, String> params) {
        String qs = "";

        qs += PARAM_RECORD_ID + "=" + params.get(PARAM_RECORD_ID);
        qs += "&" + PARAM_PUBLISH + "=" + Boolean.valueOf(params.get("publish"));
        qs += getCheckSumParameterForQuery(APICALL_PUBLISHRECORDINGS, qs);

        return this.endpoint + API_SERVERPATH + APICALL_PUBLISHRECORDINGS + "?" + qs;
    }

    public String getDeleteRecordingsURL(Map<String, String> params) {
        String qs = "";

        qs += PARAM_RECORD_ID + "=" + params.get(PARAM_RECORD_ID);
        qs += getCheckSumParameterForQuery(APICALL_DELETERECORDINGS, qs);

        return this.endpoint + API_SERVERPATH + APICALL_DELETERECORDINGS + "?" + qs;
    }
}
