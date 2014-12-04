class UrlMappings {
    /*
     * $scheme://$domain:$port/$application/$tenant/$engine/$version/?query_string
     *
     * engine = [ lti | reg | res | api ]
     * 
     * query_string = ?a=xxx&c=yyy (act=action, cmd=command)
     * 
     * engine = launch          :. act = [ cc | sso | ui ]
     *                                  act = ui :. e.g. for bigbluebutton cmd = [ join | publish | unpublish | delete ]
     * engine = registration    :. act = [  ]
     * engine = resource        :. act = [ xml | json | rss | png | ico ]
     * engine = api             :. act = [ outcomes | ]
     *                                  act = outcomes :. cmd = [ get | put | post | delete | update ] 
     * 
     * Configuration for tenant 0:InternalTest
     *      
     *      https://lti.123it.ca/hg/
     *      
     *      It is translated to
     *      
     *      https://lti.123it.ca/hg/0/lti/v1p0
     *      
     *      Meaning
     *      
     *      app=hg      { name: hg }
     *      tenant=0    { id: 0, name: test }
     *      engine=lti
     *      version=v1p0
     *      
     * Configuration for tenant 1:BlindsideNetworks
     *      
     *      https://lti.123it.ca/hg/bn
     *      
     *      It is translated to
     *      
     *      https://lti.123it.ca/hg/bn/lti/v1p0
     *      
     *      Meaning
     *      
     *      app=hg      { name: hg }
     *      tenant=bn   { id: 1, name: blindsidenetworks, alias: bn }
     *      engine=lti
     *      version=v1p0
     */

	static mappings = {

		"/"{
            application = grails.util.Metadata.current.'app.name'
            controller = 'lti'
            action = 'index'
            tenant = '0'
            engine = 'lti'
            version = 'v1p0' 
		}
        
        "/$tenant?/"{
            application = grails.util.Metadata.current.'app.name'
            controller = 'lti'
            action = 'index'
            engine = 'lti'
            version = 'v1p0' 
        }

        "/$tenant?/$engine?/"{
            application = grails.util.Metadata.current.'app.name'
            controller = 'lti'
            action = 'index'
            version = 'v1p0'
            constraints {
                // apply constraints here
            }
        }

        "/$tenant?/$engine?/$version?"{
            application = grails.util.Metadata.current.'app.name'
            controller = 'lti'
            action = 'index'
            constraints {
                // apply constraints here
            }
        }

		"500"(view:'/error')
	}
}
