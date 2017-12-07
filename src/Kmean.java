import java.awt.RenderingHints;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.clustering.evaluation.ClusterEvaluation;
import net.sf.javaml.clustering.evaluation.SumOfSquaredErrors;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;

public class Kmean {
	
	
	public static void main(String[]args) throws IOException{
		
		Dataset data = FileHandler.loadDataset(new File("data.csv"), 48, ","); //15 or 48
		
		
		long startTime = System.currentTimeMillis();
		System.out.println("Start Time="+startTime);
		
		Clusterer km = new KMeans(4); //
		String results = "";
		Dataset[] clusters = km.cluster(data);
		System.out.println("Number of clusters="+clusters.length);
		for(int j=0; j < clusters.length; j++){
			Dataset dataset = clusters[j];
			System.out.println("Cluster "+j+" size="+dataset.size());
			System.out.println("----------------------------------");
			results+="-----------------------------\n";
			for(int i=0; i<dataset.size(); i++){
				System.out.println(dataset.get(i));
				String s= dataset.get(i).toString();
				//System.out.println(s.split(";")[1]);
				//printing to the file just the ID, makes it easier to see what session is in each cluster.
				//ID of the session is programmerID_sessionID
				results += s.split(";")[1] + "\n";
				
			}
				
		}
		
		
		//ClusterEvaluation sse= new SumOfSquaredErrors();
		//double score=sse.score(clusters);
		//System.out.println("score="+score);

		long timeTaken = System.currentTimeMillis() - startTime;
		System.out.println("Time taken="+timeTaken +" ms");
		
		String resultsFilename = "results.txt";
		File f = new File(resultsFilename);
		if(f.exists()){
			f.delete();
		}
		f.createNewFile();
		
		try (PrintStream out = new PrintStream(new FileOutputStream(resultsFilename))) {
		    out.print(results);
		}catch(Exception e){
			e.printStackTrace();
			
		}

	}

}
