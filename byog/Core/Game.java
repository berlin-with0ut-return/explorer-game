package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

public class Game {
    TERenderer ter = new TERenderer();
    GameState gameState;
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;

    /**
     * Method used for playing a fresh game. The game should start from the main menu.
     */
    public void playWithKeyboard() {
        ter.initialize(WIDTH, HEIGHT);
        boolean gameOver = false;
        drawIntro();
        gameState = new GameState(null, null, null);
        while (!gameOver) {
            if (StdDraw.hasNextKeyTyped()) {
                char nxtKey = StdDraw.nextKeyTyped();
                if (nxtKey == 'n') {
                    drawTutorial();
                    long seed = getSeed();
                    Random newRand = new Random(seed);
                    TETile[][] newWorld = startWithSeed(seed);
                    gameState = new GameState(newRand, newWorld, null);
                    placePlayerStart(gameState.world);
                    ter.renderFrame(gameState.world);
                } else if (nxtKey == 'l') {
                    loadGame();
                }
            }
            int mouseXInt = (int) StdDraw.mouseX();
            int mouseYInt = (int) StdDraw.mouseY();
            if (gameState.world != null && mouseXInt < WIDTH && mouseYInt < HEIGHT) {
                StdDraw.clear(Color.BLACK);
                ter.renderFrame(gameState.world);
                String tileDes = gameState.world[mouseXInt][mouseYInt].description();
                StdDraw.setPenColor(Color.WHITE);
                StdDraw.textLeft(1, HEIGHT - 1, tileDes);
                StdDraw.show();
            }
            StdDraw.pause(500);
            if (gameState.world != null && StdDraw.hasNextKeyTyped()) {
                switch (StdDraw.nextKeyTyped()) {
                    case 'w':
                        Position nextPosW = new Position(gameState.playerLoc.xx,
                                gameState.playerLoc.yy + 1);
                        placePlayer(gameState.world, nextPosW);
                        ter.renderFrame(gameState.world);
                        break;
                    case 'a':
                        Position nextPosA = new Position(gameState.playerLoc.xx - 1,
                                gameState.playerLoc.yy);
                        placePlayer(gameState.world, nextPosA);
                        ter.renderFrame(gameState.world);
                        break;
                    case 's':
                        Position nextPosS = new Position(gameState.playerLoc.xx,
                                gameState.playerLoc.yy - 1);
                        placePlayer(gameState.world, nextPosS);
                        ter.renderFrame(gameState.world);
                        break;
                    case 'd':
                        Position nextPosD = new Position(gameState.playerLoc.xx + 1,
                                gameState.playerLoc.yy);
                        placePlayer(gameState.world, nextPosD);
                        ter.renderFrame(gameState.world);
                        break;
                    case 'q':
                        gameOver = true;
                        saveGame(gameState);
                        drawQuitScreen();
                        break;
                    default:
                        break;
                }
                triggerNPC();
            }
        }
    }

    public static void saveGame(GameState gs) {
        try {
            FileOutputStream fileOut =
                    new FileOutputStream("./byog/Core/gameState.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(gs);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public void loadGame() {
        try {
            FileInputStream fileIn = new FileInputStream("./byog/Core/gameState.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            gameState = (GameState) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
            return;
        } catch (ClassNotFoundException c) {
            System.out.println("class not found");
            c.printStackTrace();
            return;
        }
        placePlayer(gameState.world, gameState.playerLoc);
        ter.renderFrame(gameState.world);
    }

    public void placePlayerStart(TETile[][] world) {
        int playerX = gameState.rand.nextInt(WIDTH - 2);
        int playerY = gameState.rand.nextInt(HEIGHT - 2);
        TETile possPlayer = world[playerX][playerY];
        if ((possPlayer != Tileset.NOTHING) && (possPlayer != Tileset.WALL)) {
            world[playerX][playerY] = Tileset.PLAYER;
            gameState.playerLoc = new Position(playerX, playerY);
        } else {
            placePlayerStart(world);
        }
    }

    public void placePlayer(TETile[][] world, Position p) {
        if (p.xx >= 0 && p.yy >= 0 && p.xx < WIDTH && p.yy < HEIGHT) {
            TETile nextPos = world[p.xx][p.yy];
            if (nextPos != Tileset.NOTHING && nextPos != Tileset.WALL & nextPos != Tileset.PORTAL) {
                world[gameState.playerLoc.xx][gameState.playerLoc.yy] = nextPos;
                world[p.xx][p.yy] = Tileset.PLAYER;
                gameState.playerLoc = new Position(p.xx, p.yy);
            } else if (nextPos == Tileset.PORTAL) {
                teleport();
            }
        }
    }

    public void teleport() {
        int randX = gameState.rand.nextInt(WIDTH - 2);
        int randY = gameState.rand.nextInt(HEIGHT - 2);
        TETile randTile = gameState.world[randX][randY];
        while (randTile == Tileset.NOTHING
                || randTile == Tileset.WALL
                 || randTile == Tileset.PORTAL) {
            randX = gameState.rand.nextInt(WIDTH - 2);
            randY = gameState.rand.nextInt(HEIGHT - 2);
            randTile = gameState.world[randX][randY];
        }
        placePlayer(gameState.world, new Position(randX, randY));
    }

    public static void drawIntro() {
        StdDraw.clear();
        StdDraw.clear(Color.black);
        StdDraw.setPenColor(Color.white);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "NEW TERRA");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 5, "New Game (N)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 6, "Load (L)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 7, "Quit (Q)");
        StdDraw.show();
    }

    public static long getSeed() {
        StdDraw.clear();
        StdDraw.clear(Color.black);
        StdDraw.setPenColor(Color.white);
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 5, "Enter seed and press 's' to start");
        StdDraw.show();
        char nxtKey = '@';
        String seedString = "";
        while (nxtKey != 's') {
            if (StdDraw.hasNextKeyTyped()) {
                StdDraw.clear();
                StdDraw.clear(Color.black);
                StdDraw.setPenColor(Color.white);
                StdDraw.text(WIDTH / 2, HEIGHT / 2 + 5, "Enter seed and press 's' to start");
                seedString += nxtKey;
                nxtKey = StdDraw.nextKeyTyped();
                StdDraw.text(WIDTH / 2, HEIGHT / 2, seedString);
                StdDraw.show();
            }
        }
        return Long.parseLong(seedString.substring(1));
    }

    /**
     * Method used for autograding and testing the game code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The game should
     * behave exactly as if the user typed these characters into the game after playing
     * playWithKeyboard. If the string ends in ":q", the same world should be returned as if the
     * string did not end with q. For example "n123sss" and "n123sss:q" should return the same
     * world. However, the behavior is slightly different. After playing with "n123sss:q", the game
     * should save, and thus if we then called playWithInputString with the string "l", we'd expect
     * to get the exact same world back again, since this corresponds to loading the saved game.
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] startWithSeed(long seed) {
        Random randomGen = new Random(seed);

        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                finalWorldFrame[x][y] = Tileset.NOTHING;
            }
        }

        int currXX = randomGen.nextInt(10);
        int currYY = randomGen.nextInt(HEIGHT);
        Position currPos = new Position(currXX, currYY);

        while (currPos.xx < WIDTH && currPos.yy < HEIGHT) {
            int roomH = randomGen.nextInt(10) + 1;
            int roomW = randomGen.nextInt(10) + 1;
            int nextX = currPos.xx + roomW;
            int nextY = randomGen.nextInt(HEIGHT);

            if (currPos.xx + roomW < WIDTH - 1 && currPos.yy + roomH < HEIGHT - 1) {
                drawRoom(randomGen, finalWorldFrame, roomW, roomH, currPos);
                int yOffset = randomGen.nextInt(roomH);
                currPos.yy += yOffset;
            }

            drawHallway(finalWorldFrame, randomGen, currPos, new Position(nextX, nextY));

            currPos.xx = nextX;
            currPos.yy = nextY;
        }

        drawPortals(finalWorldFrame, randomGen);
        drawWalls(finalWorldFrame);
        return finalWorldFrame;
    }

    public TETile[][] playWithInputString(String input) {
        input = input.toLowerCase();
        if (input.charAt(0) == 'n') {
            long seedInput = Long.parseLong(input.substring(1, input.indexOf('s')));
            String playInput = input.substring(input.indexOf('s') + 1);
            TETile[][] worldFrame = startWithSeed(seedInput);
            gameState = new GameState(new Random(seedInput), worldFrame, null);
            placePlayerStart(worldFrame);
            parseInput(playInput);
        } else if (input.charAt(0) == 'l') {
            loadGame();
            String playInput = input.substring(input.indexOf('l') + 1);
            parseInput(playInput);
        }
        return gameState.world;
    }

    public void parseInput(String movements) {
        int up = 0;
        int right = 0;
        for (char key : movements.toCharArray()) {
            switch (key) {
                case 's':
                    up -= 1;
                    break;
                case 'w':
                    up += 1;
                    break;
                case 'd':
                    right += 1;
                    break;
                case 'a':
                    right -= 1;
                    break;
                case 'q':
                    int endXX = gameState.playerLoc.xx + right;
                    int endYY = gameState.playerLoc.yy + up;
                    placePlayer(gameState.world, new Position(endXX, endYY));
                    saveGame(gameState);
                    break;
                default:
                    break;
            }
        }
        if (movements.length() > 0 && movements.charAt(movements.length() - 1) != 'q') {
            int endXX = gameState.playerLoc.xx + right;
            int endYY = gameState.playerLoc.yy + up;
            placePlayer(gameState.world, new Position(endXX, endYY));
        }
    }

    public static TETile randomTile(Random rand) {
        TETile[] tiles = new TETile[]{Tileset.FLOWER, Tileset.MOUNTAIN,
                                      Tileset.GRASS, Tileset.TREE, Tileset.FLOOR,
                                      Tileset.WATER, Tileset.SAND};
        int rnd = rand.nextInt(tiles.length);
        return tiles[rnd];
    }

    public static void drawRoom(Random rand, TETile[][] world, int w, int h, Position start) {
        TETile tileType = randomTile(rand);
        int startX = Math.max(start.xx, 1);
        for (int x = startX; x < (start.xx + w); x += 1) {
            for (int y = start.yy; y < (start.yy + h); y += 1) {
                world[x][y] = tileType;
            }
        }
    }

    public static void drawHallway(TETile[][] world, Random rand, Position start, Position end) {
        TETile tileType = randomTile(rand);
        int stopX = Math.min(WIDTH - 2, end.xx);
        int straightY = Math.min(HEIGHT - 2, start.yy);
        for (int x = start.xx; x <= stopX; x++) {
            world[x][straightY] = tileType;
        }
        int smaller = Math.min(start.yy, end.yy);
        int larger = Math.min(HEIGHT - 2, Math.max(start.yy, end.yy));
        for (int y = smaller; y <= larger; y++) {
            world[stopX][y] = tileType;
        }
    }

    public static void drawWalls(TETile[][] world) {
        for (int x = 1; x < WIDTH - 1; x++) {
            for (int y = 1; y < HEIGHT - 1; y++) {
                if (!(world[x][y] == Tileset.NOTHING || world[x][y] == Tileset.WALL)) {
                    if (world[x + 1][y] == Tileset.NOTHING) {
                        world[x + 1][y] = Tileset.WALL;
                    }
                    if (world[x - 1][y] == Tileset.NOTHING) {
                        world[x - 1][y] = Tileset.WALL;
                    }
                    if (world[x][y + 1] == Tileset.NOTHING) {
                        world[x][y + 1] = Tileset.WALL;
                    }
                    if (world[x][y - 1] == Tileset.NOTHING) {
                        world[x][y - 1] = Tileset.WALL;
                    }
                }
            }
        }
    }

    public static void drawQuitScreen() {
        StdDraw.clear();
        StdDraw.clear(Color.black);
        StdDraw.setPenColor(Color.white);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "Game saved");
        StdDraw.show();
    }

    public static void drawTutorial() {
        StdDraw.clear();
        StdDraw.clear(Color.black);
        StdDraw.setPenColor(Color.white);
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 2,
                "You've just crash-landed onto a mysterious planet...");
        StdDraw.show();
        StdDraw.pause(2000);
        StdDraw.text(WIDTH / 2, HEIGHT / 2,
                "Who knows what mysteries you'll uncover?");
        StdDraw.show();
        StdDraw.pause(2000);
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 2, "But beware the aliens...");
        StdDraw.show();
        StdDraw.pause(2000);
        StdDraw.clear();
        StdDraw.clear(Color.black);
        StdDraw.setPenColor(Color.white);
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 2, "use WASD to move");
        StdDraw.show();
        StdDraw.pause(3000);
    }

    public void triggerNPC() {
        int trigger = gameState.rand.nextInt(100);
        if (trigger == 42) {
            StdDraw.clear();
            StdDraw.clear(Color.black);
            StdDraw.setPenColor(Color.white);
            StdDraw.text(WIDTH / 2, HEIGHT / 2 + 2,
                    "out of the corner of your eye, you see something...");
            StdDraw.show();
            StdDraw.pause(2000);
            StdDraw.text(WIDTH / 2, HEIGHT / 2,
                    "the creature dashes towards you, blue goo dripping from its claws!");
            StdDraw.show();
            StdDraw.pause(2000);
            fightAlien();
        } else if (trigger == 69) {
            npcDialogue();
        }
    }

    public void npcDialogue() {
        String[] people = new String[] {
                "an old, grizzled man singing a song in a mysterious language",
                "a stern old woman wearing a traditional sari",
                "a young woman in a red jacket next to her racing pod",
                "an angry-looking woman with thick eyeliner cursing at her ship",
                "a bemused-looking man wearing a crumpled fedora",
                "a blond woman praying with a small group of settlers"
                };
        int randPerson = gameState.rand.nextInt(people.length);
        String[] advice = new String[]{
                "it's easier to defend yourself from the aliens than attack...",
                "the physics on this planet are mysterious... sometimes you can walk through walls",
                "sometimes I hear voices in my head... they say something about a... java?",
                "if you sow the right seeds, you'll always get the right results",
                "you can't see the aliens... until it's too late",
                "this planet is quite... expansive"
        };
        StdDraw.clear();
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(WIDTH/2, HEIGHT/2 + 2, "You run into " + people[randPerson]);
        StdDraw.text(WIDTH/2, HEIGHT/2, "They give you this advice: ");
        StdDraw.text(WIDTH/2, HEIGHT/2 - 2, advice[randPerson]);
        StdDraw.show();
        StdDraw.pause(6000);
    }

    public void fightAlien() {
        StdDraw.clear();
        StdDraw.clear(Color.black);
        StdDraw.setPenColor(Color.white);
        StdDraw.text(WIDTH/2, HEIGHT/2 + 2, "press 0 to attack");
        StdDraw.text(WIDTH/2, HEIGHT/2, "press 1 to defend");
        StdDraw.show();
        StdDraw.pause(3000);
        int playerHealth = gameState.rand.nextInt(10) + 1;
        int alienHealth = gameState.rand.nextInt(10) + 1;
        while (playerHealth > 0 && alienHealth > 0) {
            StdDraw.clear();
            StdDraw.clear(Color.black);
            StdDraw.setPenColor(Color.white);
            StdDraw.textLeft(1, HEIGHT - 1, "YOUR HEALTH: " + playerHealth);
            StdDraw.textRight(WIDTH - 1, HEIGHT - 1, "ALIEN HEALTH: " + alienHealth);
            StdDraw.text(WIDTH/2, HEIGHT - 1, "press 0 to attack");
            StdDraw.text(WIDTH/2, HEIGHT - 3, "press 1 to defend");
            int alienMove = gameState.rand.nextInt(2);
            if (StdDraw.hasNextKeyTyped() ){
                char playerMove = StdDraw.nextKeyTyped();
                if (alienMove == 1 && playerMove == '1') {
                    StdDraw.text(WIDTH / 2, HEIGHT / 2,
                            "You both blocked! Nothing happened");
                } else if (alienMove == 1 && playerMove == '0') {
                    StdDraw.text(WIDTH / 2, HEIGHT / 2,
                            "You attacked, but the alien blocked you!");
                    playerHealth -= 1;
                } else if (alienMove == 0 && playerMove == '1') {
                    StdDraw.text(WIDTH / 2, HEIGHT / 2,
                            "The alien attacked, but the you blocked it!");
                    alienHealth -= 1;
                } else if (alienMove == 0 && playerMove == '0') {
                    StdDraw.text(WIDTH / 2, HEIGHT / 2 + 2,
                            "You both attacked!");
                    int playerDamage = gameState.rand.nextInt(5);
                    int alienDamage = gameState.rand.nextInt(5);
                    StdDraw.text(WIDTH / 2, HEIGHT / 2,
                            "You lost " + playerDamage + " health");
                    StdDraw.text(WIDTH / 2, HEIGHT / 2 - 2,
                            "The alien lost " + alienDamage + " health");
                    playerHealth -= playerDamage;
                    alienHealth -= alienDamage;
                }
            }
            StdDraw.show();
            StdDraw.pause(2000);
        }
        if (playerHealth <= 0) {
            drawDeathScreen();
        } else {
            drawVictoryScreen();
        }
    }

    public void drawDeathScreen() {
        StdDraw.clear();
        StdDraw.clear(Color.black);
        StdDraw.setPenColor(Color.white);
        StdDraw.text(WIDTH/2, HEIGHT/2, "you have died");
        StdDraw.show();
        StdDraw.pause(999999999);
    }

    public void drawVictoryScreen() {
        StdDraw.clear();
        StdDraw.clear(Color.black);
        StdDraw.setPenColor(Color.white);
        StdDraw.text(WIDTH/2, HEIGHT/2 + 2,
                "the alien collapses with a final death screech!");
        StdDraw.show();
        StdDraw.pause(1000);
        StdDraw.text(WIDTH/2, HEIGHT/2, "you have won!");
        StdDraw.show();
        StdDraw.pause(1000);
    }

    public static void drawPortals(TETile[][] world, Random rand) {
        int numPortals = rand.nextInt(10) + 1;
        for (int i = 0; i < numPortals; i++) {
            int portalX = rand.nextInt(WIDTH - 2);
            int portalY = rand.nextInt(HEIGHT - 2);
            TETile possPortal = world[portalX][portalY];
            if (possPortal != Tileset.WALL && possPortal != Tileset.NOTHING) {
                world[portalX][portalY] = Tileset.PORTAL;
            }
        }
    }
}
