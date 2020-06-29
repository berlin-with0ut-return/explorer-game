package byog.lab5;
import org.junit.Test;
import static org.junit.Assert.*;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static class Position {
        int x;
        int y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
    public static void addHexagon(TETile[][] world, Position p, int s, TETile t) {
        int rowStart = p.x;
        int rowLen = s;
        int currY = p.y;
        for (int i = 0; i < s; i++) {
            drawRow(world, new Position(rowStart, currY), rowLen, t);
            rowStart -= 1;
            currY += 1;
            rowLen += 2;
        }
        rowLen -= 2;
        rowStart += 1;
        for (int j = 0; j < s; j++) {
            drawRow(world, new Position(rowStart, currY), rowLen, t);
            rowStart += 1;
            currY += 1;
            rowLen -= 2;
        }
    }

    public static void drawRow(TETile[][] world, Position p, int len, TETile t) {
        int startX = p.x;
        for (int i = 0; i < len; i++) {
            world[startX][p.y] = t;
            startX++;
        }
    }

    public static void drawHexColumn(TETile[][] world, int numHex, Position start) {
        int hexY = start.y;
        for (int i = 0; i < numHex; i++) {
            addHexagon(world, new Position(start.x, hexY), 3, randomTile());
            hexY += 6;
        }
    }

    public static TETile randomTile() {
        TETile[] tiles = new TETile[]{Tileset.FLOWER, Tileset.MOUNTAIN, Tileset.GRASS, Tileset.TREE};
        int rnd = new Random().nextInt(tiles.length);
        return tiles[rnd];
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        int width = 35;
        int height = 35;
        ter.initialize(width, height);
        TETile[][] world = new TETile[width][height];
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        Position drawPos = new Position(2, 6);
        int[] xPoss = new int[]{2, 7, 12, 17, 22};
        int[] hexSizes = new int[]{3, 4, 5, 4, 3};
        int[] yPoss = new int[]{6, 3, 0, 3, 6};
        for (int i = 0; i < 5; i++) {
            drawPos.x = xPoss[i];
            drawPos.y = yPoss[i];
            drawHexColumn(world, hexSizes[i], drawPos);
        }
        ter.renderFrame(world);
    }
}
