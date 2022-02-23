package relativitization.universe.mechanisms

import relativitization.universe.mechanisms.flocking.RestMassIncrease

object ABMFlockingMechanismLists : MechanismLists() {
    override val regularMechanismList: List<Mechanism> = listOf(
        RestMassIncrease
    )

    override val dilatedMechanismList: List<Mechanism> = listOf()
}