import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;


public class HuffmanCompressionTest {
  public static int testNo = 1;

//   public static void testCase(String testName, String input) {
//       System.out.println("\n=== Test-"+testNo+": " + testName + " ===");
//       testNo++;
//       // System.out.println("Input: \"" + input + "\"");
//       System.out.println("Input length: " + input.length() + " characters");
//       try {
//           HuffmanCompression.EncodingResult encodingResult = HuffmanCompression.huffmanEncode(input);
//           HuffmanCompression.BinaryResult binaryResult = HuffmanCompression.encodeBinaryToBytes(encodingResult.encodedText);
          
//           int originalSize = HuffmanCompression.byteSize(input);
//           System.out.println("Original size: " + originalSize + " bytes");
//           System.out.println("Compressed size: " + binaryResult.data.length + " bytes");
          
//           String binaryFromBytes = HuffmanCompression.decodeBytesToBinary(binaryResult.data, binaryResult.padding);
//           String decodedText = HuffmanCompression.huffmanDecode(binaryFromBytes, encodingResult.codes);

//           double compressionRatio = ((double)binaryResult.data.length / originalSize)*100;

//         System.out.printf("Compression ratio: %.2f%%\n", compressionRatio);
          
//           boolean matches = input.equals(decodedText);
//           System.out.println("Decode matches input: " + matches);
//           if (!matches) {
//               System.out.println("Mismatch!! Decoded text: \"" + decodedText + "\"");
//               throw new Exception("Decoded text does not match input");
//           }
//       } catch (Exception e) {
//           System.out.println("Test failed with exception: " + e.getMessage());
//           e.printStackTrace();
//       }
//   }


  public static void testCase(String testName, String input) {
    System.out.println("\n=== Test-"+testNo+": " + testName + " ===");
    testNo++;
    // System.out.println("Input: \"" + input + "\"");
    System.out.println("Input length: " + input.length() + " characters");
    
    try {
        OptimizedHuffmanCompression.EncodingResult encodingResult = OptimizedHuffmanCompression.huffmanEncode(input);
        OptimizedHuffmanCompression.BinaryResult binaryResult = OptimizedHuffmanCompression.encodeBinaryToBytes(encodingResult.encodedText);
        
        int originalSize = OptimizedHuffmanCompression.byteSize(input);
        System.out.println("Original size: " + originalSize + " bytes");
        System.out.println("Compressed size: " + binaryResult.data.length + " bytes");
        
        String binaryFromBytes = OptimizedHuffmanCompression.decodeBytesToBinary(binaryResult.data, binaryResult.padding);
        String decodedText = OptimizedHuffmanCompression.huffmanDecode(binaryFromBytes, encodingResult.codes);

        double compressionRatio = ((double)binaryResult.data.length / originalSize)*100;

      System.out.printf("Compression ratio: %.2f%%\n", compressionRatio);
        
        boolean matches = input.equals(decodedText);
        System.out.println("Decode matches input: " + matches);
        if (!matches) {
            System.out.println("Mismatch!! Decoded text: \"" + decodedText + "\"");
            throw new Exception("Decoded text does not match input");
        }
    } catch (Exception e) {
        System.out.println("Test failed with exception: " + e.getMessage());
        e.printStackTrace();
    }
}


  public static void main(String[] args) {
      // Test Case 1: Empty string
      testCase("Empty String", "");

      // Test Case 2: Single character
      testCase("Single Character", "a");

      // Test Case 3: Single character repeated
      testCase("Repeated Character", "aaaaaaaaaa");

      // Test Case 4: Two different characters
      testCase("Two Characters", "ab");

      // Test Case 5: Two characters with different frequencies
      testCase("Two Characters Different Frequencies", "aaaaabbb");

      // Test Case 6: Special characters
      testCase("Special Characters", "!@#$%^&*()_+");

      // Test Case 7: Unicode characters
      testCase("Unicode Characters", "Hello, 世界! 🌍");

      // Test Case 8: Long repeated pattern
      testCase("Long Repeated Pattern", "abcabcabcabc".repeat(100));

      // Test Case 9: Whitespace handling
      testCase("Whitespace", "   \t\n\r   ");

      // Test Case 10: Binary-looking string
      testCase("Binary String", "1010101010");

      // Test Case 11: JSON-like structure
      testCase("JSON Structure", "{\"key1\": \"value1\", \"key2\": [1,2,3]}");

      // Test Case 12: URL-like string
      testCase("URL", "https://www.example.com/path?param1=value1&param2=value2");

      // Test Case 13: Mixed case with numbers
      testCase("Mixed Case and Numbers", "AbC123dEf456gHi789");

      // Test Case 14: Very long string with limited character set
      testCase("Long Limited Charset", "ABC".repeat(1000));

      // Test Case 15: All ASCII printable characters
      StringBuilder asciiChars = new StringBuilder();
      for (int i = 32; i < 127; i++) {
          asciiChars.append((char)i);
      }
      testCase("ASCII Printable Characters", asciiChars.toString());

      // Test Case 16: Large repetitive text
      testCase("Large Repetitive", "The quick brown fox jumps over the lazy dog. ".repeat(100));

      // Test Case 17: XML-like structure
      testCase("XML Structure", "<root><child attr=\"value\">content</child></root>");

      // Test Case 18: Multiple line breaks
      testCase("Multiple Line Breaks", "Line 1\nLine 2\nLine 3\nLine 4\nLine 5");

      // Test Case 19: Random characters
      StringBuilder randomChars = new StringBuilder();
      Random random = new Random(42); // Fixed seed for reproducibility
      for (int i = 0; i < 1000; i++) {
          randomChars.append((char)(random.nextInt(26) + 'a'));
      }
      testCase("Random Characters", randomChars.toString());

      // Test Case 20: Path-like string with different separators
      testCase("Path String", "C:\\Program Files\\App\\file.txt/usr/local/bin/");
  }
}