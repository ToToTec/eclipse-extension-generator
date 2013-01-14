import de.tototec.sbuild._
import de.tototec.sbuild.TargetRefs._
import de.tototec.sbuild.ant._
import de.tototec.sbuild.ant.tasks._

@version("0.3.0.9000")
@classpath("http://repo1.maven.org/maven2/org/apache/ant/ant/1.8.4/ant-1.8.4.jar")
class SBuild(implicit project: Project) {

  SchemeHandler("mvn", new MvnSchemeHandler())

  val scalaVersion = "2.10.0"

  val compileCp =
    s"mvn:org.scala-lang:scala-library:${scalaVersion}" ~
    "mvn:tv.cntt:annovention:1.3" ~
    "mvn:org.javassist:javassist:3.16.1-GA"

  ExportDependencies("eclipse.classpath", compileCp)

  val compilerCp =
    s"mvn:org.scala-lang:scala-library:${scalaVersion}" ~
    s"mvn:org.scala-lang:scala-compiler:${scalaVersion}" ~
    s"mvn:org.scala-lang:scala-reflect:${scalaVersion}"

  val bndCp = "mvn:biz.aQute:bndlib:1.50.0"

  val namespace = "de.tototec.eclipse.extensiongenerator"
  val version = "0.0.1"

  val generatorJar = s"target/${namespace}-${version}.jar"
  val annotationJar = s"target/${namespace}.annotation-${version}.jar"

  Target("phony:all") dependsOn "clean" ~ generatorJar ~ annotationJar

  Target("phony:clean") exec {
    AntDelete(dir = Path("target"))
  }

  Target("phony:compile") dependsOn compileCp ~ compilerCp exec { ctx: TargetContext =>
    IfNotUpToDate(Seq(Path("src/main/scala"), Path("src/main/java")), Path("target"), ctx) {
      AntMkdir(dir = Path("target/classes"))

      addons.scala.Scalac(
        compilerClasspath = compilerCp.files,
        classpath = compileCp.files,
        srcDirs = Seq(Path("src/main/scala"), Path("src/main/java")),
        destDir = Path("target/classes"),
        deprecation = true,
        encoding = "UTF-8",
        target = "jvm-1.5"
      )

      AntJavac(
        classpath = AntPath(locations = compileCp.files),
        destDir = Path("target/classes"),
        srcDir = AntPath("src/main/java"),
        source = "1.5",
        target = "1.5",
        encoding = "UTF-8",
        includeAntRuntime = false,
        deprecation = true,
        fork = true
      )
    }
  }

  Target(generatorJar) dependsOn "compile" ~ compileCp ~ bndCp exec { ctx: TargetContext =>
    addons.bnd.BndJar(
      destFile = ctx.targetFile.get,
      classpath = compileCp.files ++ Seq(Path("target/classes")),
      bndClasspath = bndCp.files,
      props = Map(
        "Bundle-SymbolicName" -> namespace,
        "Bundle-Version" -> version,
        "Export-Package" -> s"""${namespace};version="${version}"""",
        "Import-Package" -> s"""scalaVersion;version="[${scalaVersion},2.11)",
                                ${namespace}.annotation.*;version="${version}",
                                *""",
        "Scala-Version" -> scalaVersion
     )
    )
  }

  Target(annotationJar) dependsOn "compile" ~ compileCp ~ bndCp exec { ctx: TargetContext =>
    addons.bnd.BndJar(
      destFile = ctx.targetFile.get,
      classpath = compileCp.files ++ Seq(Path("target/classes")),
      bndClasspath = bndCp.files,
      props = Map(
        "Bundle-SymbolicName" -> s"${namespace}.annotation",
        "Bundle-Version" -> version,
        "Export-Package" -> s"""${namespace}.annotation;version="${version}"""",
        "Import-Package" -> "*"
     )
    )
  }

}
