JKit - Custom Pen
=================

A small library to use custom defined pens to draw Java-Shapes.

This project is build with Maven. Use `mvn install` to generate it as a jar
in the *target/* directory. Alternatively the project can be used as dependency
in other Maven projects (see [below] (#maven-integration)).

To use custom shape drawing an instance of `jkit.gfx.AbstractShapeDrawer` has to be used
to draw shapes instead of `java.awt.Graphics2D#draw(java.awt.Shape)`.

    Graphics2D g = ...;
    g.draw(shape);

may be converted to

    Graphics2D g = ...;
    AbstractShapeDrawer shapeDrawer = AbstractShapeDrawer.getShapeDrawerForPen(...);
    shapeDrawer.draw(g, shape);

In the package `jkit.example` is an example that shows
how to use various custom pens.

### Maven Integration

In order to use JKanvas within a Maven project you can use the following dependency
(in the `<dependencies>` section -- note that the *X.X.X* in the version tag
must be replaced with the current version):

    <dependency>
      <groupId>joschi-mvn</groupId>
      <artifactId>CustomPen</artifactId>
      <version>X.X.X</version>
    </dependency>

However, this requires an additional repository in the repositories section (`<repositories>`) of the pom.xml file:

    <repository>
      <id>joschi</id>
      <url>http://merkur57.inf.uni-konstanz.de/~krause/mvn/releases</url>
    </repository>

When using [Eclipse] (http://www.eclipse.org/) the current snapshot can be used
by having the JKit-CustomPen project open and altering the version of the dependency
to *X.X.X-SNAPSHOT* where *X.X.X* is the upcoming version.
