import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

class HuffmanNode implements Comparable<HuffmanNode>, Serializable {
    char data;
    int frequency;
    HuffmanNode left, right;

    public HuffmanNode(char data, int frequency) {
        this.data = data;
        this.frequency = frequency;
    }

    @Override
    public int compareTo(HuffmanNode node) {
        return this.frequency - node.frequency;
    }
}

public class HuffmanFileCompression {

    public static void main(String[] args) {
        String inputFile = "input.txt";
        String compressedFile = "compressed.bin";
        

        compressFile(inputFile, compressedFile);
        
    }

    static void compressFile(String inputFile, String compressedFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(compressedFile))) {

            String input = reader.lines().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
            Map<Character, Integer> frequencyMap = buildFrequencyMap(input);
            HuffmanNode root = buildHuffmanTree(frequencyMap);
            Map<Character, String> huffmanCodes = buildHuffmanCodes(root);

            byte[] compressedData = compress(input, huffmanCodes);

            outputStream.writeObject(root);
            outputStream.writeInt(compressedData.length);
            outputStream.write(compressedData);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<Character, Integer> buildFrequencyMap(String input) {
        Map<Character, Integer> frequencyMap = new HashMap<>();
        for (char c : input.toCharArray()) {
            frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
        }
        return frequencyMap;
    }

    private static HuffmanNode buildHuffmanTree(Map<Character, Integer> frequencyMap) {
        PriorityQueue<HuffmanNode> priorityQueue = new PriorityQueue<>();
        frequencyMap.forEach((key, value) -> priorityQueue.add(new HuffmanNode(key, value)));

        while (priorityQueue.size() > 1) {
            HuffmanNode left = priorityQueue.poll();
            HuffmanNode right = priorityQueue.poll();

            HuffmanNode parent = new HuffmanNode('\0', left.frequency + right.frequency);
            parent.left = left;
            parent.right = right;

            priorityQueue.add(parent);
        }

        return priorityQueue.poll();
    }

    private static Map<Character, String> buildHuffmanCodes(HuffmanNode root) {
        Map<Character, String> huffmanCodes = new HashMap<>();
        buildHuffmanCodesHelper(root, "", huffmanCodes);
        return huffmanCodes;
    }

    private static void buildHuffmanCodesHelper(HuffmanNode node, String code, Map<Character, String> huffmanCodes) {
        if (node == null) {
            return;
        }

        if (node.data != '\0') {
            huffmanCodes.put(node.data, code);
        }

        buildHuffmanCodesHelper(node.left, code + "0", huffmanCodes);
        buildHuffmanCodesHelper(node.right, code + "1", huffmanCodes);
    }

    private static byte[] compress(String input, Map<Character, String> huffmanCodes) {
        StringBuilder compressed = new StringBuilder();
        for (char c : input.toCharArray()) {
            compressed.append(huffmanCodes.get(c));
        }

        int length = compressed.length();
        byte[] result = new byte[(length + 7) / 8];

        for (int i = 0; i < length; i++) {
            if (compressed.charAt(i) == '1') {
                result[i / 8] |= (1 << (7 - i % 8));
            }
        }

        return result;
    }

}
