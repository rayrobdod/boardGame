/*
	Deduction Tactics
	Copyright (C) 2012-2015  Raymond Dodge

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
import sbt._
import Keys._

object MyBuild extends Build {
	
	private val coverageDisabledSettings = {
		if (System.getProperty("scoverage.disable", "") != "true") {
			Nil
		} else {
			Seq(
				TaskKey[Unit]("coverage") := {},
				TaskKey[Unit]("coveralls") := {}
			)
		}
	}
	
	lazy val root = Project(
			id = "boardgame",
			base = file("."),
			settings = Defaults.coreDefaultSettings ++
					coverageDisabledSettings
	)
}
