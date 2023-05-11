# Interstellar flocking model

## Run the simulation

This command run the main function in
`./simulations/src/main/kotlin/relativitization/abm/Flocking.kt`
to produce the simulation result at `simulations/data/Flocking.csv`:

```shell
./gradlew :simulations:run -PmainClass=relativitization.flocking.FlockingKt
```

You can use `-PprocessorCount` and `-PramPercentage` to limit cpu usage and ram usage respectively:

```shell
./gradlew :simulations:run -PmainClass=relativitization.flocking.FlockingKt -PprocessorCount=2 -PramPercentage=25
```

## A brief introduction to the model

[Flocking](https://en.wikipedia.org/wiki/Flocking_(behavior))
is one of the most studied behaviour in agent-based modeling.
Typically, a few simple assumptions are made about the movement of flocks,
then the emergent alignment pattern can be observed and analyzed by simulation.

This is a special flocking model, where "flocks" are spaceships in a space at the scale
of light years.
Spaceships are propelled by [photon rocket](https://en.wikipedia.org/wiki/Photon_rocket)
to make them physically more realistic.
The model is constructed on top of the
[Relativitization](https://github.com/Adriankhl/relativitization)
to account for the relativistic physics.

The code of the model is under the `./model` subproject,
and the `./simulations` contains several functions which run simulations of the model.

### Data

The model-specific data are defined in
`./model/src/main/kotlin/relativitization/universe/flocking/data/components/ABMFlockingData.kt`.
`MutableABMFlockingData` is the mutable version of the data, and it is annotated by
`@GenerateImmutable` to generate an immutable counterpart. The `@SerialName("ABMFlockingData")`
annotation changes the name of the serialized data to `ABMFlockingData`.

### Command

The `ABMFlockingChangeVelocityCommand` is defined in
`./model/src/main/kotlin/relativitization/universe/flocking/data/commands/ABMFlockingCommands.kt`.
A spaceship can send this command to let itself or other spaceships to change the velocity.

### Mechanisms

There is only one simple mechanism in this model, `RestMassReset`, in
`./model/src/main/kotlin/relativitization/universe/flocking/mechanisms/components/RestMassReset.kt`.
This mechanism reset the mass of a spaceship regularly such that the photon rocket will never
run out of fuel.
As defined in `ABMFlockingMechanismLists` in
`./model/src/main/kotlin/relativitization/universe/flocking/mechanisms/ABMFlockingMechanismLists.kt`.
this is a regular mechanism where it is not affected by time dilation, i.e., it is executed
exactly once per turn.

### AI

The most interesting part of this model goes into the AI.
There are 3 different AI here:

* `ABMFlockingAI` in
  `./model/src/main/kotlin/relativitization/universe/flocking/ai/ABMFlockingAI`
* `ABMFlockingDensitySpeedAI` in
  `./model/src/main/kotlin/relativitization/universe/flocking/ai/ABMFlockingDensitySpeedAI`
* `ABMFlockingSVMAI` in
  `./model/src/main/kotlin/relativitization/universe/flocking/ai/ABMFlockingSVMAI`

AI of a spaceship input what the spaceship can see and send a `ABMFlockingChangeVelocityCommand`
to itself to change its velocity.
Different AI determine the target velocity differently.

### Universe generation

`ABMFlockingGenerate` in
`./model/src/main/kotlin/relativitization/universe/flocking/generate/ABMFlockingGenerate.kt`
generate the universe of the model.
The `generate()` function determines the initial conditions (e.g., positions, velocities, rest mass)
of the spaceships,
the AI (`aiName`) of the spaceships,
and the shape of the universe.
The function put all the necessary data components into the spaceship objects,
put all the spaceship objects into the 4D universe,
and output a `UniverseData` as the initial state of the universe.

### Initializing the framework

Before we run any simulation,
we need to initialize the framework.
`FlockingInitializer` in
`./model/src/main/kotlin/relativitization/universe/flocking/FlockingInitializer`
defines a `initialize()` function that should be called in a `main()` function
before any simulation.
The `initialize()` function registers the AI, the universe generation, and the mechanism
we defined previously
Additionally, the function registers `ksergen.GeneratedModule.serializersModule`,
which is generated automatically everytime the code is compiled,
and it is needed to handle the serialization of data components and commands.

### Simulation

Each `Flocking*.kt` file under the `simulations` subproject defines a `main()` function,
which runs a set of simulation,
including both simple single runs and parameter scans.

## License

The source code is licensed under the [GPLv3 License](./LICENSE.md).

        Copyright (C) 2022-2023  Lai Kwun Hang

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <https://www.gnu.org/licenses/>.
