import java.util.*;
import java.nio.charset.StandardCharsets;

public class HuffmanCompression {
    static class HuffmanNode implements Comparable<HuffmanNode> {
        Character character;
        Integer frequency;
        HuffmanNode left;
        HuffmanNode right;

        HuffmanNode(Character character, Integer frequency) {
            this.character = character;
            this.frequency = frequency;
            this.left = null;
            this.right = null;
        }

        @Override
        public int compareTo(HuffmanNode other) {
            return this.frequency.compareTo(other.frequency);
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

    private static HuffmanNode buildHuffmanTree(Map<Character, Integer> freqMap) {
        PriorityQueue<HuffmanNode> heap = new PriorityQueue<>();
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

    private static void generateCodes(HuffmanNode node, String prefix, Map<Character, String> codes) {
        if (node != null) {
            if (node.character != null) {
                codes.put(node.character, prefix);
            }
            generateCodes(node.left, prefix + "0", codes);
            generateCodes(node.right, prefix + "1", codes);
        }
    }

    public static EncodingResult huffmanEncode(String text) {
        // Count character frequencies
        Map<Character, Integer> freqMap = new HashMap<>();
        for (char c : text.toCharArray()) {
            freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);
        }

        // Special case: Only one unique character
        if (freqMap.size() == 1) {
            char uniqueChar = freqMap.keySet().iterator().next();
            Map<Character, String> codes = new HashMap<>();
            codes.put(uniqueChar, "0");
            return new EncodingResult("0".repeat(text.length()), codes, 
                new HuffmanNode(uniqueChar, freqMap.get(uniqueChar)));
        }

        HuffmanNode root = buildHuffmanTree(freqMap);
        Map<Character, String> codes = new HashMap<>();
        generateCodes(root, "", codes);

        StringBuilder encodedText = new StringBuilder();
        for (char c : text.toCharArray()) {
            encodedText.append(codes.get(c));
        }

        return new EncodingResult(encodedText.toString(), codes, root);
    }

    public static BinaryResult encodeBinaryToBytes(String binaryStr) {
        if (binaryStr.isEmpty()) {
            return new BinaryResult(new byte[]{0}, 0);
        }

        int padding = (8 - binaryStr.length() % 8) % 8;
        binaryStr = binaryStr + "0".repeat(padding);

        byte[] bytes = new byte[binaryStr.length() / 8];
        for (int i = 0; i < binaryStr.length(); i += 8) {
            bytes[i / 8] = (byte) Integer.parseInt(binaryStr.substring(i, i + 8), 2);
        }

        return new BinaryResult(bytes, padding);
    }

    public static String decodeBytesToBinary(byte[] byteData, int padding) {
        StringBuilder binary = new StringBuilder();
        for (byte b : byteData) {
            binary.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
        }
        return padding > 0 ? binary.substring(0, binary.length() - padding) : binary.toString();
    }

    public static String huffmanDecode(String encodedText, Map<Character, String> codes) {
        Map<String, Character> reverseCode = new HashMap<>();
        for (Map.Entry<Character, String> entry : codes.entrySet()) {
            reverseCode.put(entry.getValue(), entry.getKey());
        }

        StringBuilder buffer = new StringBuilder();
        StringBuilder decodedText = new StringBuilder();

        for (char bit : encodedText.toCharArray()) {
            buffer.append(bit);
            if (reverseCode.containsKey(buffer.toString())) {
                decodedText.append(reverseCode.get(buffer.toString()));
                buffer.setLength(0);
            }
        }

        return decodedText.toString();
    }

    public static int byteSize(String s) {
        return s.getBytes(StandardCharsets.UTF_8).length;
    }

    public static void main(String[] args) {
        String text = "[sale.invoice, sales.invoice.payment, sales.qouation, purchase.bill, purchase.bill.payment]";
        System.out.println("Original text size: " + byteSize(text) + " bytes");

        long start = System.nanoTime();
        // Huffman Encoding
        EncodingResult encodingResult = huffmanEncode(text);
        System.out.println("Encoded Binary Length: " + encodingResult.encodedText.length() + " bits");

        // Convert Binary to Bytes
        BinaryResult binaryResult = encodeBinaryToBytes(encodingResult.encodedText);
        System.out.println("Compressed Size: " + binaryResult.data.length + " bytes");
        System.out.printf("time taken %.2f ms%n", (System.nanoTime() - start) / 1_000_000.0);

        start = System.nanoTime();
        // Decode Bytes to Binary
        String binaryFromBytes = decodeBytesToBinary(binaryResult.data, binaryResult.padding);

        // Decode Huffman
        String decodedText = huffmanDecode(binaryFromBytes, encodingResult.codes);
        System.out.println("Decoded text size: " + byteSize(decodedText) + " bytes");
        System.out.printf("time taken %.2f ms%n", (System.nanoTime() - start) / 1_000_000.0);

        if (text.equals(decodedText)) {
            System.out.println("Decoded text matches with original text");
        }

        // Validate Correctness
        assert text.equals(decodedText) : "Decoding failed!";
    }
}