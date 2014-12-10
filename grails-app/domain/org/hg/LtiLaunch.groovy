package org.hg

import java.util.Date;

class LtiLaunch {
    // Auto Timestamp
    Date dateCreated
    Date lastUpdated

    LtiResourceLink ltiResourceLink
    static belongsTo = [ltiResourceLink : LtiResourceLink]
    
    LtiUser ltiUser
    
    String info = ""

    static constraints = {
    }

    String toString() {"[${this.ltiResourceLink}]${this.ltiUser}:${this.info}"}
}
