/**
 * AutoCompleter class for CS1501 Project 2
 * @author	Anzu Sekikawa
 */

package cs1501_p2;

import java.io.*;
import java.util.*;

public class AutoCompleter implements AutoComplete_Inter {

	private DLB dictionary; 
	private UserHistory history; 

	// constructor
	// accept two String file names 
	public AutoCompleter(String dict_fname, String userHistoryFile ){
		
		dictionary = new DLB(); 

		// adds words in the dictionary file to the dictionary (DLB)
		try (Scanner s = new Scanner(new File(dict_fname))) {
			while (s.hasNext()) {
				dictionary.add(s.nextLine());
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		history = new UserHistory(); 

		// adds words in user history file to the user history object
		try (Scanner s = new Scanner(new File(userHistoryFile))) {
			while (s.hasNext()) {
				history.add(s.nextLine());
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	// constructor, only accepts dictionary file
	// initialize user history as empty
	public AutoCompleter(String dict_fname){
		dictionary = new DLB(); 

		try (Scanner s = new Scanner(new File(dict_fname))) {
			while (s.hasNext()) {
				dictionary.add(s.nextLine());
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		history = new UserHistory(); //initially empty
	}


    /**
	 * Produce up to 5 suggestions based on the current word the user has
	 * entered 
	 * These suggestions should be pulled first from the user history
	 * dictionary then from the initial dictionary
	 *
	 * @param 	next char the user just entered
	 *
	 * @return	ArrayList<String> List of up to 5 words prefixed by cur
	 */	
	public ArrayList<String> nextChar(char next){

		history.updateCurrSearch(next);
		dictionary.updateCurrSearch(next);

		// user history suggestions
		ArrayList<String> uh_sugs = history.suggest();

		// if user history suggestions is less than 5 words
		if ( uh_sugs.size() < 5 ){

			ArrayList<String> suggestions = new ArrayList<>();

			// must use sugs from dictionary

			for (String word : uh_sugs ){
				// copy words in uh_sugs into suggestions
				suggestions.add( word );
			}

			// dictionary suggestions
			ArrayList<String> dict_sugs = dictionary.suggest();

			int i=0;

			// while we have less than 5 words 
			// and while we still have words in dictionary suggestions
			while (suggestions.size() < 5 && i < dict_sugs.size()){

				String dictWord = dict_sugs.get(i);

				if ( ! suggestions.contains( dictWord )){
					// must make sure no duplicates are suggested 

					suggestions.add(dictWord);
				}
				// if words already found in user history suggestions, skip it  
				
				i++;
			}

			return suggestions;

		} else {
			assert uh_sugs.size()==5;

			return uh_sugs;
		}

	} //end nextChar

	// ********** FINISH WORD *********** //

	/**
	 * Process the user having selected the current word
	 * Resets state of any searches and 
	 * updates user history attribute to reflect the word selected
	 * 
	 * @param 	cur String representing the text the user has entered so far
	 */
	public void finishWord(String cur){

		history.add(cur); //add cur to user history object

		history.resetByChar(); 
		dictionary.resetByChar(); 
	}

	/**
	 * Save the state of the user history to a file
	 * Saving and loading the user history - O(n) runtime 
	 * n - number of distinct words in the UserHistory object
	 *
	 * @param	fname String filename to write history state to
	 */
	public void saveUserHistory(String fname) {

		File uhFile = new File(fname);

		// create the user history file
		try {
			if (uhFile.createNewFile()){
				System.out.println("File created: " + uhFile.getName());
			} else {
				System.out.println("File already exists");
			}
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace(); 
		}

		// writing to file 
		try {
			FileWriter writer = new FileWriter(fname);

			// only stores distinct words here
			ArrayList<String> userHistoryWords = history.traverse(); 

			// initially empty 
			ArrayList<String> uh_repeats = new ArrayList<>(); 

			
			for (int i=0; i < userHistoryWords.size(); i++){
				// traverse userHistoryWords

				String curr = userHistoryWords.get(i);
				//current word in user history words

				// num times user has selected curr word
				int count = history.getSelectionCount(curr);

				while ( count > 0 ){
					uh_repeats.add(curr);
					count -= 1; 
				}
				
			}

			// write every word (with repeats) to the file
			for (String word : uh_repeats){
				writer.write(word + "\n");
			}

			writer.close(); 

		} catch (IOException e) {
			e.printStackTrace(); 
		}
	}
}
