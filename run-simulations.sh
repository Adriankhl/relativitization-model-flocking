#!/bin/bash

fileList=(
  "FlockingSpeedDensityParameterScan1.kt"
  "FlockingSpeedDensityParameterScan1Big.kt"
  "FlockingSpeedDensityParameterScan2.kt"
  "FlockingSpeedDensityParameterScan2Big.kt"
  "FlockingSpeedDensityParameterScan3.kt"
  "FlockingSpeedDensityParameterScan3Big.kt"
  "FlockingSpeedDensityParameterScan4.kt"
  "FlockingSpeedDensityParameterScan4Big.kt"
  "FlockingSpeedDensityParameterScan5Big.kt"
  "FlockingSpeedDensityParameterScan6Big.kt"
  "FlockingSpeedDensityParameterScan7Big.kt"
)

for fileName in "${fileList[@]}"; do
  className=${fileName::-3}
  ./gradlew :simulations:run -PmainClass=relativitization.flocking."$className"Kt -PprocessorCount="${1:-4}" -PramPercentage="${2:-10}"
done
