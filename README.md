Eclipse Extension Generator
===========================

Annotate your Eclipse extensions and automatically generate plugin.xml files.

Supported Extensions:
* @Application - Extension of org.eclipse.core.runtime.applications
* @Perspective - Extension of org.eclipse.ui.perspective
* @View - Extension of org.eclipse.ui.views

The Eclipse Extension Generator API is designed to be easily integrated into build tools like [SBuild](http://sbuild.tototec.de).

License: [Apache License Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

Example: Scan all class files in "target/classes" and generate the content of the plugin.xml.

```
import de.tototec.eclipse.extensiongenerator._
val pluginXml = new PluginXmlBuilder(scanDirs = Seq(Path("target/classes"))).build
```

Dependencies:
* Scala 2.10
* Javassist 3.16
