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

import org.bigbluebutton.api.BBBException;
import org.bigbluebutton.api.BBBProxy;
import org.bigbluebutton.api.BBBStore;

import org.apache.log4j.Logger;

public class BBBStoreImpl implements BBBStore {

    private static final Logger log = Logger.getLogger(BBBStoreImpl.class);

    private static final BBBStoreImpl INSTANCE = new BBBStoreImpl();

    private BBBStoreImpl() {}

    public static BBBStoreImpl getInstance() {
        return INSTANCE;
    }

    public BBBProxy createProxy(String endpoint, String secret) throws BBBException {
        log.info("Creating BBBProxy");
        BBBProxy bbbProxy = null;
        try {
            String version = BBBProxyImpl.getVersion(endpoint, secret);
            log.debug("BigBluebutton server version: " + version);
            if( version.equals("0.80"))
                bbbProxy = new BBBv0p8p0(endpoint, secret);
            else if( version.equals("0.81"))
                bbbProxy = new BBBv0p8p1(endpoint, secret);
            else
                bbbProxy = new BBBProxyImpl(endpoint, secret);

        } catch ( Exception e ){
            throw new BBBException(BBBException.MESSAGEKEY_INTERNALERROR, "The proxy could not be instantiated", e.getCause());
        }
        return bbbProxy;
    }
}
