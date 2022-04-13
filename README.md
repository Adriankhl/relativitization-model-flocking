# Flocking model example

## Run the simulation

If you haven't cloned the [Relativitization](https://github.com/Adriankhl/relativitization) repo,
clone that somewhere out of this directory.

```
cd ..
git clone https://github.com/Adriankhl/relativitization.git

```

Run `createModelBase` task in `relativitization` to extract essential files to 
`relativitization-model-base`.

```
cd relativitization
./gradlew createModelBase
```

Copy all essential files from Relativitization.

```
cd ../relativitization-model-flocking
cp ../relativitization/relativitization-model-base/* .
```

Now you can open this project with your favourite IDE that supports gradle, or you can try to run
the simulation on the command line:

```
./gradlew :simulations:run -PmainClass=relativitization.abm.FlockingWithSupplyKt
```