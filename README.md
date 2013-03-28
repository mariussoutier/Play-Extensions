Play 2 extensions
=================

Everything that should be included in Play - but wasn't.


Using it
--------

In your `Build.scala`:

    val playExtensions = RootProject(uri("git://github.com/mariussoutier/Play-Extensions.git"))
    val main = play.Project(...).dependsOn(playExtensions)


Global Behavior
---------------

### Auto Conf

An extension to the global settings that simplifies loading custom configuration files with a
convention-over-configuration approach. Mix in the trait `com.mariussoutier.play.extensions.global.AutoConf`
and the following rules apply:
* In development mode, a file called `dev.conf` will be loaded; after that, a file with your current
  username + .conf (as in the Java environment variable `user.name`) will be loaded
* In test mode, a file called `test.conf` will be loaded
* In production mode, a file called `prod.conf` will be loaded

Play will always load an `application.conf` file, and this trait won't prevent this, but you can
override keys from the `application.conf` in your custom config files. Well, that's the whole point.


### Play Trailing Slash

An extension to the global settings that generates a 401 Moved Permanently redirect for request
paths that end with a slash, redirecting to a path without a trailing slash.

Consider a simple URL such as http://www.example.com/login that displays a login page. If the user
enters http://www.example.com/login/ by accident, Play will generate a 404 result. If you define both
routes, Google will treat this as duplicate content. Play's default router only offers a 302 redirect
which doesn't solve the duplicate content problem.

Scala:

    import com.mariussoutier.play._
    object Global extends GlobalSettings with TrailingSlash

Java:

    import com.mariussoutier.play.*;
    public class Global extends TrailingSlashSettings {
    }



View Helpers
------------

A collection of random useful view helpers that make working with Scala templates easier.
