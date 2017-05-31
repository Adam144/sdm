package kmeans;

import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class KmeansClustering {

    public int same_number = 0;
    protected final KmeansDataPoint[] dataPoints;
    protected final KmeansDataPoint[] hashDataPoints;
    private Hasher hasher;
    private final int dimensions;
    protected final int k;
    
    private final int maxRounds = 200;
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
        
    public KmeansClustering(DataPoint[] dataPoints, int dimensions, int k, int seed, InitializationStrategy strategy, Algorithm algorithm) throws KmeansException{
        this.dataPoints = Arrays.stream(dataPoints).map(dp -> new KmeansDataPoint(dp)).toArray(size -> new KmeansDataPoint[size]);
        this.hasher = new Hasher(dataPoints, 4);//TODO 4=numberOfhashes as param 
        this.hashDataPoints = Arrays.stream(hasher.getHashes()).map(dp -> new KmeansDataPoint(dp)).toArray(size -> new KmeansDataPoint[size]);
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
            System.out.println("round: "+this.roundCount);
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
        KmeansDataPoint point;
        KmeansDataPoint hashPoint;
        for(int i = 0; i < this.dataPoints.length; i++) {
            point = this.dataPoints[i];
            hashPoint = this.hashDataPoints[i];
            KmeansCluster closestCluster = findClosestCluster(point, hashPoint);
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
    
    public KmeansCluster findClosestCluster(KmeansDataPoint dataPoint, KmeansDataPoint hashPoint) throws KmeansException {
        double currentDistance = 0;
        KmeansCluster closestCluster = null;
        double distance;
        for(KmeansCluster cluster : this.clusters) {
          //double distance = dataPoint.getDataPoint().getDistance(cluster.getClusterCenter());
          distance = isInSameBucket(hashPoint, this.hasher.getAllHashesOfPoint(cluster.getClusterCenter())) ? 0 : dataPoint.getDataPoint().getDistance(cluster.getClusterCenter());
          if(distance < currentDistance || closestCluster == null) {
              closestCluster = cluster;
              currentDistance = distance;
              if (distance == 0) {break;}
          }
        }
        
        return closestCluster;
    }
    
    private boolean isInSameBucket(KmeansDataPoint hashPoint, DataPoint hashCenter) throws KmeansException{
        int numberOfBlocks = 2;
        int rowsPerBlock = hashCenter.getDimensions()/numberOfBlocks;
        boolean same = true;
        for (int b = 0; b < numberOfBlocks; b++) {
            for (int r = 0; r < rowsPerBlock; r++) {
                if (((int) hashPoint.getDataPoint().get(b*rowsPerBlock + r)/10) != ((int)hashCenter.get(b*rowsPerBlock + r)/10)){//buckets are [0,10][10,20]...
                    same = false;
                    break;
                }
            }
            if (same == true){
                same_number++;
                return true;
            }
            same = true;
        }
        return false;
    }
    
    public boolean isConverged() {
        return !clustersChangedDuringRound;
    }
    
    public double getClusteringError(List<Integer> reality) {
        
        System.out.println(this.same_number + " same buckets were founded.");
        ArrayList<Integer> results = new ArrayList<Integer>();
        for (int i = 0; i < this.dataPoints.length; i++){
            results.add(this.dataPoints[i].getCluster().getClusterId());
        }
        return CalculateNMI.NMI(new ArrayList<Integer>(reality), results);
    }
    
}
