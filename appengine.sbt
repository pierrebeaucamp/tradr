import sbtappengine.Plugin.{AppengineKeys => gae}
import play.PlayProject

name := "PlayFramework-AppEngine"
scalaVersion := "2.10.2"

resolvers += "Scala AppEngine Sbt Repo" at "http://siderakis.github.com/maven"

libraryDependencies ++= Seq(
  "com.siderakis"     %% "futuraes"                     % "0.1-SNAPSHOT",
  "com.siderakis"     %% "playframework-appengine-mvc"  % "0.2-SNAPSHOT",
  "javax.servlet"     % "servlet-api"                   % "2.5"           % "provided",
  "org.mortbay.jetty" % "jetty"                         % "6.1.22"        % "container",
  "junit"             % "junit"                         % "4.12"          % "test"
)

appengineSettings
PlayProject.defaultPlaySettings
Twirl.settings