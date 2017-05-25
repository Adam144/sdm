package kmeans;

import java.awt.Rectangle;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import org.apache.commons.cli.*;

public class KmeansApplication {
    private int clusterCountGeneration;
    private int dataPointsCountPerCluster;
    
    private int dimensions;
    private int seed;
    
    private int clusterCountKmeans;
    
    private KmeansClustering.Algorithm algorithm;
    private KmeansClustering.InitializationStrategy initializationStrategy;
    
    private String outputFilename;
    
    public static void main(String[] args) {
        try {
            KmeansApplication app = new KmeansApplication();
            app.init(args);
            app.run();
        } catch (KmeansException ex) {
            Logger.getLogger(KmeansApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void init(String[] args) {
        Options options = new Options();

        Option opt_clusters_generation = new Option("c", "clusters-generation", true, "count of clusters generated (default=3)");
        opt_clusters_generation.setRequired(false);
        options.addOption(opt_clusters_generation);

        Option opt_clusters_clustering = new Option("k", "clusters-clustering", true, "the k for k-means clustering (default=3)");
        opt_clusters_clustering.setRequired(false);
        options.addOption(opt_clusters_clustering);
        
        Option opt_datapoints = new Option("p", "datapoints-count", true, "the amount of generated datapoints per cluster (default=100)");
        opt_datapoints.setRequired(false);
        options.addOption(opt_datapoints);
        
        Option opt_dimensions = new Option("d", "dimensions", true, "dimensionality of the data points (default=2)");
        opt_dimensions.setRequired(false);
        options.addOption(opt_dimensions);
        
        Option opt_seed = new Option("s", "seed", true, "set specific random seed (integer)");
        opt_seed.setRequired(false);
        options.addOption(opt_seed);
        
        Option opt_macqueen = new Option("m", "macqueen", false, "use MacQueen instead of Lloyd");
        opt_macqueen.setRequired(false);
        options.addOption(opt_macqueen);
        
        Option opt_r = new Option("r", "random-points", false, "use initialization strategy 'random points as initial centroids'");
        opt_r.setRequired(false);
        options.addOption(opt_r);
        
        Option opt_output = new Option("o", "output-filename", true, "print datapoints and classification into csv file");
        opt_output.setRequired(false);
        options.addOption(opt_output);
        
        
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
            return;
        }

        this.clusterCountGeneration = Integer.parseInt(cmd.getOptionValue("clusters-generation", "3"));
        this.clusterCountKmeans = Integer.parseInt(cmd.getOptionValue("clusters-clustering", "3"));
        this.dimensions = Integer.parseInt(cmd.getOptionValue("dimensions", "2"));
        this.dataPointsCountPerCluster = Integer.parseInt(cmd.getOptionValue("datapoints-count", "100"));
        this.seed = Integer.parseInt(cmd.getOptionValue("seed", "-1"));
        
        this.initializationStrategy = cmd.hasOption("random-points") ? initializationStrategy.RANDOM_POINTS : initializationStrategy.RANDOM_PARTITION;
        this.algorithm = cmd.hasOption("macqueen") ? algorithm.MACQUEEN : algorithm.LLOYD;
        
        this.outputFilename = cmd.getOptionValue("output-filename");
    }
    
    public void run() throws KmeansException {
        Generator gen = new Generator(
            this.clusterCountGeneration,
            this.dataPointsCountPerCluster,
            this.dimensions,
            this.seed
        ); 
            
        gen.generate();

        DataPoint[] dataPoints = Arrays.stream(gen.getClusters()).flatMap(c -> Arrays.stream(c.getDataPoints())).toArray(DataPoint[]::new);
        
        KmeansClustering clustering = new KmeansClustering(
                dataPoints,
                this.dimensions, 
                this.clusterCountKmeans, 
                this.seed,
                this.initializationStrategy,
                this.algorithm          
        );
        clustering.run();

        plot2d(gen.getClusters(), true, "Generated");
        plot2d(gen.getClusters(), false, "Generated");


        plot2d(clustering.clusters, true, "Clustering");
        plot2d(clustering.clusters, false, "Clustering");
        
        if(this.outputFilename != null) {
            output(clustering.clusters);
        }
    }
    
    public void plot2d(ICluster[] clusters, boolean plotCenters, String title) throws KmeansException {
        // http://stackoverflow.com/questions/6594748/making-a-scatter-plot-using-2d-array-and-jfreechart
        // how to add jfreechart to netbeans: https://www.youtube.com/watch?v=aBONSQ44cnk
        
        XYSeriesCollection result = new XYSeriesCollection();
        
        for (ICluster cluster : clusters) {
            XYSeries series = new XYSeries("Cluster"+cluster.getClusterId());

            
            DataPoint[] points = 
                    plotCenters 
                      ? new DataPoint[] { cluster.getClusterCenter() } 
                      : cluster.getDataPoints();
            
            for (DataPoint point : points) {
                if(point.getDimensions() != 2)
                    throw new KmeansException("Only DataPoints with exactly two dimensions can be plotted!");

                series.add(point.get(0), point.get(1));
            }

            result.addSeries(series);
        }
        
        JFreeChart chart = ChartFactory.createScatterPlot(
            title,
            "X",
            "Y",
            result,
            PlotOrientation.VERTICAL,
            true, // include legend
            true, // tooltips
            false // urls
            );

        
        // http://stackoverflow.com/questions/7231824/setting-range-for-x-y-axis-jfreechart
        XYPlot xyPlot = (XYPlot) chart.getPlot();
        NumberAxis domain = (NumberAxis) xyPlot.getDomainAxis();
        domain.setRange(-5.00, 5.00);
        
        // http://www.jfree.org/phpBB2/viewtopic.php?f=3&t=12601
        xyPlot.getRenderer().setShape(new Rectangle(2,2));
        
        NumberAxis range = (NumberAxis) xyPlot.getRangeAxis();
        range.setRange(-5.0, +5.0);
        
        // create and display a frame...
        ChartFrame frame = new ChartFrame(title, chart);
        frame.pack();
        frame.setVisible(true);
    }
    
    public void output(KmeansCluster[] clusters) {
        try{
            PrintWriter writer = new PrintWriter(this.outputFilename, "UTF-8");
            writer.println("GenerationCluster;ClassificationCluster;Coordinates");
            
            for(KmeansCluster cluster: clusters) {
                for(DataPoint dp : cluster.getDataPoints()) {
                    writer.println(dp.getCluster().getClusterId() + ";" + cluster.getClusterId() + ";" + dp);
                }
            }
            
            writer.close();
        } catch (IOException e) {
            System.err.println("An error occured while writing to file: " + e.getMessage());
        }
    }
}
