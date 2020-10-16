
name := "spike-graal-hocon-fabric8"

scalaVersion := "2.12.12"

libraryDependencies ++= Seq(
  "org.ekrich" %% "sconfig" % "1.3.3",
  "io.quarkus" % "quarkus-kubernetes-client" % "1.9.0.Final",
  // "com.github.alexarchambault" %% "case-app" % "2.0.4",
  "org.wvlet.airframe" %% "airframe-log" % "20.10.0"
)

enablePlugins(GraalVMNativeImagePlugin)
// this will build the Linux binaries on docker
// graalVMNativeImageGraalVersion := Some("20.2.0-java11")

graalVMNativeImageOptions := Seq(
  "--verbose",
  "--no-server",
  "--enable-http",
  "--enable-https",
  "--enable-url-protocols=http,https,file,jar",
  "--enable-all-security-services",
  "-H:+JNI",
  // "--static", not supported on Mac
  "-H:IncludeResourceBundles=com.sun.org.apache.xerces.internal.impl.msg.XMLMessages",
  "-H:+ReportExceptionStackTraces",
  "--no-fallback",
  "--initialize-at-build-time",
  "--report-unsupported-elements-at-runtime"
)

// Command used to generate the configuration for the reflection
// fork in run := true
// run / javaOptions += "-agentlib:native-image-agent=config-output-dir=src/graal/META-INF/native-image"

// compilation time:
// Total time: 127 s (02:07)
// :-(
