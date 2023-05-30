package relativitization.universe.flocking.mechanisms

import relativitization.universe.core.mechanisms.Mechanism
import relativitization.universe.core.mechanisms.MechanismLists
import relativitization.universe.flocking.mechanisms.components.ChangeVelocity
import relativitization.universe.flocking.mechanisms.components.RestMassReset

object DensitySpeedMechanismLists : MechanismLists() {
    override val regularMechanismList: List<Mechanism> = listOf(
        RestMassReset,
    )

    override val dilatedMechanismList: List<Mechanism> = listOf(
        ChangeVelocity,
    )
}