import java.awt.Color;
import java.util.Random;
import java.util.Scanner;
import java.io.*;
import java.util.*;
import java.util.Arrays;
import java.lang.Character;


public class WordleLogic {

    //Toggle DEBUG MODE On/Off
    public static final boolean DEBUG_MODE = true;
    //Toggle WARM_UP On/Off
    public static final boolean WARM_UP = false;


    private static final String FILENAME = "englishWords5.txt";
    //Number of words in the words.txt file
    private static final int WORDS_IN_FILE = 5758; // Review BJP 6.1 for  

    //Use for generating random numbers!
    private static final Random rand = new Random();


    public static final int MAX_ATTEMPTS = 6; //max number of attempts
    public static final int WORD_LENGTH = 5; //WORD_LENTGH-letter word 
    // as is 5 like wordle, could be changed

    private static final char EMPTY_CHAR = WordleView.EMPTY_CHAR; //use to delete char


    //************       Color Values       ************

    //Green (right letter in the right place)
    private static final Color CORRECT_COLOR = new Color(53, 209, 42);
    //Yellow (right letter in the wrong place)
    private static final Color WRONG_PLACE_COLOR = new Color(235, 216, 52);
    //Dark Gray (letter doesn't exist in the word)
    private static final Color WRONG_COLOR = Color.DARK_GRAY;
    //Light Gray (default keyboard key color, letter hasn't been checked yet)
    private static final Color NOT_CHECKED_COLOR = new Color(160, 163, 168);

    private static final Color DEFAULT_BGCOLOR = Color.BLACK;

    //***************************************************

    //************      Class variables     ************

    //Add them as necessary (I have some but less than 5)
    public static int cellRow = 0;
    public static int cellCol = 0;
    private static String secretWord = ""; //stores secretWord
    public static StringBuffer inputWord = new StringBuffer(); //stores current entred word
    public static String[] wordsArr = new String[WORDS_IN_FILE];


    //***************************************************



    //************      Class methods     ************

    // There are 6 already defined below, with 5 of them to be completed.
    // Add class helper methods as necessary. Our solution has 12 of them total.


    // Complete for 3.1.1
    public static void warmUp() {

        WordleView.setCellLetter(0, 0, 'C');
        WordleView.setCellColor(0, 0, CORRECT_COLOR);
        WordleView.getKeyboardColor('C');
        WordleView.setKeyboardColor('C', CORRECT_COLOR);

        WordleView.setCellLetter(1, 2, 'O');
        WordleView.setCellColor(1, 2, WRONG_COLOR);
        WordleView.setCellLetter(3, 3, 'S');
        WordleView.getKeyboardColor('S');
        WordleView.setKeyboardColor('S', WRONG_COLOR);
        WordleView.setCellLetter(5, 4, 'C');
        WordleView.setCellColor(5, 4, WRONG_PLACE_COLOR);
    }

    //This function gets called ONCE when the game is very first launched
    //before the player has the opportunity to do anything.
    //
    //Returns the chosen mystery word the user needs to guess
    public static String init() throws FileNotFoundException {
        Random random = new Random();
        int randNum = random.nextInt(WORDS_IN_FILE - 1); //random to pick from 0 to 5757
        String[] wordsArrayInit = wordsArray(wordsArr);
        return wordsArrayInit[randNum];
    }
    //function creates an array using words in the englishWords5.txt file
    // function returns the array of english words
    public static String[] wordsArray(String[] array) throws FileNotFoundException {
        File file = new File("englishWords5.txt");
        Scanner scan = new Scanner(file);
        String wordInFile = "";
        //System.out.println(scan.nextLine());
        for (int i = 0; i < WORDS_IN_FILE; i++) {
            wordInFile = scan.nextLine(); //assigns wordInFile to the scan object 
            array[i] = wordInFile.toLowerCase();
        }
        return array;
    }
    //This function gets called everytime the user inputs 'Backspace'
    //pressing the physical or virtual keyboard.
    // call on Backspace input
    public static void deleteLetter() {
        if (DEBUG_MODE) {
            System.out.println("in deleteLetter()");
        }
        WordleView.setCellLetter(cellRow, cellCol - 1, EMPTY_CHAR);
        System.out.println("cell col" + cellCol);
        cellCol--;
        inputWord = inputWord.delete(cellCol,inputWord.length());
        System.out.println("cell col" + cellCol + " " + inputWord);
    }
    //This function gets called everytime the player inputs 'Enter'
    //pressing the physical or virtual keyboard.  
    public static void checkLetters() {

        String secretWordCopy = secretWord.toLowerCase();
        String inputWordCopy = inputWord.toString();
        int[] greenKeyArr = new int[inputWord.length()];

        if (DEBUG_MODE) {
            System.out.println("in checkLetters()" + " " + secretWord);
        }
        if ((wordLength() < 5) || (englishWord() == false)) {
            cellCol = 0;
            if (cellRow == (MAX_ATTEMPTS - 1)) {
                WordleView.gameOver(false);
            }
            cellRow++;
            inputWord.delete(0,inputWord.length());
            return;
        }
        for (int key = 0; key < inputWord.length(); key++) {
            char currentChar = inputWord.charAt(key);
            if (currentChar == secretWordCopy.toLowerCase().charAt(key)) {
                WordleView.setCellColor(cellRow, key, CORRECT_COLOR);
                WordleView.setKeyboardColor(currentChar, CORRECT_COLOR);
                greenKeyArr[key] = key + 10;
            }
        }
        secretWordCopy = removeCharacter(secretWordCopy, greenKeyArr);
        //inputWordCopy = removeCharacter(inputWordCopy, greenKeyArr);
        colorCheck(greenKeyArr, secretWordCopy);
        if ((inputWord.toString().equals(secretWord))) {
            System.out.println("Before gameOver");
            WordleView.gameOver(true);
        } else if (cellRow == (MAX_ATTEMPTS - 1)) {
            WordleView.gameOver(false);
        } else {
            cellRow++;
            cellCol = 0;
            inputWord.delete(0,inputWord.length());
        }
    }
    public static void colorCheck(int[] array, String secret) {

        for (int index = 0; index < array.length; index++) {
            int key = 0;
            int[] secretKey = new int[secret.length()];
            if ((array[index] == 0) && (secret.contains(inputWord.substring(index, index + 1)))) {
                String currentString = inputWord.substring(index, index + 1);
                key = secret.indexOf(currentString);
                secretKey[key] = key + 10;
                secret = removeCharacter(secret, secretKey);
                WordleView.setCellColor(cellRow, index, WRONG_PLACE_COLOR);
                if (!(WordleView.getKeyboardColor(inputWord.charAt(index)).equals(CORRECT_COLOR))) {
                    WordleView.setKeyboardColor(inputWord.charAt(index), WRONG_PLACE_COLOR);
                }
            } else {
                if ((array[index] == 0)) {
                    WordleView.setCellColor(cellRow, index, WRONG_COLOR);

                    if (!((WordleView.getKeyboardColor(inputWord.charAt(index)).equals(CORRECT_COLOR)) || (WordleView.getKeyboardColor(inputWord.charAt(index)).equals(WRONG_PLACE_COLOR)))) {
                        WordleView.setKeyboardColor(inputWord.charAt(index), WRONG_COLOR);
                    }
                }
            }
        }
    }
    public static String removeCharacter(String word, int[] array) {
        StringBuffer newWord = new StringBuffer();
        for (int index = 0; index < array.length; index++) {
            if (array[index] == 0) {
                newWord.append(word.charAt(index));
            }
        }
        return newWord.toString();
    }
    //This function gets called everytime the player types a valid letter
    //on the keyboard or clicks one of the letter keys on the 
    //graphical keyboard interface.  
    //The key pressed is passed in as a char uppercase for letter
    public static void inputLetter(char key) {
        WordleView.setCellLetter(cellRow, cellCol, key);
        if (cellCol < 5) {
            cellCol++;
            inputWord.append(Character.toLowerCase(key));
        }
        if (WARM_UP) {
            System.out.println("A row should wiggle");
        }
    }
    //function checks if the inputword is in the english words in the txt file
    //function returns a boolean and wiggles when the inputWord is not in the englishWords5txt file
    public static boolean englishWord() {

        for (int index = 0; index < wordsArr.length; index++) {
            if (inputWord.toString().equals(wordsArr[index].toLowerCase())) {
                return true;
            }
        }
        WordleView.wiggleRow(cellRow);
        return false;
    }
    // functions checks if the inputword Length is less than 5 and wiggles when length<5
    public static int wordLength() {
        int wordLeng = inputWord.length();
        if (wordLeng < 5) {
            WordleView.wiggleRow(cellRow);
        }
        return wordLeng;
    }


    //Initializes and launches the game logic and its GUI window
    public static void main(String[] args) throws FileNotFoundException {
        if (!WARM_UP) {
            WordleLogic.init();
            secretWord = WordleLogic.init();
           // secretWord = "banal";  //TEST for Duplicate
        }
        WordleView.create(secretWord);
        if (WARM_UP) {
            warmUp();
        }
        System.out.println(secretWord + " after init");

    }

}