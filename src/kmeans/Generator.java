package kmeans;

import java.io.Console;
import java.util.Random;
import java.util.function.Supplier;

public class Generator {

    private final int clusterCount;
    private final int dataPointsCount;
    private final int dimensionality;
    private final Random random;
    private DataCluster[] clusters;
    
    private final double deviation = 0.5;
    
    public Generator(int clusterCount, int dataPointsCount, int dimensionality) {
        this(clusterCount, dataPointsCount, dimensionality, -1);
    }
    
    public Generator(int clusterCount, int dataPointsCount, int dimensionality, long seed) {
        this.clusterCount = clusterCount;
        this.dataPointsCount = dataPointsCount;
        this.dimensionality = dimensionality;
        this.clusters = new DataCluster[clusterCount];
        
        this.random = seed == -1 ? new Random() : new Random(seed);
    }
    
    public void generate() throws KmeansException {
        for(int clusterIndex = 0; clusterIndex < this.clusterCount; clusterIndex++) {
            DataPoint clusterCenter = generateSinglePoint(() -> this.random.nextDouble() * this.clusterCount);
            DataCluster cluster = new DataCluster(clusterIndex, clusterCenter);
            
            for(int dataPoint = 0; dataPoint < this.dataPointsCount; dataPoint++) {
                DataPoint dp = generateSinglePoint(() -> this.random.nextGaussian() * this.deviation);
                
                // move point towards its cluster center
                dp = dp.add(clusterCenter);
                cluster.add(dp);
            }
            
            clusters[clusterIndex] = cluster;
        }
    }
    
    private DataPoint generateSinglePoint(Supplier<Double> generator) throws KmeansException {
        DataPoint dp = new DataPoint(this.dimensionality);
        
        for(int dimension = 0; dimension < this.dimensionality; dimension++) {
            double value = generator.get(); //random.nextGaussian();
            dp.set(dimension, value);
        }
        
        return dp;
    }
    
    public DataCluster[] getClusters() {
        return this.clusters;
    }

    @Override
    public String toString() {
        return "NOT YET IMPLEMENTED - Generator.toString()";
        /*StringBuilder sb = new StringBuilder();
        
        for (int cluster = 0; cluster < this.clusterCount; cluster++) {
            sb.append(this.centers[cluster].toString());
            sb.append("\n");
        }

        return sb.toString();*/
    }
}
