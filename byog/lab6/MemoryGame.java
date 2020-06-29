package byog.lab6;

import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    private int width;
    private int height;
    private int round;
    private Random rand;
    private boolean gameOver;
    private boolean playerTurn;
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        int seed = Integer.parseInt(args[0]);
        MemoryGame game = new MemoryGame(seed, 40, 40);
        game.startGame();
    }

    public MemoryGame(int seed, int width, int height) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
        this.rand = new Random(seed);
    }

    public String generateRandomString(int n) {
        String result = "";
        for (int i = 0; i < n; i++) {
            int charInd = rand.nextInt(25);
            result += CHARACTERS[charInd];
        }
        return result;
    }

    public void drawFrame(String s) {
        StdDraw.clear();
        Font font = new Font("Arial", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.white);
        if (!gameOver) {
            StdDraw.textLeft(0, height - 1, "Round: " + round);
            if (playerTurn) {
                StdDraw.text(width/2, height - 1, "Type!");
            } else {
                StdDraw.text(width/2, height - 1, "Watch!");
            }
            StdDraw.line(0, height - 2, width, height - 2);
            int encInd = rand.nextInt(ENCOURAGEMENT.length - 1);
            StdDraw.textRight(width - 1, height - 1, ENCOURAGEMENT[encInd]);
            StdDraw.text(width/2, height/2, s);
            StdDraw.show();
        }
    }

    public void flashSequence(String letters) {
        for (char let: letters.toCharArray()) {
            drawFrame(Character.toString(let));
            StdDraw.pause(500);
        }
    }

    public String solicitNCharsInput(int n) {
        String result = "";
        for (int i = 0; i < n; i++) {
            if (StdDraw.hasNextKeyTyped()) {
                result += StdDraw.nextKeyTyped();
            }
        }
        return result;
    }

    public void startGame() {
        gameOver = false;
        playerTurn = false;
        round = 1;
        while (!gameOver) {
            drawFrame("Round: " + round);
            String randStr = generateRandomString(round);
            flashSequence(randStr);
            playerTurn = true;
            String playerInput = solicitNCharsInput(round);
            if (!randStr.equals(playerInput)) {
                gameOver = true;
                drawFrame("Game Over! Final level: " + round);
            } else {
                playerTurn = false;
                round += 1;
            }
        }
    }

}
