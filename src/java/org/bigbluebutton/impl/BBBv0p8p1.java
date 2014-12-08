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

public class BBBv0p8p1 extends BBBv0p8p0 {

	public BBBv0p8p1(){
		super();
	}

    public BBBv0p8p1(String endpoint, String secret){
        super(endpoint, secret);
    }

    public String getCreateURL(Map<String, String> params){
        String qs;

        qs = "name=" + params.get("name");
        qs += "&meetingID=" + params.get("meetingID");
        qs += "&moderatorPW=" + params.get("moderatorPW");
        qs += "&attendeePW=" + params.get("attendeePW");
        qs += params.containsKey("welcome")? "&welcome=" + params.get("welcome"): "";
        qs += params.containsKey("logoutURL")? "&logoutURL=" + params.get("logoutURL"): "";
        qs += params.containsKey("maxParticipants")? "&maxParticipants=" + params.get("maxParticipants"): "";
        Integer voiceBridge = Integer.valueOf(params.containsKey("voiceBridge")? params.get("voiceBridge"): "0");
        voiceBridge = ( voiceBridge == null || voiceBridge == 0 )? 70000 + new Random(System.currentTimeMillis()).nextInt(10000): voiceBridge;
        qs += "&voiceBridge=" + voiceBridge.toString();
        qs += params.containsKey("dialNumber")? "&dialNumber=" + params.get("dialNumber"): "";
        qs += params.containsKey("webVoice")? "&webVoice=" + params.get("webVoice"): "";
        qs += params.containsKey("record")? "&record=" + params.get("record"): "";
        qs += params.containsKey("duration")? "&duration=" + params.get("duration"): "";
        qs += params.containsKey("meta")? "&" + params.get("meta"): "";
        qs += getCheckSumParameterForQuery(APICALL_CREATE, qs);

        return this.endpoint + API_SERVERPATH + APICALL_CREATE + "?" + qs;
    }

    public String getJoinURL(Map<String, String> params) {
        String qs;

        qs = "fullName=" + params.get("fullName");
        qs += "&meetingID=" + params.get("meetingID");
        qs += "&password=" + params.get("password");
        qs += params.containsKey("createTime")? "&createTime=" + params.get("createTime"): "";
        qs += params.containsKey("userID")? "&userID=" + params.get("userID"): "";
        qs += params.containsKey("webVoiceConf")? "&webVoiceConf=" + params.get("webVoiceConf"): "";
        qs += getCheckSumParameterForQuery(APICALL_JOIN, qs);

        return this.endpoint + API_SERVERPATH + APICALL_JOIN + "?" + qs;
    }
}
