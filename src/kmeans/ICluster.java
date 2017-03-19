package kmeans;

public interface ICluster {
    public DataPoint[] getDataPoints();
    public int getClusterId();
    public DataPoint getClusterCenter();
}
