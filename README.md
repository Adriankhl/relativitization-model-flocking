# Flocking model example

## Run the simulation

If you haven't cloned the [Relativitization](https://github.com/Adriankhl/relativitization) repo.

```
cd ..
git clone https://github.com/Adriankhl/relativitization.git
cd relativitization-model-flocking
```

Copy all essential files from Relativitization:

```
cp -r ../relativitization/{buildSrc,universe,simulations,*kts,gradle*} .
```

Run:
```
./gradlew :simulations:run -PmainClass=relativitization.abm.FlockingWithFuelProductionKt
```