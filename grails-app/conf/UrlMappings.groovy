class UrlMappings {
    /*
     * $scheme://$domain:$port/$application/$controller/$engine_type/$id/$hg_act/?query_string
     *
     * Configuration for tenant 1:BlindsideNetworks
     *  https://lti.123it.ca/hg/lti/bigbluebutton/1
     *  https://lti.123it.ca/hq/content/bigbluebutton/1/rss.xml
     *  https://lti.123it.ca/hq/content/bigbluebutton/1/cc.xml
     *  https://lti.123it.ca/hq/content/bigbluebutton/1/config.html
     *  https://lti.123it.ca/hq/content/bigbluebutton/1/ui.html
     *  https://lti.123it.ca/hq/content/bigbluebutton/1/sso
     */

	static mappings = {
		"/$controller/$type?/$id?/$act?"{
            action = 'index'
			constraints {
				// apply constraints here
			}
		}

		"/"(view:"/index")
		"500"(view:'/error')
	}
}
