/*
 * HyperGate is a generic message broker application
 *
 * Copyright (C) 2015  Jesus Federico
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * HyperGate is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.hg

import java.util.Date;

class LtiToolConsumer {
    // Auto Timestamp
    Date dateCreated
    Date lastUpdated

    String info = ""

    static hasMany = [ltiContexts:LtiContext, ltiUsers:LtiUser, ltiRegistrations:LtiRegistration]

    static mapping = {
        ltiContexts cascade: 'all-delete-orphan'
        ltiUsers cascade: 'all-delete-orphan'
        ltiRegistrations cascade: 'all-delete-orphan'
    }

    static constraints = {
    }
    
    String toString() {"${this.info}"}
}
