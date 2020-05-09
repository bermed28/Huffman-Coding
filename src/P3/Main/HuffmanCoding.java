package P3.Main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import P3.DataStructures.List.*;
import P3.DataStructures.Map.*;
import P3.DataStructures.Map.HashTable.*;
import P3.DataStructures.SortedList.*;
import P3.DataStructures.Stack.*;
import P3.DataStructures.Tree.*;
import P3.DataStructures.Tree.utils.*;


public class HuffmanCoding {
	public static void main(String[] args) {
		run();
	}

	private static void run() {
		String data = loadData("huffman.txt");
		Map<String, Integer> fD = computeFD(data);
		BTNode<String, Integer> huffmanRoot = huffmanTree(fD);
		Map<String,String> encodedHuffman = huffmanCode(huffmanRoot);
		String output = encode(encodedHuffman, data);
		processResults(fD,data,output);

	}



	public static String loadData(String inputFile) {
		BufferedReader br = null;
		String line = "";

		try {

			br = new BufferedReader(new FileReader("inputData/" + inputFile));
			line = br.readLine();

		} catch (FileNotFoundException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		} finally {
			if (br != null) 
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

		}
		return line;
	}

	public static Map<String, Integer> computeFD(String data) {
		Map<String,Integer> ht = new HashTableSC<String,Integer>(data.length(), new SimpleHashFunction<String>());
		for (int i = 0; i < data.length(); i++) {
			char key = data.charAt(i);
			if (ht.containsKey(String.valueOf(key))) {
				ht.put(String.valueOf(key), ht.get(String.valueOf(key)) + 1);

			} else {
				ht.put(String.valueOf(key), 1);
			}
		}
		return ht;
	}

	public static BTNode<String, Integer> huffmanTree(Map<String, Integer> fD) {
		int size = fD.size();
		boolean isEmpty =true;
		SortedList<BTNode<String,Integer>> sL = new SortedLinkedList<BTNode<String,Integer>>();
		List<String> keys = fD.getKeys();
		List<Integer> vals = fD.getValues();

		for (int i = 0; i < fD.size(); i++) {
			BTNode<String, Integer> node = new BTNode<String, Integer>(keys.get(i), vals.get(i));
			sL.add(node);
		}

		for (int i = 1; i <= size - 1; i++) {
			BTNode<String, Integer> n = new BTNode<String, Integer>(); 

			BTNode<String, Integer> x = minFreq(sL);
			BTNode<String, Integer> y = minFreq(sL);
			
			if(isEmpty) {
				n.setLeftChild(x);
				n.setRightChild(y);
				isEmpty = false;
			}else {
				if(x.getValue() == y.getValue() && x.getKey().compareTo(y.getKey()) < 0) {
					n.setLeftChild(y);
					n.setRightChild(x);
	
				} else {
					n.setLeftChild(x);
					n.setRightChild(y);
				}
			}
			n.setValue(x.getValue() + y.getValue());
			n.setKey(x.getKey() + y.getKey());
			sL.add(n);
		}

		BTNode<String, Integer> nodeToReturn = minFreq(sL);
		System.out.println("Root of Huffman Tree: {" + nodeToReturn.getKey() + "," + nodeToReturn.getValue()+"}");
		System.out.println("----------------------------");
		System.out.println("Huffman Tree: ");
		BinaryTreePrinter.print(nodeToReturn);


		return nodeToReturn;

	}

	private static BTNode<String, Integer> minFreq(SortedList<BTNode<String, Integer>> sL) {
		int smallest = Integer.MAX_VALUE;
		BTNode<String,Integer> smallNode = null;

		for (int i = 0; i < sL.size(); i++) {
			if (sL.get(i).getValue() < smallest) {
				smallNode = sL.get(i);
				smallest = smallNode.getValue();

			}
		}

		for (int i = 0; i < sL.size(); i++) {
			if (smallNode.getKey().compareTo(sL.get(i).getKey()) == 0) {
				sL.removeIndex(i);
			}
		}

		return smallNode;
	}

	public static Map<String, String> huffmanCode(BTNode<String, Integer> huffmanRoot) {
		Map<String, String> ht = new HashTableSC<String, String>(new SimpleHashFunction<String>());
		Stack<String> stack = new LinkedListStack<>();
		ht = recTraversal(huffmanRoot, ht, stack);
		return ht;
	}

	private static Map<String,String> recTraversal(BTNode<String, Integer> huffmanRoot, Map<String, String> ht, Stack<String> s) {
		if(huffmanRoot == null) return ht;
		else {
			if(isLeaf(huffmanRoot)) {
				Stack<String> temp = new LinkedListStack<>();
				String encode = "";
				while (!s.isEmpty()) {
					String popped = s.pop();
					encode = popped + encode;
					temp.push(popped);
				}
				while(!temp.isEmpty()) s.push(temp.pop());

				ht.put(huffmanRoot.getKey(), encode);
			} 
			s.push("0");
			recTraversal(huffmanRoot.getLeftChild(), ht,s);
			s.pop();

			s.push("1");
			recTraversal(huffmanRoot.getRightChild(), ht,s);
			s.pop();
		}

		return ht;
	}

	private static boolean isLeaf(BTNode<String, Integer> huffmanRoot) {
		return huffmanRoot.getLeftChild() == null && huffmanRoot.getRightChild() == null;
	}
	public static String encode(Map<String, String> encodedHuffman, String inputString) {
		String result = "";

		for (int i = 0; i < inputString.length(); i++) {
			String letter = String.valueOf(inputString.charAt(i));
			if(encodedHuffman.containsKey(letter))
				result += encodedHuffman.get(letter);			

		}
		
		return result;
	}


	public static void processResults(Map<String, Integer> fD, String inputData, String outputData) {
		int bytes = 0;
		int savings = 0;
		System.out.println("----------------------------");
		System.out.println("Frequency Distribution of " + inputData + " is: ");
		fD.print(System.out);		
		System.out.println("----------------------------");
		System.out.println("Original Text: " + inputData);
		System.out.println("Encoded Text: " + outputData);
		System.out.println("----------------------------");
		System.out.println("Amount of bytes needed to store the encoded text: " + bytes);
		System.out.println("Savings percentage compared to the amount of bytes needed for the original text: " + savings + "%");
	}
}
