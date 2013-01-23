Eclipse Extension Generator
===========================

Annotate your Eclipse extensions and automatically generate plugin.xml files.

Supported Extensions:
* `@Application` - Extension of `org.eclipse.core.runtime.applications`
* `@Perspective` - Extension of `org.eclipse.ui.perspective`
* `@View` - Extension of `org.eclipse.ui.views`
* `@Editor` - Extension of `org.eclipse.ui.editors`

The Eclipse Extension Generator API is designed to be easily integrated into build tools like [SBuild](http://sbuild.tototec.de).

The Eclipse Extension Generator processes class files, and can therefore work on classes generated by various JVM languages, including Java and Scala and possible others. Let us know, If you can confirm support of your favorite JVM language.


License
-------

[Apache License Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

Annotating your Extensions
--------------------------

Simply add the annotation to your class implementing an *Extension Point*, e.g. to implement an Application (Java example):

```Java
@Application(name = "My RCP Application")
public class RcpApplication implements IApplication {
...
```

Generating a plugin.xml
-----------------------

Scan all class files in "target/classes" and generate the content of the plugin.xml (Scala example):

```Scala
import de.tototec.eclipse.extensiongenerator._
val pluginXml = new PluginXmlBuilder(scanDirs = Seq(Path("target/classes"))).build
// write into file, e.g. when used in a SBuild build file
AntEcho(file = Path("target/classes/plugin.xml"), message = pluginXml)
```

Dependencies
------------

The annotations are written in Java and have no dependencies besides an Java 5.

The generator needs the following dependencies on its classpath:

* Scala 2.10
* Javassist 3.16

The annotations in `de.tototec.eclipse.extensiongenerator.annotation` package have the class retention policy, which means they are retained in the class files but are not needed at runtime. So, at runtime you will not have to add them to the classpath. 

Unfortunatelly, older and current (2.10.0) Scala compilers have a bug and those annotations end up effectively with runtime retention. Here is the ticket: [SI-4788](https://issues.scala-lang.org/browse/SI-4788). Vote for it or even better, fix it, if that is not acceptable for you.

Downloads
---------

In its current state, there are no binary releases. After some stabilzation and the addition of some more extension points as annotations, Eclipse Extension Generator will be release to Maven Central repository.

The Annotations:
```xml
<dependency>
  <groupId>de.tototec</groupId>
  <artifactId>de.tototec.eclipse.extensiongenerator.annotation</artifactId>
  <version>${eegVersion}</version>
</dependency>
```

The Generator:
```xml
<dependency>
  <groupId>de.tototec</groupId>
  <artifactId>de.tototec.eclipse.extensiongenerator</artifactId>
  <version>${eegVersion}</version>
</dependency>
```
