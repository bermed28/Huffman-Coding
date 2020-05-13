package P3.Main;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

import P3.DataStructures.List.*;
import P3.DataStructures.Map.*;
import P3.DataStructures.Map.HashTable.*;
import P3.DataStructures.SortedList.*;
import P3.DataStructures.Tree.*;
import P3.DataStructures.Tree.utils.*;

/**
 * The Huffman Encoding Algorithm
 * 
 * This is a data compression algorithm designed by David A. Huffman and published in 1952
 * 
 * What it does is it takes a string and by constructing a special binary tree with the frequencies of each character.
 * This tree generates special prefix codes that make the size of each string encoded a lot smaller, thus saving space.
 * 
 * @author bermed28
 * @version 2.0
 * @since 05/06/2020
 */
public class HuffmanCoding {

	public static MergeSort<Integer> ms = new MergeSort<Integer>(new IntegerComparator());

	public static void main(String[] args) {
		HuffmanEncodedResult();
	}

	/* This method just runs all the main methods developed or the algorithm */
	private static void HuffmanEncodedResult() {
		String data = load_data("stringData2.txt");
		Map<String, Integer> fD = compute_fd(data);
		BTNode<String, Integer> huffmanRoot = huffman_tree(fD);
		Map<String,String> encodedHuffman = huffman_code(huffmanRoot);
		String output = encode(encodedHuffman, data);
		process_results(fD, encodedHuffman,data,output);
	}
	
	/**
	 * Receives a file named in parameter inputFile (including its path),
	 * and returns a single string with the contents.
	 * 
	 * @param inputFile - name of the file to be processed in the path inputData/
	 * @return String with the information to be processed
	 */
	public static String load_data(String inputFile) {
		BufferedReader in = null;
		String line = "";

		try {
			/*We create a new reader that accepts UTF-8 encoding and extract the input string from the file, and we return it*/
			in = new BufferedReader(new InputStreamReader(new FileInputStream("inputData/" + inputFile), "UTF-8"));
			line = in.readLine();

		} catch (FileNotFoundException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		} finally {
			if (in != null) 
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

		}
		return line;
	}

	/**
	 * Receives a string and returns a Map with the symbol frequency distribution.
	 * 
	 * The frequency distribution consists of mapping each character to how many times that character appears in the string provided.
	 * For this we use a hashtable (Separate Chaining).
	 * We do this so later on we can construct out huffman tree using each K,V pair of frequencies
	 * 
	 * @param inputString - string that was processed in load_data
	 * @return HashTable with frequency distribution of characters inside input string
	 */
	public static Map<String, Integer> compute_fd(String inputString) {
		
		/* Much like other previous implementations made before of frequency distribution, this one is pretty straightforward
		 * We use a hashtable, given by previous experimentations, it's the most efficient way to count frequencies */
		Map<String, Integer> ht = new HashTableSC<String, Integer>(new SimpleHashFunction<String>());
		
		/*Like before, we iterate through every character inside out string and count how many times it appears*/
		for (Character character : inputString.toCharArray()) {
			
			/* If it's in the map, add one to the frequency, 
			 * if not just add it with a frequency of 1 because it's the first time we see it*/
			String letter =String.valueOf(character);
			if (!ht.containsKey(letter)) {
				ht.put(letter, 1);
			} else {
				ht.put(letter, ht.get(letter) + 1);
			}
		}

		return ht;
	}

	/**
	 * Receives a Map with the frequency distribution and returns the root node of the corresponding Huffman tree.
	 * 
	 * The building consists of merging the two lowest frequencies into one node until only one node is left, the root of the tree.
	 * We build the tree from the ground up (from leaves to the root).
	 * 
	 * @param fD - hashtable with our frequencies
	 * @return Root Node that builds our huffman tree
	 */
	public static BTNode<String, Integer> huffman_tree(Map<String, Integer> fD) {
		
		/* First we create a sorted list to put the lowest frequencies at the beginning of the list, so it's easier to extract
		 * P.S. Using a SortedList is a good substitute for the Priority Queue that is mostly used in this algorithm */
		SortedList<BTNode<String,Integer>> list = new SortedArrayList<>(fD.size());
		List<String> letters = fD.getKeys();
		List<Integer> freq = fD.getValues();

		/*Now we go through each K,V pair and add them to an instance of a node and ad them in increasing order inside out SortedList*/
		for (int i = 0; i < fD.size(); i++) list.add(new BTNode<String, Integer>(letters.get(i), freq.get(i)));

		/*If the string has only one character then just create and add that one node to the list*/
		if(list.size() == 1) {
			BTNode<String, Integer> parent = new BTNode<String, Integer>();
			parent.setKey(list.get(0).getKey());
			parent.setValue(list.get(0).getValue());
			list.add(parent);
		}
		/*If the string has more than one node we do the following*/
		while (list.size() > 1) {
			/*We take the first two lowest frequencies of the SortedList and make them into separate nodes
			 * 
			 * P.S. If the case were to appear that two nodes have the same frequency, 
			 * we take care of that problem inside the compareTo() method of BTNode<K,V>.
			 * 
			 * This is possible because in order to add our nodes we had to make BTNode<K,V> comparable.
			 * 
			 * So we can just delegate that task to the compareTo() method of BTNode<K,V> (see line 87 of BTNode<K,V> inside the Tree package),
			 * and it will do the same as adding an if in this method to compare the keys and values of those two nodes*/
			BTNode<String, Integer> leftChild = list.removeIndex(0);
			BTNode<String, Integer> rightChild = list.removeIndex(0);
			
			/* We make the node we're going to use to "merge" the two lowest frequencies into one, 
			 * thus making this node the "parent" of the left and right nodes extracted from the SortedList */
			BTNode<String, Integer> parent = new BTNode<String, Integer>();

			/* Now all we do is we set the parent's left and right children be the extracted nodes from the SortedList,
			 * We also merge the frequencies and the keys */
			parent.setLeftChild(leftChild);
			parent.setRightChild(rightChild);

			parent.setKey(leftChild.getKey() + rightChild.getKey());
			parent.setValue(leftChild.getValue() + rightChild.getValue());

			/* Next we add this parent node back to the SortedList and we rinse and repeat 
			 * until the only merged node left on the list is the root node of the tree,
			 * which should have the same frequency as the number of characters*/
			list.add(parent);

		}
		
		/*Finally we just extract the root node from the list and return it*/
		BTNode<String, Integer> rootNode = list.removeIndex(0);
		
		/**
		BinaryTreePrinter.print(rootNode); //Uncomment to see full Huffman Tree built with the generated root node 
		*/
		
		return rootNode;
	}
	
	/**
	 * Receives the root of a Huffman tree and returns a mapping of every symbol to its corresponding Huffman code.
	 * 
	 * In this method we construct a "lookup table" with the prefix code of every character, 
	 * we do this by traversing the tree from the root to the leaf of that character
	 * 
	 * The left vertexes are marked with a 0 and the right vertexes are marked with a 1.
	 * 
	 * This makes the code for each character equal to the path from the root to the leaf.
	 * 
	 * This also allows us to recognize that the characters with the HIGHEST frequency will have the smallest prefix code, 
	 * thus making the byte size of the encoding a LOT smaller
	 * 
	 * @param huffmanRoot - Root Node of huffman tree so we can traverse and construct the prefix codes
	 * @return HashTable with our "lookup table" that has all of our prefix codes mapped to each character
	 */
	public static Map<String, String> huffman_code(BTNode<String, Integer> huffmanRoot) {
		/* This is basically the method that makes and organizes all of our things, the real work is in the recursive helper method.
		 * Look up buidPrefixCode in line 275 to see what the recursive method does */
		Map<String,String> ht = new HashTableSC<String, String>(new SimpleHashFunction<String>());
		buildPrefixCode(ht,huffmanRoot,"");
		return ht;	
	}

	/**
	 * Receives the Huffman code map and the input string, and returns the encoded string.
	 * 
	 * All we do is we traverse the input string and concatenate the mapped prefix codes to an output string,
	 * thus generating the fully encoded huffman text.
	 * 
	 * @param encodingMap - "Lookup Table" with all the mapped prefix codes 
	 * @param inputString - Original string 
	 * @return The fully encoded string using huffman prefix codes
	 */
	public static String encode(Map<String, String> encodingMap, String inputString) {
		String encodedMessage = "";

		for (Character character : inputString.toCharArray()) {
			String letter = String.valueOf(character);
			encodedMessage += encodingMap.get(letter);
		}

		return encodedMessage;
	}

	/**
	 * Receives the frequency distribution map, the Huffman Prefix Code HashTable, 
	 * the input string, 
	 * and the output string, and prints the results to the screen (per specifications).
	 * 
	 * Includes: symbol, frequency and code. 
	 * Also includes how many bits has the original and encoded string, plus how much space was saved using this encoding algorithm
	 * 
	 * @param fD - Frequency Distribution of all the characters in input string
	 * @param encodedHuffman - Prefix Code Map
	 * @param inputData - text string from the input file
	 * @param output - processed encoded string
	 */
	public static void process_results(Map<String, Integer> fD, Map<String, String> encodedHuffman, String inputData, String output) {
		/*Here, we just get the bytes of the original string with string.getBytes().length*/
		int inputBytes = inputData.getBytes().length;
		
		/*For the bytes of the encoded one, it's not so easy.
		 * 
		 * Here we have to get the bytes the same way we got the bytes for the original one but we divide it by 8,
		 * because 1 byte = 8 bits. 
		 * 
		 * This is because we want to calculate how many bytes we saved by counting how many bits we generated with the encoding */
		int outputBytes = Math.round((float) output.getBytes().length / 8);
		DecimalFormat d = new DecimalFormat("##.##");
		
		/* To calculate how much space we saved we just take the percentage.
		 * the number of encoded bytes divided by the number of original bytes will give us how much space we "chopped off"
		 * 
		 * So we have to subtract that "chopped off" percentage to the total (which is 100%) 
		 * and that's the difference in space required*/
		String savings =  d.format(100 - (((float) ((float)outputBytes / (float)inputBytes)) * 100));


		/* Finally we just output our results to the console 
		 * with a more visual pleasing version of both our hashtables in decreasing order by frequency.
		 * 
		 * Notice that when the output is shown, the characters with the highest frequency have the lowest amount of bits.
		 * 
		 * This means the encoding worked and we saved space! 
		 * 
		 * Hooray! :D */
		System.out.println("Symbol\t" + "Frequency   " + "Code");
		System.out.println("------\t" + "---------   " + "----");

		List<String> letters = fD.getKeys();
		List<Integer> freq = fD.getValues();
		List<String> orderedLetters = new LinkedList<String>(); //We use this to make the output in decreasing order by frequency
		ms.sortList(freq);

		orderLetters(letters, orderedLetters,freq, fD);

		for (int j = fD.size() -1 ; j >= 0; j--) {
			System.out.println(orderedLetters.get(j) + "\t" + freq.get(j) + "\t    " +encodedHuffman.get(orderedLetters.get(j)));
		}

		System.out.println("\nOriginal String: \n" + inputData);
		System.out.println("Encoded String: \n" + output + "\n");
		System.out.println("The original string requires " + inputBytes + " bytes.");
		System.out.println("The encoded string requires " + outputBytes + " bytes.");
		System.out.println("Difference in space requiered is " + savings + "%.");
	}

	

	////PRIVATE METHODS USED INSIDE MAIN METHODS//////////////////////////////////////////////////////////////////
	
	/**
	 * Recursive helper method for the huffman_code() method above in line 193
	 * 
	 * @param ht - map that will store our generated codes mapped to the character they belong to
	 * @param node - node we're currently traversing. At the beginning it's the root of the tree.
	 * @param prefixCode - variable we use to store the codes generated for each character
	 */
	private static void buildPrefixCode(Map<String, String> ht, BTNode<String, Integer> node, String prefixCode) {
		
		/* In here we do a similar thing we do with traversals of a BST, but the base case in theory, doesn't exists
		 * Our version of the base case is if the node is a leaf or not*/
		
		if(!isLeaf(node)) {
			
			/* If the node is not a leaf, it means we haven't finished building the code for our character,
			 * So we need to keep traversing the tree through it's left and right children of the tree, 
			 * concatenating a 0 or a 1 depending on the vertex we're traversing at the moment.
			 * 
			 * If we're traversing a left vertex, we concatenate a 0, if it's a right vertex, we concatenate a 1*/
			
			buildPrefixCode(ht, node.getLeftChild(), prefixCode + "0");
			buildPrefixCode(ht, node.getRightChild(), prefixCode + "1");
			
		} else {
			
			/* If the node is a leaf, it means we finished building the prefix code, 
			 * so we can just add the code to the hashtable as a value mapped to the character 
			 * that it's supposed to be assigned to, according to the tree. 
			 * 
			 * The way we map the code to the character is by the key of the leaf, 
			 * given that the leaf represents the character that we're constructing the prefix code for */
			
			ht.put(node.getKey(), prefixCode);
		}
	}

	/**
	 * Method that return true or false, depending if the given node is a leaf or not
	 * @param node
	 * @return true if given node is a leaf, false otherwise
	 */
	private static boolean isLeaf(BTNode<String, Integer> node) {
		return node.getLeftChild() == null && node.getRightChild() == null;
	}
	
	/**
	 * Method that organizes the letters according to their frequencies in decreasing order 
	 * @param letters - list of characters
	 * @param orderedLetters - list with our ordered characters according to their frequency
	 * @param freq - sorted frequencies
	 * @param fD - frequency distribution map
	 */
	private static void orderLetters(List<String> letters, List<String> orderedLetters, List<Integer> freq, Map<String, Integer> fD) {
		
		int i = 0; /*index to traverse unordered letters*/ 
		for (int j = 0; j < freq.size(); j++) {
			/*While we haven't finished traversing all the frequencies*/
			while(i < fD.size()) {
				/*We organize all our letters in order according to their frequency*/
				if (fD.get(letters.get(i)) == freq.get(j)) {
					/*If we ordered a letter, 
					 * we add it to our ordered list and remove it from the original 
					 * and re-start the index so we can start looking from the start for other frequencies.
					 * 
					 * We remove the letter from the unordered list to avoid 
					 * repetitions in case there are letters with the same frequency*/
					orderedLetters.add(letters.get(i));
					letters.remove(i);
					i = 0;
					break;
				}
				/*If we didn't order a letter, we increase the index to keep looking*/
				i++;
			}
		} 
	}

}
