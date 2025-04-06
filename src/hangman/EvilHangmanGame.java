package hangman;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EvilHangmanGame implements IEvilHangmanGame {
    private StringBuilder constructedWord;
    private Set<String> dictionarySet;
    private SortedSet<Character> guessedLetters;

    public EvilHangmanGame() {
        constructedWord = new StringBuilder();
        dictionarySet = new HashSet<>();
        guessedLetters = new TreeSet<>();
    }

    @Override
    public void startGame(File dictionary, int wordLength) throws IOException, EmptyDictionaryException {
        ResetVariables();

        Scanner scanner = new Scanner(dictionary);
        if (!scanner.hasNext()) {
            throw new EmptyDictionaryException("Empty dictionary file used!");
        }

        String temp;

        while (scanner.hasNext()) {
            temp = scanner.next();
            if (temp.length() == wordLength) {
                dictionarySet.add(temp);
            }
        }

        if (dictionarySet.isEmpty()) {
            throw new EmptyDictionaryException("No words of that size found in dictionary!");
        }

        for (int i = 0; i < wordLength; i++) {
            constructedWord.append('_');
        }

        scanner.close();

    }

    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
        char lowerCaseLetter = Character.toLowerCase(guess);

        for (char usedLetter : guessedLetters) {
            if (lowerCaseLetter == usedLetter) {
                throw new GuessAlreadyMadeException("That letter has already been guessed!");
            }
        }

        guessedLetters.add(lowerCaseLetter);

        HashMap<String, Set<String>> partitionedSets = PartitionDictionary(lowerCaseLetter);

        Set<String> newDictionary = new HashSet<>();
        String newConstructedWord = "";

        Set<String> tempDictionary;
        String tempKey;
        int largestSize = 0;


        for (Map.Entry iterator : partitionedSets.entrySet()) {

            tempDictionary = (Set<String>)iterator.getValue();
            tempKey = (String)iterator.getKey();

            if (tempDictionary.size() > largestSize) {
                largestSize = tempDictionary.size();
                newDictionary = tempDictionary;
                newConstructedWord = tempKey;
            } else if (tempDictionary.size() == largestSize) {
                int charsAdded1 = 0;
                int charsAdded2 = 0;
                for (int i = 0;  i < tempKey.length(); i++) {
                    if (tempKey.charAt(i) == lowerCaseLetter) {
                        charsAdded1 += 1;
                    }

                    if (newConstructedWord.charAt(i) == lowerCaseLetter) {
                        charsAdded2 += 1;
                    }
                }

                if (charsAdded1 == 0) {
                    largestSize = tempDictionary.size();
                    newDictionary = tempDictionary;
                    newConstructedWord = tempKey;
                } else if (charsAdded1 == charsAdded2) {
                    if (FindRightmostLetter(tempKey, newConstructedWord, lowerCaseLetter) == 1) {
                        largestSize = tempDictionary.size();
                        newDictionary = tempDictionary;
                        newConstructedWord = tempKey;
                    }
                }
            }


        }

        constructedWord.delete(0, constructedWord.length());
        constructedWord.append(newConstructedWord);
        dictionarySet = newDictionary;
        return newDictionary;
    }

    public HashMap<String, Set<String>> PartitionDictionary (char guess) {

        HashMap<String, Set<String>> partitionedSets = new HashMap<>();

        for (String word : dictionarySet) {
            //make Map Key
            StringBuilder newPattern = new StringBuilder();
            for (int i = 0; i < word.length(); i++) {
                if (word.charAt(i) == guess) {
                    newPattern.append(guess);
                } else if (constructedWord.toString().charAt(i) != '_') {
                    newPattern.append(constructedWord.toString().charAt(i));
                } else {
                    newPattern.append('_');
                }
            }
            if (partitionedSets.containsKey(newPattern.toString())) {
                partitionedSets.get(newPattern.toString()).add(word);
            } else {
                Set<String> words = new HashSet<>();
                words.add(word);
                partitionedSets.put(newPattern.toString(), words);
            }
        }

        return partitionedSets;
    }

    public int FindRightmostLetter(String word1, String word2, char guess) {
        for (int i = word1.length()-1; i > 0; i--) {
            if (word1.charAt(i) != word2.charAt(i)) {
                if (word1.charAt(i) == guess) {
                    return 1;
                } else if (word2.charAt(i) == guess) {
                    return 0;
                }
            }
        }
        return 0;
    }

    @Override
    public SortedSet<Character> getGuessedLetters() {
        return guessedLetters;
    }

    public StringBuilder getConstructedWord() {
        return constructedWord;
    }

    public Set<String> getDictionarySet() {
        return dictionarySet;
    }

    public void ResetVariables() {
        constructedWord = new StringBuilder();
        dictionarySet = new HashSet<>();
        guessedLetters = new TreeSet<>();
    }
}
