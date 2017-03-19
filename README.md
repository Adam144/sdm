# sdm

run with: ```java -jar dist/Kmeans.jar```

if you want help, use ```-h```, this shows the available command line parameters:

```
usage: utility-name
 -c,--clusters-generation <arg>   count of clusters generated (default=3)
 -d,--dimensions <arg>            dimensionality of the data points
                                  (default=2)
 -k,--clusters-clustering <arg>   the k for k-means clustering (default=3)
 -m,--macqueen                    use MacQueen instead of Lloyd
 -o,--output-filename <arg>       print datapoints and classification into
                                  csv file
 -p,--datapoints-count <arg>      the amount of generated datapoints per
                                  cluster (default=100)
 -r,--random-points               use initialization strategy 'random
                                  points as initial centroids'
 -s,--seed <arg>                  set specific random seed (integer)

```

If you provide ```-s``` parameter, you get reproducible results (the random numbers are always the same, so also the generated data points should be the same, as long as you don't vary with the other parameters)

The whole thing was developed with NetBeans IDE 8.2, so there are also the project files in the git repo checked in
