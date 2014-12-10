package org.hg

import java.util.Date;

class LtiUser {
    // Auto Timestamp
    Date dateCreated
    Date lastUpdated

    LtiToolConsumer ltiToolConsumer
    static belongsTo = [ltiToolConsumer:LtiToolConsumer]

    String info = ""

    static constraints = {
    }

    String toString() {"[${this.ltiToolConsumer}]${this.info}"}
}
