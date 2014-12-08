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

public class BBBException extends Exception {

	private static final long serialVersionUID = 4842924424437922453L;

	public static final String  MESSAGEKEY_HTTPERROR            = "httpError";
	public static final String  MESSAGEKEY_NOTFOUND             = "notFound";
	public static final String  MESSAGEKEY_NOACTION             = "noActionSpecified";
	public static final String  MESSAGEKEY_IDNOTUNIQUE          = "idNotUnique";
	public static final String  MESSAGEKEY_NOTSTARTED           = "notStarted";
	public static final String  MESSAGEKEY_ALREADYENDED         = "alreadyEnded";
	public static final String  MESSAGEKEY_INTERNALERROR        = "internalError";
	public static final String  MESSAGEKEY_UNREACHABLE          = "unreachableServerError";
	public static final String  MESSAGEKEY_INVALIDRESPONSE      = "invalidResponseError";
	public static final String  MESSAGEKEY_GENERALERROR         = "generalError";

	private String messageKey;

	public BBBException(String messageKey, String message, Throwable cause) {
		super(message, cause);
		this.messageKey = messageKey;
	}

	public BBBException(String messageKey, String message) {
		super(message);
		this.messageKey = messageKey;
	}

	public String getMessageKey() {
		return messageKey;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}
	
	public String getPrettyMessage() {
		String _message = getMessage();
		String _messageKey = getMessageKey();
		
		StringBuilder pretty = new StringBuilder();
		if(_message != null) {
			pretty.append(_message);
		}
		if(_messageKey != null && !"".equals(_messageKey.trim())) {
			pretty.append(" (");
			pretty.append(_messageKey);
			pretty.append(")");
		}
		return pretty.toString();
	}

}
