package kmeans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class KmeansCluster implements ICluster {

    private final int clusterId;
    private final List<KmeansDataPoint> dataPoints = new ArrayList<>();
    private final int dimensions;
    private DataPoint clusterCenter;
    
    public KmeansCluster(int clusterId, int dimensions) {
        this.clusterId = clusterId;
        this.dimensions = dimensions;
    }

    void removeDataPoint(KmeansDataPoint dataPoint) {
        dataPoints.remove(dataPoint);
    }

    void addDataPoint(KmeansDataPoint dataPoint) throws KmeansException {
        if(dataPoints.contains(dataPoint))
            throw new KmeansException("Cannot add dataPoint to KmeansCluster because it's already contained in this cluster");
        
        if(dataPoint.getCluster() != this)
            throw new KmeansException("Cannot add dataPoint to KmeansCluster because it's already contained in another cluster");
        
        if(dataPoint.getDataPoint().getDimensions() != this.dimensions)
            throw new KmeansException("Cannot add dataPoint to KmeansCluster because it's dimensions are unfitting");
        
        dataPoints.add(dataPoint);
    }
    
    public void updateClusterCenter() throws KmeansException {
        DataPoint center = new DataPoint(dimensions);
        
        for(int dimension = 0; dimension < dimensions; dimension ++) {
            final int finalDimension = dimension;
            double summedCoordinates = this.dataPoints.stream().mapToDouble(dp -> dp.getDataPoint().getSafe(finalDimension)).sum();
            double averagedCoordinates = summedCoordinates / this.dataPoints.size();
            
            center.set(dimension, averagedCoordinates);
        }
        
        this.clusterCenter = center;
    }
        
    public DataPoint[] getDataPoints() {
        return this.dataPoints.stream().map(dp -> dp.getDataPoint()).toArray(DataPoint[]::new);
    }

    public int getClusterId() {
        return this.clusterId;
    }

    @Override
    public DataPoint getClusterCenter() {
        return this.clusterCenter;
    }
    
    public void setClusterCenter(DataPoint center) {
        this.clusterCenter = center;
    }

    @Override
    public String toString() {
        return "KmeansCluster id=" + this.clusterId;
        
    }
    
    
}
