package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;

public class GameTests {

    public static void main(String[] args) {
        Game testGame = new Game();
        TERenderer ter = new TERenderer();
        ter.initialize(80, 30);
        TETile[][] result = testGame.playWithInputString("n8937948082984650576s");
        ter.renderFrame(result);
    }
}
