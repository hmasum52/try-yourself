import java.util.*;
import java.nio.charset.StandardCharsets;

public class OptimizedHuffmanCompression {
    static class HuffmanNode implements Comparable<HuffmanNode> {
        Character character;
        int frequency;
        HuffmanNode left;
        HuffmanNode right;

        HuffmanNode(Character character, int frequency) {
            this.character = character;
            this.frequency = frequency;
        }

        @Override
        public int compareTo(HuffmanNode other) {
            return Integer.compare(this.frequency, other.frequency);
        }
    }

    static class EncodingResult {
        String encodedText;
        Map<Character, String> codes;
        HuffmanNode root;

        EncodingResult(String encodedText, Map<Character, String> codes, HuffmanNode root) {
            this.encodedText = encodedText;
            this.codes = codes;
            this.root = root;
        }
    }

    static class BinaryResult {
        byte[] data;
        int padding;

        BinaryResult(byte[] data, int padding) {
            this.data = data;
            this.padding = padding;
        }
    }

    // Optimized encoding with better frequency counting and memory usage
    public static EncodingResult huffmanEncode(String text) {
        if (text == null || text.isEmpty()) {
            return new EncodingResult("", new HashMap<>(), null);
        }

        // Use array for faster frequency counting for ASCII
        int[] freqArray = new int[128];
        char[] textChars = text.toCharArray();
        Map<Character, Integer> freqMap = new HashMap<>();
        
        // Count frequencies
        for (char c : textChars) {
            if (c < 128) {
                freqArray[c]++;
            } else {
                freqMap.merge(c, 1, Integer::sum);
            }
        }
        
        // Convert array counts to map for non-zero frequencies
        for (int i = 0; i < freqArray.length; i++) {
            if (freqArray[i] > 0) {
                freqMap.put((char)i, freqArray[i]);
            }
        }

        // Handle single character case efficiently
        if (freqMap.size() == 1) {
            Map.Entry<Character, Integer> entry = freqMap.entrySet().iterator().next();
            char uniqueChar = entry.getKey();
            Map<Character, String> codes = new HashMap<>(1);
            codes.put(uniqueChar, "0");
            char[] zeros = new char[text.length()];
            Arrays.fill(zeros, '0');
            return new EncodingResult(new String(zeros), codes, 
                new HuffmanNode(uniqueChar, entry.getValue()));
        }

        // Build Huffman tree and generate codes
        HuffmanNode root = buildHuffmanTree(freqMap);
        Map<Character, String> codes = new HashMap<>(freqMap.size());
        generateCodes(root, "", codes);

        // Optimize encoding using byte operations
        int totalBits = 0;
        for (char c : textChars) {
            totalBits += codes.get(c).length();
        }

        StringBuilder encodedText = new StringBuilder(totalBits);
        for (char c : textChars) {
            encodedText.append(codes.get(c));
        }

        return new EncodingResult(encodedText.toString(), codes, root);
    }

    // Optimized binary encoding with improved bit manipulation
    public static BinaryResult encodeBinaryToBytes(String binaryStr) {
        if (binaryStr.isEmpty()) {
            return new BinaryResult(new byte[]{0}, 0);
        }
        
        int padding = (8 - binaryStr.length() % 8) % 8;
        int byteLength = (binaryStr.length() + padding) / 8;
        byte[] bytes = new byte[byteLength];
        
        char[] bits = binaryStr.toCharArray();
        int byteValue = 0;
        int bitCount = 0;
        int byteIndex = 0;
        
        for (int i = 0; i < bits.length; i++) {
            byteValue = (byteValue << 1) | (bits[i] - '0');
            bitCount++;
            
            if (bitCount == 8) {
                bytes[byteIndex++] = (byte)byteValue;
                byteValue = 0;
                bitCount = 0;
            }
        }
        
        // Handle remaining bits with padding
        if (bitCount > 0) {
            byteValue <<= (8 - bitCount);
            bytes[byteIndex] = (byte)byteValue;
        }
        
        return new BinaryResult(bytes, padding);
    }

    // Optimized binary decoding
    public static String decodeBytesToBinary(byte[] byteData, int padding) {
        if (byteData == null || byteData.length == 0) {
            return "";
        }
        
        StringBuilder binary = new StringBuilder(byteData.length * 8);
        for (byte b : byteData) {
            for (int i = 7; i >= 0; i--) {
                binary.append((b >> i) & 1);
            }
        }
        
        return padding > 0 ? binary.substring(0, binary.length() - padding) : binary.toString();
    }

    // Optimized Huffman decoding with better memory management
    public static String huffmanDecode(String encodedText, Map<Character, String> codes) {
        if (encodedText.isEmpty()) {
            return "";
        }
        
        Map<String, Character> reverseCode = new HashMap<>(codes.size());
        for (Map.Entry<Character, String> entry : codes.entrySet()) {
            reverseCode.put(entry.getValue(), entry.getKey());
        }
        
        char[] bits = encodedText.toCharArray();
        StringBuilder decodedText = new StringBuilder(encodedText.length());
        StringBuilder buffer = new StringBuilder(16);
        
        for (char bit : bits) {
            buffer.append(bit);
            Character c = reverseCode.get(buffer.toString());
            if (c != null) {
                decodedText.append(c);
                buffer.setLength(0);
            }
        }
        
        return decodedText.toString();
    }

    private static void generateCodes(HuffmanNode node, String prefix, Map<Character, String> codes) {
        if (node != null) {
            if (node.character != null) {
                codes.put(node.character, prefix);
            } else {
                generateCodes(node.left, prefix + "0", codes);
                generateCodes(node.right, prefix + "1", codes);
            }
        }
    }

    private static HuffmanNode buildHuffmanTree(Map<Character, Integer> freqMap) {
        PriorityQueue<HuffmanNode> heap = new PriorityQueue<>(freqMap.size());
        
        for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
            heap.offer(new HuffmanNode(entry.getKey(), entry.getValue()));
        }

        while (heap.size() > 1) {
            HuffmanNode left = heap.poll();
            HuffmanNode right = heap.poll();
            HuffmanNode merged = new HuffmanNode(null, left.frequency + right.frequency);
            merged.left = left;
            merged.right = right;
            heap.offer(merged);
        }

        return heap.poll();
    }

    public static int byteSize(String s) {
        return s.getBytes(StandardCharsets.UTF_8).length;
    }

    public static void main(String[] args) {
        String text = "[sale.invoice, sales.invoice.payment, sales.qouation, purchase.bill, purchase.bill.payment]";
        System.out.println("Original text size: " + byteSize(text) + " bytes");

        long startEncode = System.nanoTime();
        
        // Huffman Encoding
        EncodingResult encodingResult = huffmanEncode(text);
        System.out.println("Encoded Binary Length: " + encodingResult.encodedText.length() + " bits");

        // Convert Binary to Bytes
        BinaryResult binaryResult = encodeBinaryToBytes(encodingResult.encodedText);
        System.out.println("Compressed Size: " + binaryResult.data.length + " bytes");
        
        double encodeTime = (System.nanoTime() - startEncode) / 1_000_000.0;
        System.out.printf("Encoding time: %.3f ms%n", encodeTime);

        long startDecode = System.nanoTime();
        
        // Decode Bytes to Binary
        String binaryFromBytes = decodeBytesToBinary(binaryResult.data, binaryResult.padding);

        // Decode Huffman
        String decodedText = huffmanDecode(binaryFromBytes, encodingResult.codes);
        
        double decodeTime = (System.nanoTime() - startDecode) / 1_000_000.0;
        System.out.printf("Decoding time: %.3f ms%n", decodeTime);

        System.out.println("Decoded text size: " + byteSize(decodedText) + " bytes");
        
        // Calculate compression ratio
        double compressionRatio = (double) binaryResult.data.length / byteSize(text) * 100;
        System.out.printf("Compression ratio: %.2f%%%n", compressionRatio);

        if (text.equals(decodedText)) {
            System.out.println("Decoded text matches with original text");
        }

        // Validate correctness
        assert text.equals(decodedText) : "Decoding failed!";
    }
}