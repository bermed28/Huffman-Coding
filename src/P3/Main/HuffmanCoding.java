package P3.Main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

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
		String data = load_data("stringData.txt");
		Map<String, Integer> fD = compute_fd(data);
		BTNode<String, Integer> huffmanRoot = huffman_tree(fD);
		Map<String,String> encodedHuffman = huffman_code(huffmanRoot);
		String output = encode(encodedHuffman, data);
		process_results(fD, encodedHuffman,data,output);

	}



	public static String load_data(String inputFile) {
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

	public static Map<String, Integer> compute_fd(String data) {
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

	public static BTNode<String, Integer> huffman_tree(Map<String, Integer> fD) {
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


			if(x.getValue() == y.getValue() && y.getKey().compareTo(x.getKey()) < 0) {
				n.setLeftChild(y);
				n.setRightChild(x);

			} else {
				n.setLeftChild(x);
				n.setRightChild(y);
			}

			n.setValue(x.getValue() + y.getValue());
			n.setKey(x.getKey() + y.getKey());
			sL.add(n);
		}

		BTNode<String, Integer> nodeToReturn = minFreq(sL);
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

	public static Map<String, String> huffman_code(BTNode<String, Integer> huffmanRoot) {
		Map<String, String> ht = new HashTableSC<String, String>(new SimpleHashFunction<String>());
		Stack<String> stack = new LinkedListStack<>();
		ht = buildPrexixCodeMap(huffmanRoot, ht, stack);
		return ht;
	}

	private static Map<String,String> buildPrexixCodeMap(BTNode<String, Integer> huffmanRoot, Map<String, String> ht, Stack<String> s) {
		if(huffmanRoot == null) return ht;
		else {
			if(isLeaf(huffmanRoot)) {
				Stack<String> temp = new LinkedListStack<>();
				String encode = "";
				encode = generatePrefixCode(encode,s, temp);
				ht.put(huffmanRoot.getKey(), encode);
			} 
			s.push("0");
			buildPrexixCodeMap(huffmanRoot.getLeftChild(), ht,s);
			s.pop();

			s.push("1");
			buildPrexixCodeMap(huffmanRoot.getRightChild(), ht,s);
			s.pop();
		}
		
		return ht;
	}

	private static String generatePrefixCode(String encode, Stack<String> s, Stack<String> temp) {
		while (!s.isEmpty()) {
			String popped = s.pop();
			encode = popped + encode;
			temp.push(popped);
		}
		while(!temp.isEmpty()) s.push(temp.pop());
		return encode;
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


	public static void process_results(Map<String, Integer> fD, Map<String, String> encodedHuffman, String inputData, String outputData) {
		int bytes = 0;
		int encodedBytes = 0;
		double savings = 0.0;
		int size = fD.size() - 1;
		List<String> letters = fD.getKeys();
		List<Integer> frequencies = fD.getValues();
		List<String> prefixCodes = encodedHuffman.getValues();
		
		System.out.println("Symbol" + "\t\t" + "Frequency" + '\t' + "Code");
		System.out.println("------" + "\t\t" + "---------" + "\t" + "----");
		for (int i = size; i >= 0; i--) {
			System.out.println(letters.get(i) + "\t\t" + frequencies.get(i) + "\t\t" + prefixCodes.get(i));
		}
		System.out.println();
		
		System.out.println("Original String: \n" + inputData);
		System.out.println("Encoded String: \n" + outputData + '\n');
		System.out.println("The original string requires " + bytes + " bytes");
		System.out.println("The encoded string requires " + encodedBytes + " bytes");
		System.out.println("Difference in space required is " + savings + "%");
		
		processOutputFile(inputData, outputData, letters, frequencies, prefixCodes, bytes, encodedBytes, savings);
	}

	private static void processOutputFile(String inputData, String outputData, List<String> letters, 
										  List<Integer> frequencies, List<String> prefixCodes,
										  int bytes, int encodedBytes, double savings) {
		int size = letters.size() - 1;
		Writer writer = null;
		try {	

			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream("huffmanResults.txt"), "utf-8"));
			
			writer.write("Symbol" + "\t\t" + "Frequency" + '\t' + "Code\n");
			writer.write("------" + "\t\t" + "---------" + "\t" + "----\n");
			for (int i = size; i >= 0; i--) {
				writer.write(letters.get(i) + "\t\t\t" + frequencies.get(i) + "\t\t\t" + prefixCodes.get(i) + '\n');
			}
			writer.write('\n');
			
			writer.write("Original String: \n" + inputData + '\n');
			writer.write("Encoded String: \n" + outputData + "\n\n");
			writer.write("The original string requires " + bytes + " bytes\n");
			writer.write("The encoded string requires " + encodedBytes + " bytes\n");
			writer.write("Difference in space required is " + savings + "%");
			
			
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}catch(IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(writer != null) {
					writer.close();
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
}
