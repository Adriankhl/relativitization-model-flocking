package relativitization.universe.flocking

import ksergen.serializers.module.GeneratedModule
import relativitization.universe.core.RelativitizationInitializer
import relativitization.universe.flocking.ai.ABMFlockingAI
import relativitization.universe.flocking.ai.ABMFlockingDensitySpeedAI
import relativitization.universe.flocking.ai.ABMFlockingSVMAI
import relativitization.universe.flocking.generate.ABMFlockingGenerate
import relativitization.universe.flocking.mechanisms.ABMFlockingMechanismLists

object FlockingInitializer {
    fun initialize() {
        RelativitizationInitializer.initialize(
            serializersModule = GeneratedModule.serializersModule,
            generateUniverseMethod = ABMFlockingGenerate,
            mechanismLists = ABMFlockingMechanismLists,
        )

        RelativitizationInitializer.initialize(
            aiList = listOf(
                ABMFlockingAI,
                ABMFlockingSVMAI,
                ABMFlockingDensitySpeedAI,
            )
        )
    }
}