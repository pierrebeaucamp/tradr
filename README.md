#Tradr

Tradr is a platform to help people looking for, giving away or exchanging items 
to find suitable trading partners with minimum time and effort invested.

**Current Version:** *0.0.3*

## Prerequisites
This project is built using ``sbt``, **not** ``activator``. Therefore, you'll 
need to install [sbt](http://www.scala-sbt.org/).

Next, as this project uses the Google App Engine, it needs access to the Google
App Engine Java SDK. You can download it [here](https://cloud.google.com/appengine/downloads).
After it is installed, ``sbt`` requires an environment variable to point to
the directory of the SDK:
````
export APPENGINE_SDK_HOME=/path/to/appengine-java-sdk
````

Finally, for all the Frontend work, we need [Gulp](http://gulpjs.com/).

## Building
First, get all the dependencies for Gulp:
````
npm install
````

Afterwards, you need to build all the frontend code using Gulp:
````
gulp
````

To run a local developer server, run:
````
sbt ~appengineDevServer
````
This local server will rebuild itself once it detects changes in one of the source
files. So you don't need to kill the process everytime you want to rebuild the
project.

To deploy the application to Google App Engine, run:
````
sbt appengineDeploy
````

## Developing
The folder structure follows the 
[default sbt layout](https://www.playframework.com/documentation/2.4.x/Anatomy#Default-SBT-layout)
as supported by Play. As Play 2.4.x requires Java 8, but the Appengine SDK still
uses Java 7, we need to write the application using Play 2.3 (in Java 7). 
**Please be sure that you're using Java 7 - otherwise App Engine will throw
Errors after the App is deployed**

Furthermore, since Play 2.0, the Framework doesn't support Modules anymore. This
breaks the [official App Engine plugin](https://www.playframework.com/documentation/1.0/gae)
so we are forced to use an unofficial one from the community
([source](https://github.com/siderakis/playframework-appengine)). However, this
unofficial plugin is written purely in Scala, enforcing the Scala language for
play controllers.

As we are required to write all our logic in Java, this project introduces the
Java package ``com.appspot.tradr_seba``. So while Play routes all trafic to
``ApplicationController.scala``, the controller will pass everything down to
the tradr_seba Java package so we can comply with our requirements. 

Finally, it is important that we update the Version number with each 
commit. Once the CI is in place, App Engine will always run the latest version 
of our code. To be able to debug and monitor the application, please update the 
Version number in ``README.md``, ``src/main/assets/manifest.json``, 
``src/main/assets/manifest.webapp``, ``appengine.sbt`` and 
``src/main/assets/WEB-INF/appengine-web.xml``. 
If I have some free time on my hands, I'll write a bash script to adjust the version
number on the go, but for now, we need to update it manually.
