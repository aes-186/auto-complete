/**
 * DLB class for CS1501 Project 2
 * @author	Anzu Sekikawa
 */

package cs1501_p2;

import java.util.ArrayList; // import the ArrayList class
 
/**
 * De La Briandias Trie implemented with terminator char
 */
public class DLB implements Dict {

    // signals the end of a valid key in the DLB
    public static final char END_CHAR= '^'; 

    // represents the root node of the DLB
    private DLBNode root; 

    // represents the current search string
    private String currSearch;

    /**
     * Default constructor, sets root to null
     */
    public DLB(){
        root = null; 
        currSearch = "";
    }

    /**
     * Is Empty method
     * 
     * @return true if DLB Trie is empty, false otherwise
     */
    public boolean isEmpty(){
        return root==null; 
    }

    // ********* ADD ********* // 

    /**
	 * Add a new word to the dictionary
	 *
	 * @param 	key New word to be added to the dictionary
     * @throws IllegalArgumentException if {@code key} is {@code null}
	 */	
	public void add(String key){

        if (key==null){
            throw new IllegalArgumentException("calls add() with null key");
        }

        // append END_CHAR to the key 
        key += END_CHAR; 

        // call recursive helper method here 
        root = add(root, key, 0); 
    }


    /**
     * private recursive helper method for add 
     * 
     * @param x the DLBNode
     * @param key the String key (with END_CHAR)
     * @param d current index of key
     * @return DLBNode 
     */
    private DLBNode add(DLBNode x, String key, int d){

        // curr char
        char c = key.charAt(d); 

        // if null ref reached, create new node storing curr char
        if (x==null){
            x = new DLBNode(c); 
        }
        
        if ( c != x.getLet() ) {
            // if curr node does not store curr char
            // search to the right

            DLBNode temp = add(x.getRight(), key, d); 
            x.setRight(temp); 

        } else if ( d < key.length()-1) {
            // if we've found the node storing curr char
            // move onto the next char in the key
            // add as a new down node to curr node 

            DLBNode temp = add(x.getDown(), key, d+1);
            x.setDown( temp ); 

        } // else word already found in DLB, do nothing 

        return x; 
    }

    // ********* CONTAINS ******* // 
    /**
	 * Check if the dictionary contains a word
	 *
	 * @param	key	Word to search the dictionary for
	 *
	 * @return	true if key is in the dictionary, false otherwise
     * @throws IllegalArgumentException if {@code key} is {@code null}
	 */
	public boolean contains(String key){
        if (key==null) throw new IllegalArgumentException("argument to contains() is null");

        // call get method 
        // get method, returns ref to node that stores last char of key
        // if null, key not found, if !=null, key found
        return get(key+END_CHAR) != null;         
    }

    /**
     * get method, returns ref to node storing last char of key
     * 
     * @param key the String key 
     * @return DLBNode ref, storing last char of key, or null if not found
     */
    public DLBNode get(String key){

        if (key==null){
            throw new IllegalArgumentException("calls get() with null key");
        }

        // call recursive get method 
        DLBNode x = get(root, key, 0);

        if (x==null) return null; 

        return x; 
    }


    // private recursive helper method for get 
    private DLBNode get(DLBNode x, String key, int d){
        if (x==null) return null; 
        
        // curr char
        char c = key.charAt(d);

        // if curr char not stored in curr node, search right
        if ( c != x.getLet() ) return get(x.getRight(), key, d);
        // if curr char stored in curr node, search for next char
        else if ( d < key.length()-1 ) return get(x.getDown(), key, d+1);
        else return x; 

    }

    // ********** CONTAINS PREFIX ********* // 

    /**
	 * Check if a String is a valid prefix to a word in the dictionary
	 *
	 * @param	pre	Prefix to search the dictionary for
	 *
	 * @return	true if prefix is valid, false otherwise
	 */
	public boolean containsPrefix(String pre){
        if (pre==null) throw new IllegalArgumentException("argument to containsPrefix() is null");

        // call recursive helper, getPrefix method
        return getPrefix(pre);
    }

    /**
     * Recursive helper for containsPrefix
     * 
     * @param pre the prefix we are searching for 
     * @return true if prefix found, false otherwise 
     */
    private boolean getPrefix(String pre){
        if (pre==null){
            throw new IllegalArgumentException("calls get() with null argument");
        }

        DLBNode curr = get(root, pre, 0); //not adding END_CHAR onto pre 

        if (curr==null) return false; 

        if (curr.getDown().getLet()==END_CHAR && curr.getDown().getRight()==null) {
            // valid word, but not a prefix 
            return false;
        } else {
            return true; 
        }
    }

    // ************** SEARCH BY CHAR *********** // 

	/**
	 * Search for a word one character at a time
	 *
	 * @param	next Next character to search for
	 *
	 * @return	int value indicating result for current by-character search:
	 *				-1: not a valid word or prefix
	 *				 0: valid prefix, but not a valid word
	 *				 1: valid word, but not a valid prefix to any other words
	 *				 2: both valid word and a valid prefix to other words
	 */
	public int searchByChar(char next){

        currSearch += next; // updates currSearch 

        if ( contains(currSearch) && containsPrefix(currSearch)){
			return 2;
		} else if ( contains(currSearch) && !containsPrefix(currSearch)){
			return 1;
		} else if ( !contains(currSearch) && containsPrefix(currSearch)){
			return 0;
		} else { //( !contains(currSearch) && !containsPrefix(currSearch)){
			assert (!contains(currSearch) && !containsPrefix(currSearch));

			return -1;
		}
    }

	/**
	 * Reset the state of the current by-character search
	 */
	public void resetByChar(){

        currSearch = ""; 
    }

    // used in AutoComplete
    public void updateCurrSearch(char next){
        currSearch += next;
    }

    // used in AutoComplete 
    public String getCurrSearch(){
        return currSearch; 
    }

	/**
     * Recursive helper for suggest,
     * Returns ArrayList<String> of up to 5 words from the dictionary with given prefix 
     * 
     * @param pre the prefix
     * @return ArrayList<String> of up to 5 String with given prefix pre
     */
	private ArrayList<String> suggestRec( String pre ){
        
        ArrayList<String> suggestions = new ArrayList<>(); 
        
        // call recursive get method 
        DLBNode x = get(root, pre, 0);

        // if null node reached, return the ArrayList 
        if (x==null) return suggestions; 

        // if valid word found and suggestions is still <5, add the word to suggestions
        if (x.getLet()==END_CHAR && suggestions.size()<5 ) suggestions.add(pre); 

        // collects all words in DLB with prefix 'pre' 
        collect(x.getDown(), pre, suggestions);

        return suggestions; 

    }

    /**
	 * Suggest up to 5 words from the dictionary based on the current
	 * by-character search
	 * 
	 * @return	ArrayList<String> List of up to 5 words that are prefixed by
	 *			the current by-character search
	 */
	public ArrayList<String> suggest(){

        return suggestRec( currSearch );

    }

    // ********** TRAVERSE ********** //

	/**
	 * List all of the words currently stored in the dictionary
     * (lists words starting from the last word added to the dictionary)
     * 
	 * @return	ArrayList<String> List of all valid words in the dictionary
	 */
	public ArrayList<String> traverse(){
        ArrayList<String> words = new ArrayList<>();
        
        // collects all strings with prefix "", and stores it in the array list
        collect(root, "", words);

        return words; 
    }

    // ********* COLLECT ******** //

    // collect all keys in DLB rooted at x with given prefix 
    private void collect(DLBNode x, String prefix, ArrayList<String> words){

        if (x==null) return; 
        collect(x.getRight(),prefix, words); 
        if (x.getLet()=='^') words.add(prefix); 
        collect(x.getDown(), prefix+x.getLet(), words);
    }

    // ************* COUNT *********** // 

	/**
	 * Count the number of words in the dictionary
	 *
	 * @return	int, the number of (distinct) words in the dictionary
	 */
	public int count(){
        ArrayList<String> words = traverse(); 
        return words.size(); 
    }

    
} //end DLB
