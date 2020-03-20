package waynehsu.tankwar;

import java.awt.*;

enum Direction {
    UP("U"),
    DOWN("D"),
    LEFT("L"),
    RIGHT("R"),

    LEFT_UP("LU"),
    RIGHT_UP("RU"),
    RIGHT_DOWN("RD"),
    LEFT_DOWN("LD");
    // 約定優於配置
    private final String abbrev;

    Direction (String abbrev) {
        this.abbrev = abbrev;
    }

    Image getImage (String prefix) {
        return Tools.getImage(prefix + abbrev + ".gif");
    }

}
