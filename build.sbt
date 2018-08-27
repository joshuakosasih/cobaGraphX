name := "secondtry-ide"

version := "0.1"

scalaVersion := "2.11.12"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-sql" % "2.3.1" % "provided",
  "org.apache.spark" % "spark-graphx_2.11" % "2.3.1" % "provided",
  "com.googlecode.json-simple" % "json-simple" % "1.1.1",
  "com.google.inject" % "guice" % "4.1.0",
  "org.janusgraph" % "janusgraph-core" % "0.2.0",
  "org.janusgraph" % "janusgraph-cassandra" % "0.2.0",
  "org.janusgraph" % "janusgraph-es" % "0.2.0",
  "org.janusgraph" % "janusgraph-lucene" % "0.2.0"
)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

