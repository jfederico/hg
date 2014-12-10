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
