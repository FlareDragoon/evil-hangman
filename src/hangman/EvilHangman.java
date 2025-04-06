package hangman;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.Set;

public class EvilHangman {

    public static void main(String[] args) {
        try {
            File fileName = new File(args[0]);
            int wordLength = Integer.parseInt(args[1]);
            int incorrectGuessesRemaining = Integer.parseInt(args[2]);

            EvilHangmanGame game = new EvilHangmanGame();
            game.startGame(fileName, wordLength);

            while (incorrectGuessesRemaining > 0) {

                String oldWordGuessed = game.getConstructedWord().toString();

                System.out.printf("You have %d guesses remaining \n", incorrectGuessesRemaining);
                System.out.printf("Used letters: %s \n", game.getGuessedLetters().toString());
                System.out.printf("Word: %s \n", game.getConstructedWord().toString());
                System.out.printf("Enter guess: ");

                Scanner scanner = new Scanner(System.in);
                char guess = '!';
                while (!Character.isAlphabetic(guess)) {
                    String scannedInput = scanner.nextLine();
                    while(scannedInput.length() <= 0) {
                        scannedInput = scanner.nextLine();
                    }

                    guess = scannedInput.charAt(0);
                }


                try {
                    Set<String> newDictionary = game.makeGuess(guess);

                    int unknownLettersRemaining = 0;
                    int newLettersFound = 0;
                    for (int i = 0; i < game.getConstructedWord().length(); i++) {
                        if (game.getConstructedWord().toString().charAt(i) == '_') {
                            unknownLettersRemaining += 1;
                        } else if (game.getConstructedWord().toString().charAt(i) == Character.toLowerCase(guess)) {
                            newLettersFound += 1;
                        }
                    }
                    if (newLettersFound == 0) {
                        System.out.printf("Sorry, there are no %c \n", guess);
                        incorrectGuessesRemaining -= 1;
                    } else if (unknownLettersRemaining == 0) {
                        System.out.printf("You win! The word was %s \n", game.getConstructedWord().toString());
                        break;
                    } else if (newLettersFound > 0) {
                        System.out.printf("There are %d %c \n", newLettersFound, guess);
                    }
                    if (incorrectGuessesRemaining == 0) {
                        System.out.printf("You lose! The word was %s", newDictionary.toArray()[0]);
                    }

                    System.out.printf("\n");

                } catch (GuessAlreadyMadeException e) {
                    System.out.printf("Guess again. \n");
                }
            }

        } catch (EmptyDictionaryException e) {
            System.out.printf("Check your dictionary file and word length parameters \n");
        } catch (IOException e) {
            System.out.printf("IO Exception - Check your input files and parameters \n");
        }

    }

}
