package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;

public class Huffman {

	public static HuffCode[] huffCodeArray;
	public static int huffCodeArraySize = 0;
	static int compressedSize;
	static int originalSize;
	public static String outFileName;
	public static String heder;

	public int[] compress(File file) throws IOException {
		String filepath = file.getPath();
		FileInputStream scan = new FileInputStream(filepath);

		originalSize = scan.available();

		byte[] buffer = new byte[1024];	
		int[] tmp = new int[256];//تمثيل العناصر من 0 ل 255

		// Read file content into the buffer and count byte frequencies
		int size = scan.read(buffer, 0, 1024);
		int index = -1;
		do {
			for (int i = 0; i < size; i++) {
				index = buffer[i];
				if (index < 0)
					index += 256;
				if (tmp[index] == 0)
					huffCodeArraySize++;
				
				tmp[index]++;// Increment the frequency count of the byte
			}
			size = scan.read(buffer, 0, 1024);
		} while (size > 0);

		for (int i = 0; i < buffer.length; i++)
			buffer[i] = 0;
		int tracker = 0;
		int nbChar = 0;

		// Initialize an array to store Huffman codes based on the frequencies
		huffCodeArray = new HuffCode[huffCodeArraySize];

		// Loop through the frequency array and populate the Huffman code array with
		// characters and their frequencies
		for (int i = 0; i < 256; i++)
			if (tmp[i] != 0) {
				huffCodeArray[tracker++] = new HuffCode((char) i, tmp[i]);
				nbChar += tmp[i];
				tmp[i] = 0;
			}

		if (huffCodeArraySize != 1) { // If there is more than one unique byte, construct the Huffman tree
			Node[] t = new Node[huffCodeArraySize];
			Heap h = new Heap(huffCodeArraySize + 10);

			// Populate the heap with nodes representing each unique byte and their
			// frequencies
			for (int i = 0; i < huffCodeArraySize; i++) {
				t[i] = new Node(huffCodeArray[i].counter, huffCodeArray[i].character);
				h.addElt(t[i]);
			}

			// Build the Huffman tree by repeatedly adding nodes with the lowest
			// frequencies
			for (int i = 1; i <= t.length - 1; i++) { // t.lenght -1= huffCodeArraySize-1
				Node z = new Node();
				Node x = h.deleteElt(); // dequeue a node x with the lowest frequency from the heap
				Node y = h.deleteElt();
				z.freq = x.freq + y.freq;
				z.left = x;
				z.right = y;
				h.addElt(z);
			}
			getCodes(h.getElt()[1], "");// the first Node (the root)
			String result = preorderTraversal(h.getElt()[1], "");
			this.heder=result;
			
		} else {
			huffCodeArray[0].huffCode = "1";
			huffCodeArray[0].codeLength = 1;
			String result="1";
			this.heder=result;
		}
		
	
		String[] out = new String[256];
		for (int i = 0; i < huffCodeArraySize; i++)
			out[(int) huffCodeArray[i].character] = new String(huffCodeArray[i].huffCode);

		outFileName = new StringTokenizer(file.getAbsolutePath(), ".").nextToken() + ".huff";
		File f = new File(outFileName);
		FileOutputStream output = new FileOutputStream(outFileName);
		byte[] outbuffer = new byte[1024];
		tracker = 0;

		// Construct the header information
		for (int i = 0; i < filepath.length(); i++)
			outbuffer[tracker++] = (byte) filepath.charAt(i);
		outbuffer[tracker++] = '\n';

		String nbchar = String.valueOf(nbChar);
		for (int i = 0; i < nbchar.length(); i++) {
			outbuffer[tracker++] = (byte) nbchar.charAt(i);
		}
		outbuffer[tracker++] = '\n';

		for (int i = 0; i < String.valueOf(huffCodeArraySize).length(); i++)
			outbuffer[tracker++] = (byte) String.valueOf(huffCodeArraySize).charAt(i);
		outbuffer[tracker++] = '\n';

		output.write(outbuffer, 0, tracker);
		tracker = 0;
		// reset the trakter and clear outbuffer
		for (int i = 0; i < outbuffer.length; i++)
			outbuffer[i] = 0;
       // loop in huffman code
		for (int i = 0; i < huffCodeArraySize; i++) {
			if (tracker == 1024) {
				output.write(outbuffer);
				tracker = 0;
			}
			outbuffer[tracker++] = (byte) huffCodeArray[i].character;
			if (tracker == 1024) {
				output.write(outbuffer);
				tracker = 0;
			}
			outbuffer[tracker++] = (byte) huffCodeArray[i].codeLength;
			//Convert Huffman Code to Numeric Form
			String res = "";
			Long x;
			if (huffCodeArray[i].huffCode.length() > 15) { // the long =4byte=16bit
				for (int z = 0; z < huffCodeArray[i].huffCode.length() / 2; z++) {
					res += huffCodeArray[i].huffCode.charAt(z) + "";
				}
				x = Long.parseLong(res);
				res = "";
				for (int z = (huffCodeArray[i].huffCode.length() + 1) / 2; z < huffCodeArray[i].huffCode
						.length(); z++) {
					res += huffCodeArray[i].huffCode.charAt(z) + "";
				}
				x += Long.parseLong(res);

			} else {
				x = Long.parseLong(huffCodeArray[i].huffCode);
			}
			byte[] code = new byte[50];
			int l = 0;
			if (x == 0) {
				outbuffer[tracker++] = 0;
				if (tracker == 1024) {
					output.write(outbuffer);
					tracker = 0;
				}
				outbuffer[tracker++] = 0;
				if (tracker == 1024) {
					output.write(outbuffer);
					tracker = 0;
				}
			} else {
				while (x != 0) {
					if (tracker == 1024) {
						output.write(outbuffer);
						tracker = 0;
					}
					code[l++] = (byte) (x % 256); // store the last 8 bits of x
					x /= 256; // remove the last 8 bits
				}
				outbuffer[tracker++] = (byte) l; // number of bytes
				if (tracker == 1024) {
					output.write(outbuffer);
					tracker = 0;
				}
				for (int j = 0; j < l; j++) {// Write all the bytes from the code array to the outbuffer
					outbuffer[tracker++] = code[j];
					if (tracker == 1024) {
						output.write(outbuffer);
						tracker = 0;
					}
				}
			}

			if (tracker == 1024) {
				output.write(outbuffer);
				tracker = 0;
			}
			outbuffer[tracker++] = '\n';
			
		}

		output.write(outbuffer, 0, tracker);

		for (int i = 0; i < outbuffer.length; i++)
			outbuffer[i] = 0;

		scan.close();

		scan = new FileInputStream(filepath);
		tracker = 0; // Initialize a tracker to keep track of bit positions
		size = scan.read(buffer, 0, 1024);

		// Loop until all data is read from the FileInputStream
		do {
			for (int i = 0; i < size; i++) {
				index = buffer[i];// Get the current byte from the buffer
				if (index < 0)
					index += 256;
				for (int j = 0; j < out[index].length(); j++) {
					char ch = out[index].charAt(j);
					if (ch == '1') // Check if the character is 1 to set a bit in the output buffer
						outbuffer[tracker / 8] = (byte) (outbuffer[tracker / 8] | 1 << 7 - tracker % 8);// the location of the bit in the byte
					tracker++;// Move to the next bit
					if (tracker / 8 == 1024) {
						output.write(outbuffer);
						for (int k = 0; k < outbuffer.length; k++)
							outbuffer[k] = 0;
						tracker = 0;
					}
				}
			}
			size = scan.read(buffer, 0, 1024);
		} while (size > 0);
		scan.close();
		output.close();
		scan = new FileInputStream(f);
		compressedSize = scan.available();
		scan.close();

		return new int[] { originalSize, compressedSize };
	}
	
	//////////////////////////////////////////////////

	public int[] deCompress(File infile) {
		
		int[] sizes = new int[2]; // Index 0: compressed size, Index 1: decompressed size
         if (infile.exists()) {
		try {
			int size = 0;
			String fileName = infile.getPath();
			sizes[0] = compressedSize; // Set the compressed size

			int tracker = 0;
			int bufferTracker = 0;
			boolean flag = true;
			String originalFileName = "";
			File file = new File(fileName);

			try (FileInputStream scan = new FileInputStream(file)) {
				byte[] buffer = new byte[1024];

				// Get The File Name
				size = scan.read(buffer, 0, 1024);
				char[] tmp = new char[200];
				while (flag) {
					if (buffer[tracker] != '\n')
						tmp[tracker++] = (char) buffer[bufferTracker++];
					else
						flag = false;
				}

				bufferTracker++;
				originalFileName = String.valueOf(tmp, 0, tracker);

				// Get the Number of Characters in the file
				long nbChar = 0;
				tracker = 0;
				flag = true;
				while (flag) {
					if (bufferTracker == 1024) {
						size = scan.read(buffer, 0, 1024);
						bufferTracker = 0;
					}
					if (buffer[bufferTracker] != '\n')
						tmp[tracker++] = (char) buffer[bufferTracker++];
					else
						flag = false;
				}
				nbChar = Long.parseLong(String.valueOf(tmp, 0, tracker));
				bufferTracker++;

				// Get the Number of Distinct characters
				int loopSize = 0;
				tracker = 0;
				flag = true;
				while (flag) {
					if (bufferTracker == 1024) {
						size = scan.read(buffer, 0, 1024);
						bufferTracker = 0;
					}
					if (buffer[bufferTracker] != '\n')
						tmp[tracker++] = (char) buffer[bufferTracker++];
					else
						flag = false;
				}
				loopSize = Integer.parseInt(String.valueOf(tmp, 0, tracker));
				bufferTracker++;
				// Specify the new output file name
				String outputFileName = originalFileName;

				// Specify the new output file
				FileOutputStream output = new FileOutputStream(outputFileName);

				// Reading the huff Code
				huffCodeArray = new HuffCode[loopSize];
				huffCodeArraySize = loopSize;
				for (int i = 0; i < loopSize; i++) {
					huffCodeArray[i] = new HuffCode((char) Byte.toUnsignedInt(buffer[bufferTracker++]));
					if (bufferTracker == 1024) {
						size = scan.read(buffer, 0, 1024);
						bufferTracker = 0;
					}
					huffCodeArray[i].codeLength = buffer[bufferTracker++];
					if (bufferTracker == 1024) {
						size = scan.read(buffer, 0, 1024);
						bufferTracker = 0;
					}
					int l = buffer[bufferTracker++];
					if (bufferTracker == 1024) {
						size = scan.read(buffer, 0, 1024);
						bufferTracker = 0;
					}
					if (l == 0)
						bufferTracker++;
					if (bufferTracker == 1024) {
						size = scan.read(buffer, 0, 1024);
						bufferTracker = 0;
					}
					long x = 0;
					for (int j = 0; j < l; j++) {
						x += Byte.toUnsignedLong(buffer[bufferTracker++]) * (1L << 8 * j);
						if (bufferTracker == 1024) {
							size = scan.read(buffer, 0, 1024);
							bufferTracker = 0;
						}
					}
					huffCodeArray[i].huffCode = String.valueOf(x);
					if (huffCodeArray[i].huffCode.length() != huffCodeArray[i].codeLength) {
						String s = "";
						for (int j = 0; j < huffCodeArray[i].codeLength - huffCodeArray[i].huffCode.length(); j++)
							s += "0";
						s += huffCodeArray[i].huffCode;
						huffCodeArray[i].huffCode = s;
					}
					bufferTracker++;
					if (bufferTracker == 1024) {
						size = scan.read(buffer, 0, 1024);
						bufferTracker = 0;
					}
				}

				// Reconstruct the Huffman tree
				BinaryTree tree = new BinaryTree();
				// rebuild the tree based on Huffman codes and characters
				for (int i = 0; i < huffCodeArraySize; i++) {
					tree = BinaryTree.addElt(tree, huffCodeArray[i].huffCode, 0, huffCodeArray[i].character);
				}
				BinaryTree root = tree;

				if (bufferTracker == 1024) { // Check buffer tracker to manage buffer size
					size = scan.read(buffer, 0, 1024);
					bufferTracker = 0; // Reset buffer tracker to process from the start
				}

				int index = bufferTracker;
				bufferTracker = 0;
				byte[] outputBuffer = new byte[1024];
				tracker = 0;
				String name = "";
				File n = new File(name);

				if (n.exists())// Check if a file with the specified name exists and delete it if it does
					n.delete();

				long count = 0;
				flag = false;
				do {// Loop to reconstruct data using the Huffman tree
					BinaryTree currentNode = root;
					while (currentNode != null && (currentNode.left != null || currentNode.right != null)) {
						if ((buffer[index] & (1 << 7 - bufferTracker)) == 0)
							currentNode = currentNode.left;
						else
							currentNode = currentNode.right;
						bufferTracker++;
						// Check if the entire buffer has been processed and read more data if needed
						if (bufferTracker == 8) {
							bufferTracker = 0;
							index++;
							if (index == 1024) {
								size = scan.read(buffer, 0, 1024);
								index = 0;
								if (size == -1)
									flag = true;// all data has been read
							}
						}
					}
					if (flag)
						break;
					// Store the decoded data from the Huffman tree traversal into the output buffer
					outputBuffer[tracker++] = (byte) (currentNode != null ? currentNode.ch : 0);
					// Write data when the output buffer is full
					if (tracker == 1024) {
						output.write(outputBuffer);
						tracker = 0;
					}
					count++;
					if (count == nbChar) // the count reaches the expected number of characters
						break;
				} while (size != -1);
				output.write(outputBuffer, 0, tracker);
				output.close();

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
         else {
             System.out.println("File not found: " + infile.getAbsolutePath());
             // Handle the case where the file doesn't exist (e.g., create a new file)
         }
		return sizes;
	}

	// traversing a Huffman tree to generate the Huffman codes for each character in
	public static void getCodes(Node t, String st) {
		if (t != null) {
			if (t.left == null && t.right == null) {
				for (int i = 0; i < huffCodeArraySize; i++) {
					if (huffCodeArray[i].character == t.ch) {
						huffCodeArray[i].huffCode = st;
						huffCodeArray[i].codeLength = st.length();
						break;
					}
				}
			} else {
				getCodes(t.left, st + '0');
				getCodes(t.right, st + '1');
			}
		}
	}
	//preorder tree for header 
	public String preorderTraversal(Node root,String code) {
	    if (root == null) {
	        return ""; // Base case: If the node is null, return an empty string
	    }

	    StringBuilder result = new StringBuilder();

	    // Check if the node is a leaf node (it contains a character)
	    if (root.left == null && root.right == null) {
	        result .append(code);
	    }

	    // Traverse the left subtree, appending '0' to the code
	    result.append(preorderTraversal(root.left, code + "0"));

	    // Traverse the right subtree, appending '1' to the code
	    result.append(preorderTraversal(root.right, code + "1"));

	    return result.toString();
	}

}