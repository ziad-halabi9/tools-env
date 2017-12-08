import java.awt.Color;
import java.awt.Dimension;
import java.awt.RenderingHints;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.math.plot.Plot2DPanel;
import org.math.plot.Plot3DPanel;

import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.clustering.evaluation.ClusterEvaluation;
import net.sf.javaml.clustering.evaluation.SumOfSquaredErrors;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;

public class Kmean {

	private static Color[] colors = {Color.RED, Color.BLUE, Color.GREEN,  Color.GRAY, Color.CYAN, Color.MAGENTA, Color.PINK, Color.YELLOW, Color.DARK_GRAY, Color.ORANGE};

	public static void main(String[]args) throws IOException{

		Dataset data = FileHandler.loadDataset(new File("data.csv"), 48, ","); 


		long startTime = System.currentTimeMillis();
		System.out.println("Start Time="+startTime);

		Clusterer km = new KMeans(6);
		String results = "";
		Dataset[] clusters = km.cluster(data);
		System.out.println("Number of clusters="+clusters.length);
		
		ArrayList<Cluster> plottingClusters = new ArrayList<>();
		for(int j=0; j < clusters.length; j++){
			Dataset dataset = clusters[j];
			
			System.out.println("Cluster "+j+" size="+dataset.size());
			System.out.println("----------------------------------");
			results+="-----------------------------\n";
			
			double[] x = new double[dataset.size()];
			double[] y = new double[dataset.size()];
			double[] z = new double[dataset.size()];
			
			for(int i=0; i<dataset.size(); i++){
				//System.out.println(dataset.get(i));
				String s= dataset.get(i).toString();
				
				
				x[i] = +dataset.get(i).get(0);
				y[i] = +dataset.get(i).get(1);
				z[i] = +dataset.get(i).get(2);
				
				System.out.println(s.split(";")[1]);
				//printing to the file just the ID, makes it easier to see what session is in each cluster.
				//ID of the session is programmerID_sessionID
				results += s.split(";")[1] + "\n";

			}
			
			Color color = new Color((int)(255*Math.random()),(int) (255*Math.random()),(int) (255*Math.random()));
			Cluster plottingCluster = new Cluster(x, y, z, color);
			plottingClusters.add(plottingCluster);
		}


		//ClusterEvaluation sse= new SumOfSquaredErrors();
		//double score=sse.score(clusters);
		//System.out.println("score="+score);

		long timeTaken = System.currentTimeMillis() - startTime;
		System.out.println("Time taken="+timeTaken +" ms");
		
		saveToFile(results);
		
		plot(plottingClusters);

	}


	private static void plot(ArrayList<Cluster> clusters){


		// create your PlotPanel (you can use it as a JPanel)
		Plot3DPanel plot = new Plot3DPanel();

		for(Cluster cluster: clusters){
			plot.addScatterPlot("my plot", cluster.color, cluster.x, cluster.y, cluster.z);
		}

		// put the PlotPanel in a JFrame, as a JPanel
		JFrame frame = new JFrame("a plot panel");
		frame.setMinimumSize(new Dimension(600, 600));
		frame.setContentPane(plot);

		frame.setVisible(true);
	}
	
	private static void saveToFile(String text) throws IOException{

		String resultsFilename = "results.txt";
		File f = new File(resultsFilename);
		if(f.exists()){
			f.delete();
		}
		f.createNewFile();

		try (PrintStream out = new PrintStream(new FileOutputStream(resultsFilename))) {
			out.print(text);
		}catch(Exception e){
			e.printStackTrace();

		}
	}

}
