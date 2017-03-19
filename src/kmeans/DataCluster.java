package kmeans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataCluster implements ICluster {
    private final List<DataPoint> dataPoints = new ArrayList<>();
    private final int clusterId;
    private final DataPoint clusterCenter;
    
    public DataCluster(int clusterId, DataPoint clusterCenter) {
        this.clusterId = clusterId;
        this.clusterCenter = clusterCenter;
    }
    
    public void add(DataPoint dp) throws KmeansException {
        dp.setCluster(this);
        this.dataPoints.add(dp);
    }
    
    public DataPoint getClusterCenter() {
        return this.clusterCenter;
    }
    
    public DataPoint[] getDataPoints() {
       return this.dataPoints.toArray(new DataPoint[this.dataPoints.size()]);
    }
    
    public int getClusterId() {
        return this.clusterId;
    }
}
