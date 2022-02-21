package relativitization.universe.mechanisms

import relativitization.universe.mechanisms.flocking.FuelProduction

object ABMFlockingMechanismLists : MechanismLists() {
    override val regularMechanismList: List<Mechanism> = listOf(
        FuelProduction
    )

    override val dilatedMechanismList: List<Mechanism> = listOf()
}