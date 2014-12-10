package org.hg

import java.util.Date;

class LtiResourceLink {
    // Auto Timestamp
    Date dateCreated
    Date lastUpdated

    LtiContext ltiContext
    static belongsTo = [ltiContext:LtiContext]

    String info = ""

    static hasMany = [ltiLaunches:LtiLaunch]

    static mapping = {
        ltiLaunches cascade: 'all-delete-orphan'
    }

    static constraints = {
    }

    String toString() {"[${this.ltiContext}]${this.info}"}
}
