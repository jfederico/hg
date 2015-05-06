import org.hg.engine.Engine

class UrlMappings {
    /*
     * $scheme://$domain:$port/$application/$tenant/$engine_type/?query_string
     *
     * engine_type = [ launch | registration | resource | api | config.xml ]
     *
     * query_string = ?a=xxx&c=yyy (act=action, cmd=command)
     *
     * engine_type = launch          :. act = [ cc | sso | ui ]
     *                                  act = ui :. e.g. for bigbluebutton cmd = [ join | publish | unpublish | delete ]
     * engine_type = registration    :. act = [  ]
     * engine_type = resource        :. act = [ xml | json | rss | png | ico ]
     * engine_type = api             :. act = [ outcomes | ]
     *                                  act = outcomes :. cmd = [ get | put | post | delete | update ] 
     *
     * Configuration for tenant 0:InternalTest
     *
     *      https://lti.123it.ca/hg/
     *
     *      It is translated to
     *
     *      https://lti.123it.ca/hg/0/launch
     *
     *      Meaning
     *
     *      app=hg      { name: hg }
     *      tenant=0    { id: 0, name: test }
     *      engine=launch
     *
     *
     * Configuration for tenant 1:BlindsideNetworks
     *
     *      https://lti.123it.ca/hg/bn
     *
     *      It is translated to
     *
     *      https://lti.123it.ca/hg/bn/launch
     *
     *      Meaning
     *
     *      app=hg      { name: hg }
     *      tenant=bn   { id: 1, name: blindsidenetworks, alias: bn }
     *      engine=launch
     *
     */

	static mappings = {

		"/"{
            application = grails.util.Metadata.current.'app.name'
            controller = 'hg'
            action = 'index'
            tenant = '0'
            engine = Engine.ENGINE_TYPE_LAUNCH
		}
        
        "/$tenant?/"{
            application = grails.util.Metadata.current.'app.name'
            controller = 'hg'
            action = 'index'
            engine = Engine.ENGINE_TYPE_LAUNCH
        }

        "/$tenant?/$engine?(.${format})?"{
            application = grails.util.Metadata.current.'app.name'
            controller = 'hg'
            action = 'index'
            constraints {
                // apply constraints here
            }
        }

		"500"(view:'/error')
	}
}
