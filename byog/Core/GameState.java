package byog.Core;

import byog.TileEngine.TETile;

import java.io.Serializable;
import java.util.Random;

public class GameState implements Serializable {
    public Random rand;
    public TETile[][] world;
    public Position playerLoc;

    public GameState(Random rand, TETile[][] world, Position playerLoc) {
        this.rand = rand;
        this.world = world;
        this.playerLoc = playerLoc;
    }
}
