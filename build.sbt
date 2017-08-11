organization in ThisBuild := "io.protoless"
name := "protoless"

lazy val settings = Seq(
  scalaVersion in ThisBuild := "2.12.3-bin-typelevel-4",
  scalaOrganization := "org.typelevel",
  scalacOptions ++= Seq(
    "-deprecation",                      // Emit warning and location for usages of deprecated APIs.
    "-encoding", "utf-8",                // Specify character encoding used by source files.
    "-explaintypes",                     // Explain type errors in more detail.
    "-feature",                          // Emit warning and location for usages of features that should be imported explicitly.
    "-language:existentials",            // Existential types (besides wildcard types) can be written and inferred
    "-language:experimental.macros",     // Allow macro definition (besides implementation and application)
    "-language:higherKinds",             // Allow higher-kinded types
    "-language:implicitConversions",     // Allow definition of implicit functions called views
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
    "-Ywarn-extra-implicit",             // Warn when more than one implicit parameter section is defined.
    "-Ywarn-inaccessible",               // Warn about inaccessible types in method signatures.
    "-Ywarn-infer-any",                  // Warn when a type argument is inferred to be `Any`.
    "-Ywarn-nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
    "-Ywarn-nullary-unit",               // Warn when nullary methods return Unit.
    "-Ywarn-numeric-widen",              // Warn when numerics are widened.
    "-Ywarn-unused:imports",             // Warn if an import selector is not referenced.
    "-Ywarn-unused:locals",              // Warn if a local definition is unused.
    "-Ywarn-unused:params",              // Warn if a value parameter is unused.
    "-Ywarn-unused:patvars",             // Warn if a variable bound in a pattern is unused.
    "-Ywarn-unused:privates",            // Warn if a private member is unused.
    "-Ywarn-value-discard"               // Warn when non-Unit expression results are unused.
  ),
  // Typelevel-4 scala options
  scalacOptions ++= Seq(
    "-Yinduction-heuristics",       // speeds up the compilation of inductive implicit resolution
    "-Yliteral-types",              // literals can appear in type position
    "-Xstrict-patmat-analysis",     // more accurate reporting of failures of match exhaustivity
    "-Xlint:strict-unsealed-patmat" // warn on inexhaustive matches against unsealed traits
  ),

  // scalastyle task should run on all source file
  (scalastyleSources in Compile) ++= (unmanagedSourceDirectories in Compile).value,
  // Ignore CustomMappingEncoderDecoderSuite test file because it use literal type, not accepted by scalastyle
  (scalastyleSources in Test) := {
      val scalaSourceFiles = ((scalastyleSources in Test).value ** "*.scala").get
      scalaSourceFiles.filterNot(_.getName == "CustomMappingEncoderDecoderSuite.scala")
  },

  autoAPIMappings := true,
  libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-core" % "1.0.0-MF",
    "com.chuusai" %% "shapeless" % "2.3.2",
    "com.google.protobuf" % "protobuf-java" % "3.3.1"
  )
)

lazy val protoless = project.in(file("."))
  .settings(settings)
  .settings(
    libraryDependencies ++= Seq(
      "org.scalacheck" %% "scalacheck" % "1.13.4" % "test",
      "org.scalatest" %% "scalatest" % "3.0.3" % "test",
      "org.scalactic" %% "scalactic" % "3.0.3" % "test"
    )
  )
  .dependsOn(core, generic, tests % Test)

lazy val core = project.in(file("modules/core"))
  .settings(settings)
  .settings(
    name := "Protoless core"
  )

lazy val tests = project.in(file("modules/tests"))
  .settings(settings)
  .settings(
    name := "Protoless tests",
    libraryDependencies ++= Seq(
      "org.scalactic" %% "scalactic" % "3.0.3",
      "org.scalacheck" %% "scalacheck" % "1.13.5"
    )
  ).dependsOn(core)

lazy val generic = project.in(file("modules/generic"))
  .settings(settings)
  .settings(
    name := "Protoless generic"
  )
  .dependsOn(core, tests % Test)


lazy val noPublishSettings = Seq(
  publish := (),
  publishLocal := (),
  publishArtifact := false
)

lazy val docSettings = Seq(
  micrositeName := "protoless",
  micrositeDescription := "A Protobuf library for Scala powered by Cats and Shapeless",
  micrositeAuthor := "Julien Lafont",
  micrositeHighlightTheme := "atom-one-light",
  micrositeHomepage := "https://www.toto.fr",
  micrositeBaseUrl := "protoless",
  micrositeDocumentationUrl := "api",
  micrositeGithubOwner := "studiodev",
  micrositeGithubRepo := "protoless",
  micrositePalette := Map(
    "brand-primary" -> "#5B5988",
    "brand-secondary" -> "#292E53",
    "brand-tertiary" -> "#222749",
    "gray-dark" -> "#49494B",
    "gray" -> "#7B7B7E",
    "gray-light" -> "#E5E5E6",
    "gray-lighter" -> "#F4F3F4",
    "white-color" -> "#FFFFFF"),
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
  git.remoteRepo := "git@github.com:studiodev/aeroless.git",
  includeFilter in makeSite := "*.html" | "*.css" | "*.png" | "*.jpg" | "*.gif" | "*.svg" | "*.js" | "*.swf" | "*.yml" | "*.md"
)

lazy val docs = project.dependsOn(protoless, core, generic)
  .enablePlugins(ScalaUnidocPlugin)
  .enablePlugins(GhpagesPlugin)
  .enablePlugins(MicrositesPlugin)
  .settings(settings)
  .settings(docSettings)
  .settings(noPublishSettings)


lazy val jvmProjects = Seq[Project](protoless, core, tests, generic)

addCommandAlias("validate", ";protoless/test;test:scalastyle;docs/unidoc")
