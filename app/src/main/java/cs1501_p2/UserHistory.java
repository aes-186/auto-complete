/**
 * UserHistory class for CS1501 Project 2
 * @author	Anzu Sekikawa
 */

package cs1501_p2;

import java.util.*;

// must have same worst case asymptotic runtime as DLB methods

public class UserHistory implements Dict {

    // signal end of a valid key 
    public static final char END_CHAR= '^'; 

    // represent root node of UserHistory 
    private Node root; 

    private String currSearch; //current search 

    // map word (String key) to num times it has been 'added' to user history (int value)
    private HashMap<String, Integer> wordCounts;

    /**
     * Default constructor
     * sets root to null, currSearch to empty String, initializes HashMap (wordCounts)
     */
    public UserHistory(){
        root = null; 

        currSearch = "";

        wordCounts = new HashMap<String, Integer>();
    }

    /**
     * Is Empty method
     * 
     * @return true if UserHistory is empty, false otherwise
     */
    public boolean isEmpty(){
        return root==null; 
    }

    // ********* ADD ********* // 

    /**
	 * Add a new word to user history 
	 *
	 * @param key New word to be added to the user history
     * @throws IllegalArgumentException if {@code key} is {@code null}
	 */	
	public void add(String key){

        if (key==null){
            throw new IllegalArgumentException("calls add() with null key");
        }

        // adds END_CHAR onto key when adding to user history
        key += END_CHAR; 

        root = add(root, key, 0); 

    } // end add 

    // private recursive helper method for add 
    private Node add(Node x, String key, int d){
        char c = key.charAt(d); 

        if (x==null){
            x = new Node(c); 
        }
        
        if ( c != x.let ) {
            Node temp = add(x.getRight(), key, d);
            x.setRight(temp);

        } else if ( d < key.length()-1) {
            Node temp = add(x.down, key, d+1);
            x.setDown(temp);

        } else {
            // word already found in DLB
            // update selection count 

            assert x.getLet() == END_CHAR; 

            x.incrementCount(); //selectionCount+=1 

            // strip END_CHAR from key here before adding to wordCounts map 

            String word = key.substring(0, key.length()-1);

            wordCounts.put(word, x.getCount() ); //add key, selectionCount to map 

        } 

        return x; 

    } // end recursive helper add 


    // ********* CONTAINS ******* // 
    /**
	 * Check if user history contains a word
	 *
	 * @param	key	Word to search the user history for
	 *
	 * @return	true if key is in user history, false otherwise
     * @throws IllegalArgumentException if {@code key} is {@code null}
	 */
	public boolean contains(String key){
        if (key==null) throw new IllegalArgumentException("argument to contains() is null");

        // adds END_CHAR onto key when calling get 
        return get(key+END_CHAR) != null;         
    } // end contains

    /**
     * get method
     * 
     * @param key the key
     * @return node reference that stores last char of key, or null if key not found
     */
    public Node get(String key){
        if (key==null){
            throw new IllegalArgumentException("calls get() with null key");
        }

        // call recursive helper get
        Node x = get(root, key, 0);

        if (x==null) return null; 

        return x; 

    } // end get


    // private recursive helper method for get 
    private Node get(Node x, String key, int d){
        if (x==null) return null; 
        
        char c = key.charAt(d);

        if ( c != x.getLet() ) return get(x.getRight(), key, d);
        else if ( d < key.length()-1 ) return get(x.getDown(), key, d+1);
        else return x; 
    } // end recursive helper get

    // ********* CONTAINS PREFIX ********* // 

    /**
	 * Check if a String is a valid prefix to a word in this user history
	 *
	 * @param	pre	Prefix to search the dictionary for
	 *
	 * @return	true if prefix is valid, false otherwise
	 */
	public boolean containsPrefix(String pre){
        if (pre==null) throw new IllegalArgumentException("argument to containsPrefix() is null");

        // call recursive getPrefix method
        return getPrefix(pre);
    } // end containsPrefix

    // recursive getPrefix, helper for containsPrefix method
    private boolean getPrefix(String pre){

        if (pre==null){
            throw new IllegalArgumentException("calls get() with null argument");
        }

        Node curr = get(root, pre, 0); //not adding END_CHAR onto pre 

        if (curr==null) return false; 

        // if curr is referencing node containing last char of 'pre'
        // for contains prefix, must check .down node
        if (curr.getDown().getLet()==END_CHAR && curr.getDown().getRight()==null) {
            // valid word, not prefix 
            return false;
        } else {
            return true; 
        }

    } // end recursive helper getPrefix

    // ************ SEARCH BY CHAR ********** //

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

        currSearch += next; 

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

    // ************* RESET BY CHAR ********** //

	/**
	 * Reset the state of the current by-character search
	 */
	public void resetByChar(){

        currSearch = ""; 
    }

    // use in AutoCompleter
    public void updateCurrSearch(char next){
        currSearch += next; 
    }

    // user in AutoCompleter
    public String getCurrSearch(){
        return currSearch;
    }
    
    // *************** SUGGEST *********** // 

    /**
	 * Suggest up to 5 words from user history based on the current
	 * by-character search.
     * Produce up to 5 words that the user has most frequently selected 
     * based off of the given prefix (in order).
	 * 
	 * @return	ArrayList<String> List of up to 5 words that are prefixed by
	 *			the current by-character search
	 */
	public ArrayList<String> suggest(){

        // empty array list
        ArrayList<String> words = new ArrayList<>();

        // empty array list 
        ArrayList<String> max5words = new ArrayList<>();
        
        // call recursive helper method for suggest 
        words = suggestRec( currSearch ); 

        // sort suggestions based off of selection count
        collectionSort(words);

        // must cut off size to 5

        if ( words.size() > 5 ){
            int i=0;

            while (max5words.size() < 5 && i < words.size()){
                max5words.add(words.get(i));
                i++;
            }

            return max5words;
            
        } else {

            assert words.size()<=5; 

            return words;
        }
    }

    // private recursive helper for suggest
    private ArrayList<String> suggestRec(String pre){

        // emtpy array list 
        ArrayList<String> suggestions = new ArrayList<>(); 
        
        // call recursive get 
        // return node storing last char of pre 
        Node x = get(root, pre, 0);

        // if reach null node, return suggestions
        if (x==null) return suggestions; 

        // if valid word found, add the word to suggestions
        if (x.getLet()==END_CHAR) suggestions.add( pre ); 

        collect(x.getDown(), pre, suggestions);

        return suggestions; // size could be > 5 here! 

    }

    // class for sorting words in suggestions based on frequency
    class SortByCount implements Comparator<String>{
        // sort based on count in user history
        public int compare(String a, String b){
            return compareCount(a,b);
        }
    }

    // collection sort method for sorting strings based on frequency selected in user history
    private void collectionSort(ArrayList<String> words){
        Collections.sort(words, new SortByCount());
    }
    
    // GOAL - sort words in descending order based on frequency selected in user history
    private int compareCount(String a, String b){
        
        int countA = wordCounts.get(a);

        int countB = wordCounts.get(b);

        if (countA > countB ) return -1;
        else if (countA==countB) return 0; 
        else return 1; 

    }

    public int getSelectionCount(String key){
        if (key==null) throw new IllegalArgumentException("argument is null");

        return wordCounts.get(key);

    }
    
    // ********* TRAVERSE ********* //

	/**
	 * List all of the words currently stored in the dictionary
	 * @return	ArrayList<String> List of all valid words in the dictionary
	 */
	public ArrayList<String> traverse(){
        
        ArrayList<String> words = new ArrayList<>();
        
        collect(root, "", words);
        return words; 
    }

    // collect all keys in user history rooted at x with given prefix 
    private void collect(Node x, String prefix, ArrayList<String> words){

        if (x==null) return; 

        collect(x.getRight(), prefix, words); 

        if (x.getLet()==END_CHAR) words.add(prefix); 

        collect(x.getDown(), prefix+x.getLet(), words); 
    }

	/**
	 * Count the number of words in the dictionary
	 *
	 * @return	int, the number of (distinct) words in the dictionary
	 */
	public int count(){
        ArrayList<String> words = traverse(); 
        return words.size(); 
    }

    // ***************** NODE CLASS **************** // 

    private class Node {
	
        /**
         * Letter represented by this Node
         */
        private char let;
    
        /**
         * Lead to other alternatives for current letter in the path
         */
        private Node right;
    
        /**
         * Leads to keys with prefixed by the current path
         */	
        private Node down;

        private int selectionCount; 
    
        /**
         * Constructor that accepts the letter for the new node to represent
         */
        public Node(char let) {
            this.let = let;
            this.selectionCount = 0; 
            this.right = null;
            this.down = null;
        }
    
        /**
         * Getter for the letter this DLBNode represents
         *
         * @return	The letter
         */
        public char getLet() {
            return let;
        }
    
        /**
         * Getter for the next linked-list DLBNode
         *
         * @return	Reference to the right DLBNode
         */
        public Node getRight() {
            return right;
        }
    
        /**
         * Getter for the child DLBNode
         *
         * @return	Reference to the down DLBNode
         */
        public Node getDown() {
            return down;
        }
    
        /**
         * Setter for the next linked-list DLBNode
         *
         * @param	r DLBNode to set as the right reference
         */
        public void setRight(Node r) {
            right = r;
        }
    
        /**
         * Setter for the child DLBNode
         *
         * @param	d DLBNode to set as the down reference
         */
        public void setDown(Node d) {
            down = d;
        }

        public int getCount(){
            return selectionCount; 
        }

        public void incrementCount(){
            selectionCount += 1;
        }

    } // end Node class 
 
}

