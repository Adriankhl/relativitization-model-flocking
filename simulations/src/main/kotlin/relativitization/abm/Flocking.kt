package relativitization.abm

import relativitization.universe.Universe
import relativitization.universe.ai.ABMFlockingSVMAI
import relativitization.universe.ai.name
import relativitization.universe.data.MutableUniverseSettings
import relativitization.universe.data.commands.AllCommandAvailability
import relativitization.universe.data.commands.name
import relativitization.universe.generate.method.GenerateSettings
import relativitization.universe.generate.method.GenerateUniverseMethodCollection
import relativitization.universe.generate.method.abm.ABMFlockingGenerate
import relativitization.universe.generate.method.name
import relativitization.universe.global.EmptyGlobalMechanismList
import relativitization.universe.global.name
import relativitization.universe.mechanisms.ABMFlockingMechanismLists
import relativitization.universe.mechanisms.name

fun main() {
    val generateSetting = GenerateSettings(
        generateMethod = ABMFlockingGenerate.name(),
        numPlayer = 50,
        numHumanPlayer = 0,
        otherIntMap = mutableMapOf(),
        otherDoubleMap = mutableMapOf(
            "coreRestMass" to 0.0,
            "initialFuelRestMass" to 1.0,
        ),
        otherStringMap = mutableMapOf(
            "aiName" to ABMFlockingSVMAI.name(),
        ),
        universeSettings = MutableUniverseSettings(
            universeName = "Flocking",
            commandCollectionName = AllCommandAvailability.name(),
            mechanismCollectionName = ABMFlockingMechanismLists.name(),
            globalMechanismCollectionName = EmptyGlobalMechanismList.name(),
            speedOfLight = 1.0,
            xDim = 10,
            yDim = 10,
            zDim = 3,
            otherDoubleMap = mutableMapOf(
                "nearByRadius" to 3.0,
                "maxAnglePerturbation" to 0.1,
                "flockVelocity" to 0.5,
            ),
        )
    )

    val universe = Universe(GenerateUniverseMethodCollection.generate(generateSetting), ".")

    for (turn in 1..1000) {
        println("Turn: $turn")
        universe.pureAIStep()
    }
}