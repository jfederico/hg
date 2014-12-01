grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

// uncomment (and adjust settings) to fork the JVM to isolate classpaths
//grails.project.fork = [
//   run: [maxMemory:1024, minMemory:64, debug:false, maxPerm:256]
//]

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // specify dependency exclusions here; for example, uncomment this to disable ehcache:
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve
    legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

    repositories {
        inherits true // Whether to inherit repository definitions from plugins

        grailsPlugins()
        grailsHome()
        grailsCentral()

        mavenLocal()
        mavenCentral()

        // uncomment these (or add new ones) to enable remote dependency resolution from public Maven repositories
        mavenRepo "http://snapshots.repository.codehaus.org"
        mavenRepo "http://repository.codehaus.org"
        mavenRepo "http://download.java.net/maven/2/"
        mavenRepo "http://repository.jboss.com/maven2/"

        // Blindside Networks repositories
        mavenRepo "https://raw.github.com/blindsidenetworks/bigbluebutton-api/mvn-repo/"
        mavenRepo "https://raw.github.com/blindsidenetworks/oauth/mvn-repo/"
        //mavenRepo "https://raw.github.com/blindsidenetworks/lti/mvn-repo/"

    }

    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes e.g.

        runtime 'mysql:mysql-connector-java:5.1.27'
        runtime 'org.xerial:sqlite-jdbc:3.8.7'
        runtime "commons-net:commons-net:3.0.1"
        runtime "org.json:json:20131018"
        runtime "org.bigbluebutton:bigbluebutton-api:1.0.5"
        runtime "net.oauth:oauth:1.0.1"
        //runtime "org.lti:lti:1.0.11"
    }

    plugins {
        // plugins for the build system only
        build ":tomcat:7.0.50.1"

        // plugins for the compile step
        compile ":scaffolding:2.0.2"
        compile ':cache:1.1.1'
        
        // plugins needed at runtime but not for compilation
        runtime ":hibernate:3.6.10.8" // or ":hibernate4:4.3.1.1"
        runtime ":database-migration:1.3.8"
        runtime ":jquery:1.11.1"
        runtime ":resources:1.2.1"
        runtime ':twitter-bootstrap:3.1.1'
        // Uncomment these (or add new ones) to enable additional resources capabilities
        //runtime ":zipped-resources:1.0.1"
        //runtime ":cached-resources:1.1"
        //runtime ":yui-minify-resources:0.1.5"

        // An alternative to the default resources plugin is the asset-pipeline plugin
        //compile ":asset-pipeline:1.5.0"

        // Uncomment these to enable additional asset-pipeline capabilities
        //compile ":sass-asset-pipeline:1.5.1"
        //compile ":less-asset-pipeline:1.5.0"
        //compile ":coffee-asset-pipeline:1.5.0"
        //compile ":handlebars-asset-pipeline:1.0.0.3"

        // Uncomment these (or add new ones) to enable additional resources capabilities
        //runtime ":zipped-resources:1.0"
        //runtime ":cached-resources:1.0"
        //runtime ":yui-minify-resources:0.1.5"
    }
}
