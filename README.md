CutCAD
======


CutCAD is the result of our work for the Media Computing Project 2014. It is supposed to help you to build housings for your electronics projects, create boxes to store things or build other crazy things with your lasercutter.

For detailed information visit http://hci.rwth-aachen.de/CutCAD

Check out the documentation on http://laewahn.github.io/

How to compile it
-----------------

There are two ways for running CutCAD from source.

1. Using Eclipse
2. Using Ant

### Using Eclipse

If you are running Eclipse >= Kepler with Java >= 1.7, you can import the whole CutCAD project into your workspace by selecting File > Import > General/Existing Projects Into Eclipse.

### Using Ant

We included a build file for Ant in this project, with the following options:
- build: Builds all source files into the build folder (CutCAD/bin)
- jar: Creates an executable .jar file in the deployment folder (CutCAD/CutCAD)
- clean: Cleans the build folder 
- clean-jar: Cleans the deployment folder
- javadoc: Builds the documentation

Why CutCAD?
-----------

To create three dimensional objects with a lasercutter, there are different possible approaches: One can use easy to use tools such as BoxMaker BoxMaker, which can automatically generate boxes, but are not powerful enough to create more complex shapes.

The other option is to create the necessary vector graphics manually with tools such as Inkscape. Such vector graphics editors are very powerful and can be used to create any shape. However, building three-dimensional objects from shapes drawn in such editors requires both a lot of experience and a lot of manual work and calculations.

One of the main issues is the connection of the parts: A common approach is to create a structure of tenons on the outlines of shapes that allows two shapes to interlock and form a stable connection. However, creating such structures is a lot of work in a vector graphics tool such as inkscape: One has to calculate the correct sizes for tenons and make sure that the sides of both shapes fit together perfectly. This is one of the main problems that CutCAD tries to solve: Shapes can simply be connected in CutCAD and CutCAD will take care of the calculations necessary to create stable connections between the shapes.

Another problem we have encountered while working with vector graphics editors in combination with a lasercutter are angles: As soon as objects become more complex and not only use rectangular angles, calculating the correct length of sides and tenons becomes much more work. Again, CutCAD can help tremendously by taking care of the correct alignment of shapes and all calculations necessary.

Using CutCAD, the user does not need to have any knowledge about any of these calculations: Connecting two shapes is as easy as clicking on the edges that should be connected. CutCAD will then figure out on its own how to rotate and align the shapes in order to connect them and take care of creating the correct tenon-structure.


About CutCAD:
-------------

The basic approach of CutCAD is to assemble three dimensional shapes by connecting the edges of two-dimensional shapes. These shapes can either be created with tools provided by CutCAD or imported from svg-files. After a connection has been made, each shape will be automatically aligned in 3D space and the corresponding tenon structure will be created on the connected edges. When all shapes are connected and the 3D-object is complete, you can directly cut the necessary parts with the lasercutter or just export them into an SVG-file.


Team Members:
-------------
* [Dennis Lewandowski](https://github.com/laewahn)
* [Jan Thar](https://github.com/JanThar)
* [Mirko Hartmann](https://github.com/x2mirko)
* Pierre Schoonbrood 
