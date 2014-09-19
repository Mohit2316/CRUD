
name := """pcci-i"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  javaCore,
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  "org.apache.commons" % "commons-email" % "1.2",
  "com.typesafe.play.plugins" %% "play-plugins-mailer" % "2.3.0",
  "commons-io" % "commons-io" % "2.3",
  "mysql" % "mysql-connector-java" % "5.1.31",
  "be.objectify" %% "deadbolt-java" % "2.3.0-RC1",
  "net.sf.opencsv" % "opencsv" % "2.3"
)

//val appDependencies = Seq(
//  "play.modules.mailer" %% "play-mailer" % "2.1.3"
//)
// resolvers += "Rhinofly Internal Release Repository" at "http://maven-repository.rhinofly.net:8081/artifactory/libs-release-local"

resolvers ++= Seq("release repository" at  "http://hakandilek.github.com/maven-repo/releases/",
              "snapshot repository" at "http://hakandilek.github.com/maven-repo/snapshots/",
               Resolver.url("Objectify Play Repository", url("http://schaloner.github.io/releases/"))(Resolver.ivyStylePatterns))




