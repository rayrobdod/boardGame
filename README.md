# BoardGame
[![Build Status](https://travis-ci.org/rayrobdod/boardGame.svg?branch=master)](https://travis-ci.org/rayrobdod/json)
[![Coverage Status](https://coveralls.io/repos/rayrobdod/boardGame/badge.svg?branch=master&service=github)](https://coveralls.io/github/rayrobdod/boardGame?branch=master)
[![Build status](https://ci.appveyor.com/api/projects/status/vtv458lxe8cd54rv/branch/master?svg=true)](https://ci.appveyor.com/project/rayrobdod/boardgame/branch/master)

In short, a model for tile-based games, and views for those games.

The model is designed to be agnostic to the number of connections between tiles
(e.g. it should handle hex, square, and more exotic forms equally well), but
does have special forms for square and hex. 

but as I've only used the square models personally, this only contains a view
for rectangular boards.


## Build Instructions
This repository uses [sbt](http://www.scala-sbt.org/) as its build tool.

The dependencies can be found by sbt automatically, but only scala itself is
used something other than some kind of testing harness.


