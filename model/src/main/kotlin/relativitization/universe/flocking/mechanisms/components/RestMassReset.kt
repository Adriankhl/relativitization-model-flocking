package relativitization.universe.flocking.mechanisms.components

import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.commands.Command
import relativitization.universe.core.data.global.UniverseGlobalData
import relativitization.universe.core.mechanisms.Mechanism
import relativitization.universe.flocking.data.components.MutableABMFlockingData
import relativitization.universe.flocking.data.components.abmFlockingData
import kotlin.random.Random

object RestMassReset : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random,
    ): List<Command> {

        val flockingData: MutableABMFlockingData =
            mutablePlayerData.playerInternalData.abmFlockingData()

        flockingData.restMassFraction = flockingData.restMass
        flockingData.restMass = 1.0

        return listOf()
    }
}