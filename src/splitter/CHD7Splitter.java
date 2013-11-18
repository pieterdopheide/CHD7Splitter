package splitter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CHD7Splitter {

	public static int mutationCount = 0;
	
	public static void main(String[] args) {
		String chd7File = "resources/CHD7 mutaties 7-3-2012.txt";
		String patientsFile = "resources/PatientsTest.txt";
		String mutationsFile = "resources/MutationsTest.txt";
		
		try {
			splitFile(chd7File, patientsFile, mutationsFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	private static void splitFile(String chd7File, String patientsFile, String mutationsFile) throws IOException{
		System.out.println("Reading data...");
		BufferedReader br = new BufferedReader(new FileReader(chd7File));
		String row;
		System.out.println("Done");
		
		BufferedWriter bwPatients = new BufferedWriter(new FileWriter(patientsFile));
		BufferedWriter bwMutations = new BufferedWriter(new FileWriter(mutationsFile));
		
		System.out.println("Splitting and writing data...");
		
		int x = 0;
		
		while((row = br.readLine()) != null){
			String mutationID = null;
			String rowElements[] = row.split("\t");
			ArrayList<String> patient = new ArrayList<String>();
			ArrayList<String> mutation = new ArrayList<String>();
			
			for(int i = 0; i < rowElements.length; i++){
				if(i < 7 || i > 14 && i != 15){
					patient.add(rowElements[i]);
				}else if(i > 6 && i < 14){
					mutation.add(rowElements[i]);
				}else if(i == 15){
					//System.out.println(i);
					patient.add(rowElements[i]);
					mutation.add(rowElements[i]);
				}
			}
			
			mutationID = splitMutation(mutation, bwMutations);
			
			if(x == 0){ // Mutation ID column header to the patient data
				//patient.add("Mutation ID");
				patient.add(0, "Mutation ID");
			}else{
				//patient.add(mutationID);
				patient.add(0, mutationID);
			}
			
			//System.out.println("patient: " + patient);
			
			StringBuffer sb = new StringBuffer();
			for(String p : patient){
				if(sb.length() != 0){
					//sb.append(", ");
					sb.append("\t");
				}
				sb.append(p);
			}
			
			bwPatients.write(sb.toString() + "\r");
			x++;
		}
		
		System.out.println("Done");
		
		bwPatients.close();
		bwMutations.close();
		br.close();
	}
	
	private static String splitMutation(ArrayList<String> mutation, BufferedWriter bwMutations) throws IOException{
		ArrayList<String[]> splitMutations = new ArrayList<String[]>();
		int i = 0;
		int x = 0;
		String[] mut = null;
		
		for(String m : mutation){
			if(x != 7 && x != 4 && x != 6){
				mut = m.split("[,/]|and"); // split on comma and slash
			}else if(x == 4){
				mut = m.split("[,/+]");
			}else if(x == 7){
				// 'Other information mutation' does not have to be split
				mut = new String[] {m}; // cast string to array
			}else if(x == 6){
				mut = m.split("[,+]");
			}
			
			if(mut.length > i){
				i = mut.length;
			}
			
			splitMutations.add(mut);
			x++;
		}
		
		StringBuffer sb2 = new StringBuffer(); // to make a list of mutation ID's for the patient data
		for(int j = 0; j < i; j++){
			StringBuffer sb = new StringBuffer();
			for(String[] m2 : splitMutations){
				// ugly way to add separator EXCEPT after last element
				if(sb.length() != 0){
					//sb.append(", ");
					sb.append("\t");
				}else{ // add mutation id to mutation
					if(mutationCount !=0){
						sb.append("M" + mutationCount + "\t");
						sb2.append("M" + mutationCount + ",");
					}else{
						sb.append("Mutation ID\t");
					}
					mutationCount++;
				}
				
				if(m2.length > j){
					sb.append(m2[j].trim().replace("\"", ""));// + ", ");
				}else{
					sb.append(m2[0].trim().replace("\"", ""));// + ", "); //(or the last element in the array?)
				}
			}
			
			bwMutations.write(sb.toString() + "\r");
		}
		
		if(sb2.length() > 0){
			return sb2.deleteCharAt(sb2.lastIndexOf(",")).toString(); // remove last comma and convert to string to return
		}else{
			return sb2.toString();
		}
	}

}
