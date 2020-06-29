package byog.Core;

import java.io.Serializable;

public class Position implements Serializable {
    int xx;
    int yy;

    Position(int x, int y) {
        xx = x;
        yy = y;
    }
}
