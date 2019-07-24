inThisBuild(Seq(
  organization in ThisBuild := "io.protoless",
  scalaVersion := "2.12.4-bin-typelevel-4",
  crossScalaVersions := Seq("2.11.11-bin-typelevel-4", "2.12.4-bin-typelevel-4"),
  scalaOrganization := "org.typelevel",
  releaseCrossBuild := true
))

// Dependencies
lazy val catsVersion = "1.0.0"
lazy val shapelessVersion = "2.3.3"
lazy val protobufJavaVersion = "3.5.1"
lazy val scalaTestVersion = "3.0.3"
lazy val scalaticVersion = "3.0.3"
lazy val scalaCheckVersion = "1.13.5"

lazy val cats = Seq(
  "org.typelevel" %% "cats-core" % catsVersion
)

lazy val shapeless = Seq(
  "com.chuusai" %% "shapeless" % shapelessVersion
)

lazy val protobuf = Seq(
  "com.google.protobuf" % "protobuf-java" % protobufJavaVersion
)

lazy val scalatest = Seq(
  "org.scalatest" %% "scalatest" % scalaTestVersion,
  "org.scalactic" %% "scalactic" % scalaticVersion
)

lazy val scalacheck = Seq(
  "org.scalacheck" %% "scalacheck" % scalaCheckVersion % "test"
)

// Scalac 2.12 flags: https://tpolecat.github.io/2017/04/25/scalac-flags.html
val `compilerOptions-Scala-2.12` = Seq(
  "-deprecation",                      // Emit warning and location for usages of deprecated APIs.
  "-encoding", "utf-8",                // Specify character encoding used by source files.
  "-explaintypes",                     // Explain type errors in more detail.
  "-feature",                          // Emit warning and location for usages of features that should be imported explicitly.
  "-language:higherKinds",             // Allow higher-kinded types
  "-unchecked",                        // Enable additional warnings where generated code depends on assumptions.
  "-Xcheckinit",                       // Wrap field accessors to throw an exception on uninitialized access.
  "-Xfatal-warnings",                  // Fail the compilation if there are any warnings.
  "-Xfuture",                          // Turn on future language features.
  "-Xlint:adapted-args",               // Warn if an argument list is modified to match the receiver.
  "-Xlint:by-name-right-associative",  // By-name parameter of right associative operator.
  "-Xlint:constant",                   // Evaluation of a constant arithmetic expression results in an error.
  "-Xlint:delayedinit-select",         // Selecting member of DelayedInit.
  "-Xlint:doc-detached",               // A Scaladoc comment appears to be detached from its element.
  "-Xlint:inaccessible",               // Warn about inaccessible types in method signatures.
  "-Xlint:infer-any",                  // Warn when a type argument is inferred to be `Any`.
  "-Xlint:missing-interpolator",       // A string literal appears to be missing an interpolator id.
  "-Xlint:nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Xlint:nullary-unit",               // Warn when nullary methods return Unit.
  "-Xlint:option-implicit",            // Option.apply used implicit view.
  "-Xlint:package-object-classes",     // Class or object defined in package object.
  "-Xlint:poly-implicit-overload",     // Parameterized overloaded implicit methods are not visible as view bounds.
  "-Xlint:private-shadow",             // A private field (or class parameter) shadows a superclass field.
  "-Xlint:stars-align",                // Pattern sequence wildcard must align with sequence component.
  "-Xlint:type-parameter-shadow",      // A local type parameter shadows a type already in scope.
  "-Xlint:unsound-match",              // Pattern match may not be typesafe.
  "-Yno-adapted-args",                 // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
  "-Ypartial-unification",             // Enable partial unification in type constructor inference
  "-Ywarn-dead-code",                  // Warn when dead code is identified.
  "-Ywarn-inaccessible",               // Warn about inaccessible types in method signatures.
  "-Ywarn-infer-any",                  // Warn when a type argument is inferred to be `Any`.
  "-Ywarn-nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Ywarn-nullary-unit",               // Warn when nullary methods return Unit.
  "-Ywarn-numeric-widen",              // Warn when numerics are widened.
  "-Ywarn-unused:locals",              // Warn if a local definition is unused.
  "-Ywarn-unused:patvars",             // Warn if a variable bound in a pattern is unused.
  "-Ywarn-unused:privates",            // Warn if a private member is unused.
  "-Ywarn-value-discard",               // Warn when non-Unit expression results are unused.
  "-Ywarn-extra-implicit",             // Warn when more than one implicit parameter section is defined.,
  "-Ywarn-unused:imports"
)

lazy val `compilerOptions-Scala-2.11` = Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked",
  "-language:higherKinds",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Xfuture",
  "-Ywarn-unused-import"
)

lazy val settings = Seq(
  scalacOptions ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, p)) if p >= 12 => `compilerOptions-Scala-2.12`
      case _ => `compilerOptions-Scala-2.11`
    }
  },

  // Typelevel-4 scala options
  scalacOptions ++= Seq(
    "-Yinduction-heuristics",       // speeds up the compilation of inductive implicit resolution
    "-Yliteral-types",              // literals can appear in type position
    "-Xstrict-patmat-analysis",     // more accurate reporting of failures of match exhaustivity
    "-Xlint:strict-unsealed-patmat" // warn on inexhaustive matches against unsealed traits
  ),

  // Flags not compatible with REPL mode
  scalacOptions in (Compile, console) ~= (_.filterNot(Set(
    "-Ywarn-unused:imports",  // 2.12
    "-Ywarn-unused-import",   // 2.11
    "-Xfatal-warnings"
  ))),

  // Test source code contains `import cats.syntax.either._` for scala 2.11 compatibility, but it is
  // an unused import for scala 2.12... so disabled it for now.
  scalacOptions in Test ~= (_.filterNot(Set(
    "-Ywarn-unused:imports"  // 2.12
  ))),

  // Warnings in scaladoc must not fail the build
  scalacOptions in (Compile, doc)  ~= (_.filterNot(Set(
    "-Xfatal-warnings"
  ))),

  // scalastyle task should run on all source file
  (scalastyleSources in Compile) ++= (unmanagedSourceDirectories in Compile).value,

  // Ignore CustomMappingEncoderDecoderSuite test file because it use literal type, not accepted by scalastyle
  (scalastyleSources in Test) := {
      val scalaSourceFiles = ((scalastyleSources in Test).value ** "*.scala").get
      scalaSourceFiles.filterNot(_.getName == "CustomMappingEncoderDecoderSuite.scala")
  },

  autoAPIMappings := true,

  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  ),

  libraryDependencies ++= cats ++ scalatest.map(_ % "test") ++ scalacheck
)

lazy val protoless = project.in(file("."))
  .settings(settings)
  .settings(noPublishSettings)
  .settings(
    initialCommands in console :=
      """
        |import io.protoless._
        |import io.protoless.generic.auto._
        |import io.protoless.tag._
        |import io.protoless.syntax._
      """.stripMargin
    )
  .dependsOn(core, tag, generic)
  .aggregate(core, tag, generic, docs)


lazy val core = project.in(file("modules/core"))
  .settings(settings)
  .settings(publishSettings)
  .settings(
    name := "Protoless core",
    libraryDependencies ++= protobuf ++ shapeless ++cats
  )
  .dependsOn(tag, testing % Test)

lazy val testing = project.in(file("modules/testing"))
  .settings(settings)
  .settings(noPublishSettings)
  .settings(
    name := "Protoless testing",
    libraryDependencies ++= protobuf ++ scalatest
  )
  .dependsOn(tag)

lazy val generic = project.in(file("modules/generic"))
  .settings(settings)
  .settings(publishSettings)
  .settings(
    name := "Protoless generic",
    libraryDependencies ++= shapeless
  )
  .dependsOn(core % "test->test;compile->compile", tag, testing % Test)

lazy val tag = project.in(file("modules/tag"))
  .settings(settings)
  .settings(publishSettings)
  .settings(
    name := "Protoless tag",
    libraryDependencies ++= shapeless
  )

lazy val docs = project.dependsOn(core, generic)
  .enablePlugins(ScalaUnidocPlugin)
  .enablePlugins(GhpagesPlugin)
  .enablePlugins(MicrositesPlugin)
  .settings(settings)
  .settings(docSettings)
  .settings(noPublishSettings)


lazy val noPublishSettings = Seq(
  publish := ((): Unit),
  publishLocal := ((): Unit),
  publishArtifact := false
)

lazy val docSettings = Seq(
  micrositeName := "protoless",
  micrositeDescription := "Type-safe and schema-free Protobuf library for Scala",
  micrositeAuthor := "Julien Lafont",
  micrositeTwitterCreator := "@julien_lafont",
  micrositeHighlightTheme := "monokai",
  micrositeHomepage := "https://julien-lafont.github.io/protoless",
  micrositeDocumentationUrl := "api",
  micrositeBaseUrl := "/protoless",
  micrositeGithubOwner := "julien-lafont",
  micrositeGithubRepo := "protoless",
  micrositeGitterChannel := true,
  micrositeGitterChannelUrl := "protoless/Lobby",
  micrositeHighlightTheme := "atom-one-dark",
  micrositeAnalyticsToken := "UA-40626968-2",
  micrositeCDNDirectives := microsites.CdnDirectives(
    jsList = List(
      "https://cdnjs.cloudflare.com/ajax/libs/raphael/2.2.7/raphael.min.js",
      "https://cdnjs.cloudflare.com/ajax/libs/flowchart/1.6.6/flowchart.min.js"
    )
  ),
  addMappingsToSiteDir(mappings in (ScalaUnidoc, packageDoc), micrositeDocumentationUrl),
  ghpagesNoJekyll := false,
  scalacOptions in (ScalaUnidoc, unidoc) ++= Seq(
    "-groups",
    "-implicits",
    "-diagrams",
    "-doc-source-url", scmInfo.value.get.browseUrl + "/tree/master€{FILE_PATH}.scala",
    "-sourcepath", baseDirectory.in(LocalRootProject).value.getAbsolutePath,
    "-doc-root-content", (resourceDirectory.in(Compile).value / "rootdoc.txt").getAbsolutePath
  ),
  git.remoteRepo := "git@github.com:julien-lafont/protoless.git",
  unidocProjectFilter in (ScalaUnidoc, unidoc) := inAnyProject,
  includeFilter in makeSite := "*.html" | "*.css" | "*.png" | "*.jpg" | "*.gif" | "*.svg" | "*.js" | "*.swf" | "*.yml" | "*.md"
)

lazy val publishSettings = Seq(
  homepage := Some(url("https://github.com/julien-lafont/protoless")),
  licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  autoAPIMappings := true,
  apiURL := Some(url("https://julien-lafont.github.io/protoless/api/")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/julien-lafont/protoless"),
      "scm:git:git@github.com:julien-lafont/protoless.git"
    )
  ),
  developers := List(
    Developer("julien-lafont", "Julien Lafont", "julien.lafont@gmail.com",
      url("https://twitter.com/julien_lafont"))
  ),
  bintrayOrganization := Some("julien-lafont")
)

addCommandAlias("validate", ";protoless/test;test:scalastyle;docs/unidoc")
