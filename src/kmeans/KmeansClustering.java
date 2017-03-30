package kmeans;

import java.util.Arrays;
import java.util.Random;

public class KmeansClustering {

    protected final KmeansDataPoint[] dataPoints;
    private final int dimensions;
    protected final int k;
    
    private final int maxRounds = 1000;
    private int roundCount = 0;
    protected Random random;
    protected KmeansCluster[] clusters;
    
    private boolean updateAfterEachPoint = false;
    private boolean updateAfterEachRound = true;
    
    private boolean clustersChangedDuringRound;
    private final InitializationStrategy strategy;
    
    public enum InitializationStrategy {
        RANDOM_PARTITION,
        RANDOM_POINTS
    } 
    
    public enum Algorithm {
        LLOYD,
        MACQUEEN
    }
        
    public KmeansClustering(DataPoint[] dataPoints, int dimensions, int k, int seed, InitializationStrategy strategy, Algorithm algorithm) {
        this.dataPoints = Arrays.stream(dataPoints).map(dp -> new KmeansDataPoint(dp)).toArray(size -> new KmeansDataPoint[size]);
        this.dimensions = dimensions;
        this.k = k;
        
        this.random = seed == -1 ? new Random() : new Random(seed);
        this.strategy = strategy;
        
        if(algorithm == Algorithm.MACQUEEN) {
            this.updateAfterEachPoint = true;
            this.updateAfterEachRound = true;
        }
    }
    
    public void run() throws KmeansException {
        initializeClusters();
                
        do {
            if(this.roundCount > this.maxRounds)
                throw new KmeansException("Exceeded maximum amount of rounds -> doesn't converge");
            
            runRound();
            this.roundCount ++;
        } while (!isConverged());
        
        System.out.println("=== Converged after " + this.roundCount + " rounds ===");
    }
    
    public void initializeClusters() throws KmeansException {
        this.clusters = new KmeansCluster[this.k];
        
        for(int i = 0; i < this.k; i++) {
            this.clusters[i] = new KmeansCluster(i, this.dimensions);
        }
        
        if(this.strategy == strategy.RANDOM_PARTITION) {
            // randomly assign the points to clusters and then calculate the centroids
            for(KmeansDataPoint dp : this.dataPoints) {
                KmeansCluster randomCluster = this.clusters[random.nextInt(this.k)];
                dp.setCluster(randomCluster);
            }

            for(KmeansCluster cluster : this.clusters) {
                cluster.updateClusterCenter();
            }
        } else {
            // choose random points as initial centroids
            for(KmeansCluster cluster : this.clusters) {
                DataPoint center = this.dataPoints[this.random.nextInt(this.dataPoints.length)].getDataPoint();
                cluster.setClusterCenter(center);
            }
        }        
        
    }
    
    public void runRound() throws KmeansException {
        this.clustersChangedDuringRound = false;
        
        for(KmeansDataPoint point : this.dataPoints) {
            KmeansCluster closestCluster = findClosestCluster(point);
            KmeansCluster lastAssignedCluster = point.getCluster();
            
            if(lastAssignedCluster != closestCluster)
                this.clustersChangedDuringRound = true;
            
            point.setCluster(closestCluster);
                        
            if(updateAfterEachPoint) {
                // MacQueen
                closestCluster.updateClusterCenter();
                if(lastAssignedCluster != null)
                    lastAssignedCluster.updateClusterCenter(); //update the cluster which "lost" the DataPoint
            }
        }
        
        if(updateAfterEachRound) {
            // Lloyd
            for(KmeansCluster cluster : this.clusters) {
                cluster.updateClusterCenter();
            }
        }
    }
    
    public KmeansCluster findClosestCluster(KmeansDataPoint dataPoint) throws KmeansException {
        double currentDistance = 0;
        KmeansCluster closestCluster = null;
        
        for(KmeansCluster cluster : this.clusters) {
          double distance = dataPoint.getDataPoint().getDistance(cluster.getClusterCenter());
          if(distance < currentDistance || closestCluster == null) {
              closestCluster = cluster;
              currentDistance = distance;
          }
        }
        
        return closestCluster;
    }
        
    public boolean isConverged() {
        return !clustersChangedDuringRound;
    }
    
}
