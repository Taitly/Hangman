package com.taitly;

import java.io.*;
import java.util.*;

public class Hangman {
    private static final String dictionary = "dictionary.txt";
    private static final Random random = new Random();

    private static int PLAYER_FAILS = 0;
    private static final int MAX_FAILS = 6;
    private static boolean GAME_FINISHED = false;

    private static final int WORD_MINIMAL_LENGTH = 5;
    private static final char MASK = '*';

    private static final String AGREE = "да";
    private static final String DISAGREE = "нет";

    private static final String RESTART_GAME_MESSAGE = "Хотите сыграть еще?";
    private static final String END_GAME_MESSAGE = "Спасибо за игру!";
    private static final String WIN_GAME_MESSAGE = "Верно! Вы угадали слово:";
    private static final String LOSE_GAME_MESSAGE = "Вы проиграли!\nБыло загадано слово - ";
    private static final String ERROR_INPUT_MESSAGE = "Некорректный ввод!";

    private static final String LETTER_IS_GUESSED = "\nБуква угадана верно!";
    private static final String LETTER_IS_WRONG = "\nВ слове нет данной буквы!";
    private static final String LETTER_WAS_USED = "\nВведенная буква уже была использована!";

    private static final String START_GAME_PROMPT = "Введите \"%s\" - чтобы начать игру, \"%s\" - чтобы завершить.\n";

    private static final String WELCOME_MESSAGE =
                    """
                    ************************************************
                      *      Добро пожаловать в игру "Виселица"      *
                      ************************************************
                    """;


    public static void main(String[] args) {
        System.out.println(WELCOME_MESSAGE);
        while (true) {
            startGame();
        }
    }

    private static void startGame() {
        System.out.printf(START_GAME_PROMPT, AGREE, DISAGREE);
        Scanner inputScanner = new Scanner(System.in);
        String userInput = inputScanner.next().toLowerCase();

        if (!isUserInputValid(userInput)) {
            System.out.println(ERROR_INPUT_MESSAGE);
        } else {
            switch (userInput) {
                case AGREE -> gameLoopProcess();
                case DISAGREE -> {
                    System.out.println(END_GAME_MESSAGE);
                    System.exit(0);
                }
                default -> startGame();
            }
        }
    }

    private static void gameLoopProcess() {
        Scanner letterScanner = new Scanner(System.in);
        Set<String> usedLetters = new HashSet<>();

        String word = getRandomWord();
        char[] maskedWord = maskWord(word);

        String letter;

        System.out.printf("\nОтгадайте загаданное слово: %s", String.valueOf(maskedWord));
        System.out.printf("\nПри достижении %d ошибок, вы проиграете. Желаем удачи!\n", MAX_FAILS);
        HangmanPicture.drawHangman(PLAYER_FAILS);

        while (!GAME_FINISHED) {
            System.out.print("Введите букву:");
            letter = getValidLetter(letterScanner);

            if (!isLetterGuessed(word, letter, usedLetters)) {
                PLAYER_FAILS++;
            }

            System.out.print("Загаданное слово: ");
            openLetterInMask(letter, word, maskedWord);

            System.out.printf("\nСписок использованных буквы: %s.\nКоличество сделанных ошибок: %d\n", usedLetters, PLAYER_FAILS);

            HangmanPicture.drawHangman(PLAYER_FAILS);

            if (isGameFinished(maskedWord)) {
                if (isWordGuessed(maskedWord)) {
                    System.out.println(WIN_GAME_MESSAGE + word.toUpperCase());
                } else {
                    System.out.println(LOSE_GAME_MESSAGE + word.toUpperCase());
                }
                GAME_FINISHED = true;
                System.out.println(RESTART_GAME_MESSAGE);
            }
        }
        GAME_FINISHED = false;
        PLAYER_FAILS = 0;
    }

    private static boolean isUserInputValid(String userInput) {
        return AGREE.equals(userInput) || DISAGREE.equals(userInput);
    }

    private static String getRandomWord() {
        InputStream inputStream = Hangman.class.getClassLoader().getResourceAsStream(dictionary);
        if (inputStream == null) {
            throw new RuntimeException("File with dictionary not found.");
        }

        List<String> words = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.length() >= WORD_MINIMAL_LENGTH) {
                    words.add(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading from dictionary.");
        }

        if (words.isEmpty()) {
            throw new NullPointerException("Dictionary is empty!");
        }
        return words.get(random.nextInt(words.size()));
    }

    private static char[] maskWord(String word) {
        if (word == null) {
            throw new IllegalArgumentException("Input parameters cannot be null");
        }
        char[] maskedWord = new char[word.length()];
        for (int i = 0; i < word.length(); i++) {
            maskedWord[i] = MASK;
        }
        return maskedWord;
    }

    private static String getValidLetter(Scanner scanner) {
        if (scanner == null) {
            throw new IllegalArgumentException("Scanner cannot be null");
        }

        String letter;
        String regex = "^[а-яёА-ЯЁ]$";
        while (true) {
            letter = scanner.nextLine();
            if (letter.matches(regex)) {
                return letter;
            }
            System.out.printf("%s\nПожалуйста укажите одну букву:\n", ERROR_INPUT_MESSAGE);
        }
    }

    private static boolean isLetterGuessed(String word, String letter, Set<String> letters) {
        if (letter == null || word == null || letters == null) {
            throw new IllegalArgumentException("Input parameters cannot be null");
        }
        String lowerCaseLetter = letter.toLowerCase();
        if (letters.contains(lowerCaseLetter)) {
            System.out.println(LETTER_WAS_USED);
            return true;
        }
        if (word.contains(lowerCaseLetter)) {
            System.out.println(LETTER_IS_GUESSED);
            letters.add(lowerCaseLetter);
            return true;
        }
        System.out.println(LETTER_IS_WRONG);
        letters.add(lowerCaseLetter);
        return false;
    }

    private static void openLetterInMask(String letter, String word, char[] maskedWord) {
        if (letter == null || word == null || maskedWord == null) {
            throw new IllegalArgumentException("Input parameters cannot be null");
        }
        if (maskedWord.length != word.length()) {
            throw new IllegalArgumentException("maskedWord must have the same length as word");
        }

        for (int i = 0; i < word.length(); i++) {
            if (letter.charAt(0) == (word.charAt(i))) {
                maskedWord[i] = word.charAt(i);
            }
        }
        for (char c : maskedWord) {
            System.out.print(c);
        }
    }

    private static boolean isWordGuessed(char[] mask) {
        if (mask == null) {
            throw new IllegalArgumentException("Input parameters cannot be null");
        }
        for (char c : mask) {
            if (c == MASK) {
                return false;
            }
        }
        return true;
    }

    private static boolean isGameFinished(char[] maskedWord) {
        return (isWordGuessed(maskedWord)) || (PLAYER_FAILS == MAX_FAILS);
    }

    private static class HangmanPicture {
        private static final String[][] PICTURES = {
                {
                        " ┌─ ─ ─ ─ ┐",
                        " |",
                        " |",
                        " |",
                        " |",
                        "─┴───"
                },
                {
                        " ┌─ ─ ─ ─ ┐",
                        " |        ◯",
                        " |",
                        " |",
                        " |",
                        "─┴───"
                },
                {
                        " ┌─ ─ ─ ─ ┐",
                        " |        ◯",
                        " |        │",
                        " |",
                        " |",
                        "─┴───"
                },
                {
                        " ┌─ ─ ─ ─ ┐",
                        " |        ◯",
                        " |       /│",
                        " |",
                        " |",
                        "─┴───"
                },
                {
                        " ┌─ ─ ─ ─ ┐",
                        " |        ◯",
                        " |       /│\\",
                        " |",
                        " |",
                        "─┴───"
                },
                {
                        " ┌─ ─ ─ ─ ┐",
                        " |        ◯",
                        " |       /│\\",
                        " |       ╱",
                        " |",
                        "─┴───"
                },
                {
                        " ┌─ ─ ─ ─ ┐",
                        " |        ◯",
                        " |       /│\\",
                        " |       ╱ \\",
                        " |",
                        "─┴───"
                },
        };

        static void drawHangman(int failsCount) {
            String[] picture = PICTURES[failsCount];
            for (String line : picture) {
                System.out.println(line);
            }
        }
    }
}