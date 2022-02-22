package relativitization.universe.mechanisms

import relativitization.universe.mechanisms.flocking.ReflectiveBoundary

object ABMFlockingMechanismLists : MechanismLists() {
    override val regularMechanismList: List<Mechanism> = listOf(
        ReflectiveBoundary
    )

    override val dilatedMechanismList: List<Mechanism> = listOf()
}