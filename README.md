# BoardGame
[![Build Status](https://travis-ci.org/rayrobdod/boardGame.svg?branch=master)](https://travis-ci.org/rayrobdod/boardGame)
[![Coverage Status](https://coveralls.io/repos/rayrobdod/boardGame/badge.svg?branch=master&service=github)](https://coveralls.io/github/rayrobdod/boardGame?branch=master)
[![Build status](https://ci.appveyor.com/api/projects/status/vtv458lxe8cd54rv/branch/master?svg=true)](https://ci.appveyor.com/project/rayrobdod/boardgame/branch/master)

A model and rendering engine for tile-based games

The libraries abstract over

* Space data (tparams: SpaceClass)
* Shape of the space (tparams: Repr / SpaceType, Index, Dimension)
* Rendering tecnhology (tparams: Icon, IconPart, Component)

### Space data

This is treated as entirely user-defined.

The intention is that this describes the properties of the space – what is
allowed to enter the space, how much extra defense a unit gets for standing on
the space, things like that.

The not-trivial tilesheet is based on
[View-Tile Rulesets](http://www.squidi.net/three/entry.php?id=166);
I'd recommend against including "use tile \#38" as a part of this data.

### Shape

There are subclasses of Space which have methods to describe the adjacent space
in each direction. RectangularSpaces have `north`, `south`, `east` and `west`,
for example.

### Renderer

Swing, JavaFx, LWJGL (theoretically), etc.

Implementations of the per-renderer things are in the ViewJavaFx and ViewSwing
subprojects, in the `view.Javafx` and `view.Swing` objects.


