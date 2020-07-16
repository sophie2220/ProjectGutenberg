import java.io.*; 
import java.util.*;

public class ProjectGutenberg {
   private static final String FILE_PATH = "book.txt";

    public static int getTotalNumberOfWords(BufferedReader reader) throws IOException {
        String line;
        int countWord = 0;
        while ((line = reader.readLine()) != null) {
            String[] wordList = line.split("\\s+");
            for (String word : wordList)
                if (word.length() > 0)
                    countWord++;
        }
        return countWord;
    }

    public static int getTotalUniqueWords(BufferedReader reader) throws IOException {
        String line;
        HashSet<String> set = new HashSet<>();
        while ((line = reader.readLine()) != null) {
            String[] wordList = line.split("\\s+");
            for (String word : wordList) {
                word = word.replace(".", "").replace("!", "").replace(";", "").replace(":", "").replace(",", "")
                .replace("\"", "").replace("?", "").replace("(", "").replace(")", "").replace("*", "").toLowerCase();
                if (word.length() > 0) 
                    set.add(word);
            }
        }
        reader.close();
        return set.size();
    }

    public static List<List<Object>> get20MostFrequentWords(BufferedReader reader) throws IOException {
        HashMap<String, Integer> map = makeHashMap(reader, null);
        reader.close();
        List<Map.Entry<String, Integer>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });
        return putWordsInList(map, list);
    }

    public static List<List<Object>> get20MostInterestingFrequentWords(BufferedReader reader) throws IOException {
        BufferedReader hundredReader = makeBufferedReader("hundredFrequent.txt");
        HashSet<String> set = new HashSet<>();
        String hundredWord;
        while ((hundredWord = hundredReader.readLine()) != null)
            set.add(hundredWord);
        hundredReader.close();
        HashMap<String, Integer> map = makeHashMap(reader, set);
        reader.close();
        List<Map.Entry<String, Integer>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });
        return putWordsInList(map, list);
    }

    public static List<List<Object>> get20LeastFrequentWords(BufferedReader reader) throws IOException {
        HashMap<String, Integer> map = makeHashMap(reader, null);
        List<Map.Entry<String, Integer>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });
        return putWordsInList(map, list);
    }

    public static List<Integer> getFrequencyOfWord(BufferedReader reader, String targetWord) throws IOException {
        List<Integer> result = new ArrayList<>();
        int wordCount = 0;
        String line;
        while ((line = reader.readLine()) != null) {
            String[] wordList = line.split("\\s+");
            for (String currWord: wordList) {
                if (currWord.contains("*")) { //'*' marks the start of a new chapter
                    result.add(wordCount);
                    wordCount = 0;
                }
                currWord = currWord.replace(".", "").replace("!", "").replace(";", "").replace(":", "").replace(",", "")
                        .replace("\"", "").replace("?", "").replace("(", "").replace(")", "").replace("*", "").toLowerCase();
                if (currWord.equals(targetWord))
                    wordCount++;
            }
        }
        reader.close();
        result.add(wordCount);
        return result;
    }

    public static int getChapterQuoteAppears(BufferedReader reader, String quote) throws IOException {
        int currChapter = 1, index = 0; 
        String line;
        String[] quoteArr = quote.split("\\s+");
        while ((line = reader.readLine()) != null) {
            String[] wordList = line.split("\\s+");
            for (String currWord: wordList) {
                if (currWord.contains("*")) 
                    currChapter++;
                if (currWord.equals(quoteArr[index])) {
                    index++;
                    if (index == quoteArr.length - 1)
                        return currChapter;
                } else 
                    index = 0;
            }
        }
        reader.close();
        return -1;
    }

    public static String generateSentence(BufferedReader reader, StringBuilder returnString, String targetWord, int iter) throws IOException {
        if (iter == 0)
            return returnString.toString();
        HashSet<String> wordChoices = new HashSet<>();
        String line;
        boolean foundTarget = false;
        while ((line = reader.readLine()) != null) {
            String[] wordList = line.split("\\s+");
            for (String currWord: wordList) {
                if (foundTarget) {
                    wordChoices.add(currWord);
                    foundTarget = false;
                }
                if (currWord.equals(targetWord))
                    foundTarget = true;
            }
        }
        reader.close();
        String randWord = getRandomWord(wordChoices);
        iter--;
        return generateSentence(makeBufferedReader(FILE_PATH), returnString.append(" " + randWord), randWord, iter);
    }

    private static String getRandomWord(HashSet<String> set) {
        int index = new Random().nextInt(set.size());
        Iterator<String> iter = set.iterator();
        for (int i = 0; i < index; i++) 
            iter.next();
        return iter.next();
    }

    private static List<List<Object>> putWordsInList(Map<String, Integer> map, List<Map.Entry<String, Integer>> list) {
        HashMap<String, Integer> newMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list) 
            newMap.put(entry.getKey(), entry.getValue());
        List<List<Object>> result = new ArrayList<>();
        int i = 0;
        for (Map.Entry<String, Integer> entry : newMap.entrySet()) {
            if (i >= 20)
                break;
            List<Object> temp = new ArrayList<>();
            temp.add(entry.getKey());
            temp.add(entry.getValue());
            result.add(temp);
            i++;
        }
        return result;
    }

    private static HashMap<String, Integer> makeHashMap(BufferedReader reader, Set<String> set) throws IOException {
            String line;
            HashMap<String, Integer> map = new HashMap<>();
            while ((line = reader.readLine()) != null) {
                String[] wordList = line.split("\\s+");
                for (String word : wordList) {
                    word = word.replace(".", "").replace("!", "").replace(";", "").replace(":", "").replace(",", "")
                    .replace("\"", "").replace("?", "").replace("(", "").replace(")", "").replace("*", "").toLowerCase();
                    if (word.length() > 0 && (set == null || !set.contains(word))) {
                        if (word.length() > 0)
                            map.put(word, map.getOrDefault(word, 0) + 1);
                    }
                }
            }
            return map;
    }

    public static BufferedReader makeBufferedReader(String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        FileInputStream fileStream = new FileInputStream(file);
        InputStreamReader input = new InputStreamReader(fileStream);
        BufferedReader reader = new BufferedReader(input);
        return reader;
    }
    
    public static void main(String args[]) throws IOException {
        System.out.println("Number of words in the novel: " + getTotalNumberOfWords(makeBufferedReader(FILE_PATH)));
        System.out.println("Number of unique words in the novel: " + getTotalUniqueWords(makeBufferedReader(FILE_PATH)));
        System.out.println("20 most frequent words in the novel: ");
        List<List<Object>> words = get20MostFrequentWords(makeBufferedReader(FILE_PATH));
        for (List<Object> lst: words) 
            System.out.println(lst.get(0) + ": " + lst.get(1));
        System.out.println("20 most interesting frequent words in the novel: ");
        words = get20MostInterestingFrequentWords(makeBufferedReader(FILE_PATH));
        for (List<Object> lst : words)
            System.out.println(lst.get(0) + ": " + lst.get(1));
        System.out.println("20 least frequent words in the novel: ");
        words = get20LeastFrequentWords(makeBufferedReader(FILE_PATH));
        for (List<Object> lst : words)
            System.out.println(lst.get(0) + ": " + lst.get(1));
        System.out.println("Number of occurrences of 'love' in each chapter: ");
        List<Integer> wordsPerChapter = getFrequencyOfWord(makeBufferedReader(FILE_PATH), "love");
        System.out.println(wordsPerChapter);
        String quote = "Searching means: having a goal.  But finding means: being free, being open, having no goal.  You, oh venerable one, are perhaps indeed a searcher, because, striving for your goal, there are many things you don't see, which are directly in front of your eyes.";
        System.out.println("Chapter in which '" + quote + "' appears: " + 
        getChapterQuoteAppears(makeBufferedReader(FILE_PATH), quote));
        String randomSentence = generateSentence(makeBufferedReader(FILE_PATH), new StringBuilder("The"), "The", 19);
        System.out.println("Randomly generated sentence: ");
        System.out.println(randomSentence);
    }
}