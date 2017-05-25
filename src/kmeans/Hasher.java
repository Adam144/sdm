/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kmeans;
import java.util.Random;

/**
 *
 * @author adam.vondracek
 */
public class Hasher {
    
    private DataPoint[] dataPoints;
    private DataPoint[] hashes;
    private int numberOfHashes;
    private int dimensionOfPoints;
    
    public Hasher(DataPoint[] dataPoints, int numberOfHashes) throws KmeansException {
        this.dataPoints = dataPoints;
        this.hashes = new DataPoint[this.dataPoints.length];
        
        if (numberOfHashes < 1) {
            throw new KmeansException("Number of hashes must be greater then 0.");
        }
        this.numberOfHashes = numberOfHashes;
        this.dimensionOfPoints = dataPoints.length > 0 ? dataPoints[0].getDimensions() : 0;
        
        for (int i = 0; i < this.dataPoints.length; i++){
            this.hashes[i] = new DataPoint(numberOfHashes);
        }
    }
    
    private double getHashOfPoint(DataPoint point, double[] hashFunction) throws KmeansException {
        double hashValue = 0;
        for (int i = 0; i < this.dimensionOfPoints; i++){
            hashValue += point.get(i)*hashFunction[i];
        }
        return hashValue;
    }
    
    private double[] generateHashFunction(int dimension){
        double[] fnc = new double[dimension];
        Random random = new Random();
        for (int i = 0; i < dimension; i++){
            fnc[i] = random.nextGaussian();
        }
        return fnc;
    }
    
    public DataPoint[] getHashes() throws KmeansException {
        double[] hashFunction; //hash function is vector of coeficient from N(0,1)
        for (int i = 0; i < this.numberOfHashes; i++){
            hashFunction = generateHashFunction(this.dimensionOfPoints);
            for (int j = 0; j < this.dataPoints.length; j++){
                double hash = getHashOfPoint(this.dataPoints[j], hashFunction);
                this.hashes[j].set(i, hash);
            }
        }
        return this.hashes;
    }
}
