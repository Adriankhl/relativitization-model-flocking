package relativitization.universe.mechanisms.flocking

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.MutableABMFlockingData
import relativitization.universe.data.components.abmFlockingData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism

object RestMassIncrease : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {

        val flockingData: MutableABMFlockingData =
            mutablePlayerData.playerInternalData.abmFlockingData()

        flockingData.restMassFraction = flockingData.lastRestMass / flockingData.restMass

        flockingData.restMass += 1.0
        flockingData.lastRestMass = flockingData.restMass

        return listOf()
    }
}