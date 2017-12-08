import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

public class Parsing {
	
	public static void main(String[] args) throws IOException{
		BufferedReader br = null;
		FileReader fr = null;
		
		String newText = "";
		int count =0;
		try {

			//br = new BufferedReader(new FileReader(FILENAME));
			fr = new FileReader("complex_event_frequency_count_with_session_min_size_500.csv");
			br = new BufferedReader(fr);

			String sCurrentLine;
			br.readLine(); //skipping first line
			while ((sCurrentLine = br.readLine()) != null) {
				String[] entries = sCurrentLine.split(",");
				String newId = entries[0]+"_"+entries[1];
				String newLine = "";
				for(int i=2; i<entries.length; i++){
					newLine = newLine + entries[i]+",";
				}
				newLine+=newId;
				//do such an if, if u want to get programmers 7, 8, 9, 30, 0, and 15 only
				if(entries[0].equals("7")|| entries[0].equals("8")|| entries[0].equals("9")|| 
						entries[0].equals("30") || entries[0].equals("0") || entries[0].equals("15")){
					newText = newText + newLine + "\n";
				}
			//	System.out.println(newLine);
				count++;
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null){
					br.close();
				}

				if (fr != null){
					fr.close();
				}

			} catch (IOException ex) {
				ex.printStackTrace();
			}

		}
		
		System.out.println(newText);
		
		String dataOutputFilename = "data.csv";
		File f = new File(dataOutputFilename);
		if(f.exists()){
			f.delete();
		}
		f.createNewFile();
		
		try (PrintStream out = new PrintStream(new FileOutputStream(dataOutputFilename))) {
		    out.print(newText);
		}catch(Exception e){
			e.printStackTrace();
			
		}
		
		String[] lines = newText.split("\n");
		String entries[] = lines[0].split(",");
		//this is used in Kmean as argument as the index of the id
		System.out.println("session_id_index="+(entries.length-1));
		
	}

}
