name := "example"

version := "0.1"

libraryDependencies ++= Seq(
  "com.googlecode.json-simple" % "json-simple" % "1.1.1",
  "com.amazonaws" % "aws-lambda-java-core" % "1.2.0",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)

