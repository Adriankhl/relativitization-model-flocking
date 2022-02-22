package relativitization.universe.mechanisms.flocking

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.abmFlockingData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism
import kotlin.math.abs

object ReflectiveBoundary : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {

        // Reflective boundary
        if (mutablePlayerData.double4D.x < 1.0) {
            mutablePlayerData.velocity.vx = abs(mutablePlayerData.velocity.vx)
        }

        if (mutablePlayerData.double4D.x > universeSettings.xDim.toDouble() - 1.0) {
            mutablePlayerData.velocity.vx = -abs(mutablePlayerData.velocity.vx)
        }

        if (mutablePlayerData.double4D.y < 1.0) {
            mutablePlayerData.velocity.vy = abs(mutablePlayerData.velocity.vy)
        }

        if (mutablePlayerData.double4D.y > universeSettings.yDim.toDouble() - 1.0) {
            mutablePlayerData.velocity.vy = -abs(mutablePlayerData.velocity.vy)
        }

        if (mutablePlayerData.double4D.z < 1.0) {
            mutablePlayerData.velocity.vz = abs(mutablePlayerData.velocity.vz)
        }

        if (mutablePlayerData.double4D.z > universeSettings.zDim.toDouble() - 1.0) {
            mutablePlayerData.velocity.vz = -abs(mutablePlayerData.velocity.vz)
        }

        return listOf()
    }
}