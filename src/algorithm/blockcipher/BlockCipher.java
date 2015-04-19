package algorithm.blockcipher;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.jar.Pack200.Unpacker;

import javax.swing.JFileChooser;
import javax.xml.bind.DatatypeConverter;


public class BlockCipher {

	public static void main(String[] args) {
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager
					.getInstalledLookAndFeels()) {
				if ("Windows".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException
				| javax.swing.UnsupportedLookAndFeelException ex) {
		}
		
//		System.out.println("muhammad harits elfahmi\n".split(" |\n").length);
		
		BlockCipher cipher = new BlockCipher();
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Welcome to BlockCipher! Please choose encryption or decryption!");
		System.out.println("1. Encryption");
		System.out.println("2. Decryption");
		boolean isEncryption = (sc.nextInt()==1);
		//sc.nextLine();
		cipher.setIsEncryption(isEncryption);
		
		if (isEncryption) {
			System.out.println("Please press enter to choose plaintext file!");
		}
		else {
			System.out.println("Please press enter to choose ciphertext file!");
		}
		sc.nextLine();
		
		
		cipher.readInput();
		
		System.out.println("Please choose mode of operation!");
		System.out.println("1. ECB");
		System.out.println("2. CBC");
		System.out.println("3. CFB 8-bit");
		System.out.println("Mode of operation :");
		int mode = sc.nextInt();
		sc.nextLine();
		
		System.out.println("Please type in the key :");
		String keyString = sc.nextLine();
		cipher.setKey(keyString);
		
		if (isEncryption) {
			System.out.println("Encryption process started...");
		}
		else {
			System.out.println("Decryption process started...");
		}
		long start = System.nanoTime();
		switch (mode) {
		case 1:
			cipher.ECB();
			break;
		case 2:
			cipher.CBC();
			break;
		case 3:
			cipher.CFB();
			break;
		}
		long time = System.nanoTime() - start;
		System.out.println("Waktu Eksekusi : "+time+" nano second");
		// Get the Java runtime
	    Runtime runtime = Runtime.getRuntime();
	    // Run the garbage collector
	    runtime.gc();
	    // Calculate the used memory
	    long memory = runtime.totalMemory() - runtime.freeMemory();
	    System.out.println("Used memory is bytes: " + memory);
	    System.out.println("Used memory is megabytes: "
	        + bytesToMegabytes(memory));
		
		if (isEncryption) {
			System.out.println("Encryption process finished!");
		}
		else {
			System.out.println("Decryption process finished!");
		}
		
		cipher.saveOutput();
		
		cipher.printByteArray(cipher.input);
		System.out.println();
		
		cipher.printByteArray(cipher.output);
		System.out.println();
		
		System.out.println("Hasil enkripsi: " + cipher.getOutput());
		
		cipher.setIsEncryption(false);
		cipher.input = new String(cipher.output, StandardCharsets.UTF_8).getBytes(StandardCharsets.UTF_8);
		cipher.printByteArray(cipher.input);
		System.out.println();
//		cipher.setKey(keyString);
//		cipher.CBC();
//		cipher.printByteArray(cipher.output);
//		System.out.println();
//		System.out.println("Hasil dekripsi: " + cipher.getOutput());
		
//		cipher.setKey("tes");
//		
//		byte[] tes = new byte[32];
//		for (int i=0; i<32; i++) {
//			tes[i] = (byte)(i*5);
//		}
//		System.out.println(Arrays.toString(tes));
//		
//		cipher.encrypt(tes, 0, tes, 0);
//		cipher.decrypt(tes, 0, tes, 0);
//		System.out.println(Arrays.toString(tes));
	}
	
	private Rijndael rijndael;
	private Serpent serpent;
	
	public byte[] input;
	public byte[] output;
	private byte[] key;
	private byte[] IV;
	private byte[] C0;
	
	private boolean isEncryption;
	
	JFileChooser fileChooser;
	
	private boolean isRijndael = false;
	private boolean isSerpent = false;
	
	public BlockCipher() {
		IV = new byte[16];
		Random random = new Random(0);
		for (int i=0; i<16; i++) {
			IV[i] = (byte)(random.nextInt() % 255);
		}
		
		C0 = new byte[32];
		for (int i=0; i<32; i++) {
			C0[i] = (byte)(random.nextInt() % 255);
		}
		
		fileChooser = new JFileChooser();
	}
	
	public void setKey(String keyString) {
		key = new byte[16];
		byte[] keyTemp = keyString.getBytes(StandardCharsets.UTF_8);
		int keyTempLength = Math.min(keyTemp.length, 16);
		System.arraycopy(keyTemp, 0, key, 16-keyTempLength, keyTempLength);

		rijndael = new Rijndael(key);
		serpent = new Serpent(key);
	}
	
	public void setIsEncryption(boolean isEncryption) {
		this.isEncryption = isEncryption;
	}
	
	public void encrypt(byte[] in, int inOff, byte[] out, int outOff) {
		if (isRijndael) {
			RijndaelEngine re = new RijndaelEngine(256);
			re.init(true, key);
			re.processBlock(in, inOff, out, outOff);
		}
		else if (isSerpent) {
			SerpentEngine se = new SerpentEngine();
			se.init(true, key);
			se.processBlock(in, inOff, out, outOff);
			se.processBlock(in, inOff+16, out, outOff+16);
		}
		else {
			byte[] L = new byte[16];
			byte[] R = new byte[16];
			byte[] temp = new byte[16];
			
			System.arraycopy(in, inOff, L, 0, 16);
			System.arraycopy(in, inOff+16, R, 0, 16);
			
			for (int i=0; i<16; i++) {
				System.arraycopy(L, 0, temp, 0, 16);
				System.arraycopy(R, 0, L, 0, 16);
				rijndael.encryptRound(R, 0, R, 0, i);
				for (int j=0; j<16; j++) {
					R[j] ^= temp[j];
				}
				
				System.arraycopy(L, 0, temp, 0, 16);
				System.arraycopy(R, 0, L, 0, 16);
				serpent.encryptRound(R, 0, R, 0, i);
				for (int j=0; j<16; j++) {
					R[j] ^= temp[j];
				}
			}
			
			System.arraycopy(L, 0, out, outOff, 16);
			System.arraycopy(R, 0, out, outOff+16, 16);
		}
	}
	
	public void decrypt(byte[] in, int inOff, byte[] out, int outOff) {
		if (isRijndael) {
			RijndaelEngine re = new RijndaelEngine(256);
			re.init(false, key);
			re.processBlock(in, inOff, out, outOff);
		}
		else if (isSerpent) {
			SerpentEngine se = new SerpentEngine();
			se.init(false, key);
			se.processBlock(in, inOff, out, outOff);
			se.processBlock(in, inOff+16, out, outOff+16);
		}
		else {
			byte[] L = new byte[16];
			byte[] R = new byte[16];
			byte[] temp = new byte[16];
			
			System.arraycopy(in, inOff, L, 0, 16);
			System.arraycopy(in, inOff+16, R, 0, 16);
			
			for (int i=15; i>=0; i--) {
				System.arraycopy(R, 0, temp, 0, 16);
				System.arraycopy(L, 0, R, 0, 16);
				serpent.encryptRound(L, 0, L, 0, i);
				for (int j=0; j<16; j++) {
					L[j] ^= temp[j];
				}
				
				System.arraycopy(R, 0, temp, 0, 16);
				System.arraycopy(L, 0, R, 0, 16);
				rijndael.encryptRound(L, 0, L, 0, i);
				for (int j=0; j<16; j++) {
					L[j] ^= temp[j];
				}
			}
			
			System.arraycopy(L, 0, out, outOff, 16);
			System.arraycopy(R, 0, out, outOff+16, 16);
		}
	}
	
	public void ECB() {
		if (isEncryption) {
			// padding to be multiples of 256 bit
			byte[] inputEncryption = Arrays.copyOf(input, input.length+(32-(input.length%32)));
			
			// encrypt using ECB
			int i=0;
			while (i<inputEncryption.length/32) {
				encrypt(inputEncryption, i*32, inputEncryption, i*32);
				i++;
			}

			// add length info to output
			byte[] length = (Integer.toString(input.length)+"#").getBytes(StandardCharsets.UTF_8);
			output = Arrays.copyOf(length, length.length+inputEncryption.length);
			System.arraycopy(inputEncryption, 0, output, length.length, inputEncryption.length);
		}
		else {
			// get length info
			String inputString = new String(input, StandardCharsets.UTF_8);
			int firstFound = inputString.indexOf('#');
			int length = Integer.parseInt(inputString.substring(0, firstFound));
			byte[] inputDecryption = Arrays.copyOfRange(input, firstFound+1, input.length);
			
			// decrypt using ECB
			int i=0;
			while (i<inputDecryption.length/32) {
				decrypt(inputDecryption, i*32, inputDecryption, i*32);
				i++;
			}
			
			// remove padding
			output = Arrays.copyOf(inputDecryption, length);
		}
	}
	
	public void CBC() {
		if (isEncryption) {
			// padding to be multiples of 256 bit
			byte[] inputEncryption = Arrays.copyOf(input, input.length+(32-(input.length%32)));
			
			// encrypt using CBC
			byte[] C = new byte[32];
			System.arraycopy(C0, 0, C, 0, 32);
			int i=0;
			while (i<inputEncryption.length/32) {
				for (int j=0; j<32; j++) {
					inputEncryption[i*32+j] ^= C[j];
				}
				encrypt(inputEncryption, i*32, inputEncryption, i*32);
				System.arraycopy(inputEncryption, i*32, C, 0, 32);
				i++;
			}
			

			// add length info to output
			byte[] length = (Integer.toString(input.length)+"#").getBytes(StandardCharsets.UTF_8);
			output = Arrays.copyOf(length, length.length+inputEncryption.length);
			System.arraycopy(inputEncryption, 0, output, length.length, inputEncryption.length);
		}
		else {
			// get length info
			String inputString = new String(input, StandardCharsets.UTF_8);
			int firstFound = inputString.indexOf('#');
			int length = Integer.parseInt(inputString.substring(0, firstFound));
			byte[] inputDecryption = Arrays.copyOfRange(input, firstFound+1, input.length);
			byte[] outputDecryption = new byte[inputDecryption.length];
			
			// decrypt using CBC
			byte[] Cbefore = new byte[32];
			System.arraycopy(C0, 0, Cbefore, 0, 32);
			int i=0;
			while (i<inputDecryption.length/32) {
				decrypt(inputDecryption, i*32, outputDecryption, i*32);
				for (int j=0; j<32; j++) {
					outputDecryption[i*32+j] ^= Cbefore[j];
				}
				System.arraycopy(inputDecryption, i*32, Cbefore, 0, 32);
				i++;
			}
			
			// remove padding
			output = Arrays.copyOf(outputDecryption, length);
		}
	}

	public void CFB() {
		if (isEncryption) {
			// padding to be multiples of 256 bit
			byte[] inputEncryption = Arrays.copyOf(input, input.length+(32-(input.length%32)));

						
			// TODO encrypt using CFB
			byte[] queue = new byte[32];
			byte[] queueTemp = new byte[32];
			byte[] tempEncryption = new byte[32];
			System.arraycopy(C0, 0, queue, 0, 32);
			int i=0;
			while (i<inputEncryption.length) {
				encrypt(queue, 0, tempEncryption, 0);
				inputEncryption[i] ^= tempEncryption[0];
			
				System.arraycopy(queue, 1, queueTemp, 0, 31);
				System.arraycopy(inputEncryption, i, queueTemp, 31, 1);
				System.arraycopy(queueTemp, 0, queue, 0, 32);
				i++;
			}
			
			// add length info to output
			byte[] length = (Integer.toString(input.length)+"#").getBytes(StandardCharsets.UTF_8);
			output = Arrays.copyOf(length, length.length+inputEncryption.length);
			System.arraycopy(inputEncryption, 0, output, length.length, inputEncryption.length);
		}
		else {
			// get length info
			String inputString = new String(input, StandardCharsets.UTF_8);
			int firstFound = inputString.indexOf('#');
			int length = Integer.parseInt(inputString.substring(0, firstFound));
			byte[] inputDecryption = Arrays.copyOfRange(input, firstFound+1, input.length);
			byte[] outputDecryption = new byte[32];
			outputDecryption = inputDecryption.clone();
			
			// TODO decrypt using CFB
			byte[] queue = new byte[32];
			byte[] tempDecryption = new byte[32];
			byte[] queueTemp = new byte[32];
			System.arraycopy(C0, 0, queue, 0, 32);
			int i=0;
			while (i<inputDecryption.length) {
				encrypt(queue, 0, tempDecryption, 0);
				outputDecryption[i] ^= tempDecryption[0];
				
				System.arraycopy(queue, 1, queueTemp, 0, 31);
				System.arraycopy(inputDecryption, i, queueTemp, 31, 1);
				System.arraycopy(queueTemp, 0, queue, 0, 32);
				i++;
			}
			
			
			// remove padding
			output = Arrays.copyOf(outputDecryption, length);
		}
	}
	
	public void setInput(String in){
		input = in.getBytes(StandardCharsets.UTF_8);
	}
	
	public void setInputWithByteString(String _byteStrings){
		_byteStrings = _byteStrings.replaceAll("\r\n", "\n");
		String byteStrings[] = _byteStrings.split(" |\n");
		byte _input[] = new byte[byteStrings.length];
		for(int i=0;i<_input.length;i++){
			_input[i] = Byte.parseByte(byteStrings[i]);
		}
		input = _input;
	}
	
	public void readInput() {
		
		if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			try {
				input = Files.readAllBytes(file.toPath());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	public void saveOutput() {
		
		if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			try {
				Files.write(file.toPath(), output);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	public String getOutput(){
		String str = "";
		str = new String(output, StandardCharsets.UTF_8);
		return str;
	}
	
	private static final long MEGABYTE = 1024L * 1024L;

	public static long bytesToMegabytes(long bytes) {
	    return bytes / MEGABYTE;
	}
	
	public void printByteArray(byte[] bytes) {
		for (int i=0; i<bytes.length; i++) {
			System.out.print(bytes[i]+" ");
		}
	}
	
	public String getByteString(){
		String result = "";
		for(int i=0;i<output.length;i++){
			result += output[i];
			if(i != output.length - 1){
				result += " ";
			}
		}
		return result;
	}
}
