package relativitization.universe.mechanisms

import relativitization.universe.mechanisms.flocking.RestMassReset

object ABMFlockingMechanismLists : MechanismLists() {
    override val regularMechanismList: List<Mechanism> = listOf(
        RestMassReset
    )

    override val dilatedMechanismList: List<Mechanism> = listOf()
}