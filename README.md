# Flocking model example

## Run the simulation

This command run the main function in
`./simulations/src/main/kotlin/relativitization/abm/Flocking.kt` 
to produce the simulation result at `simulations/data/Flocking.csv`:

```
./gradlew :simulations:run -PmainClass=relativitization.flocking.FlockingKt
```

You can use `-PprocessorCount` and `-PramPercentage` to limit cpu usage and ram usage respectively:

```
./gradlew :simulations:run -PmainClass=relativitization.flocking.FlockingKt -PprocessorCount=2 -PramPercentage=25
```
