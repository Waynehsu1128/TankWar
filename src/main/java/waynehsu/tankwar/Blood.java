package waynehsu.tankwar;

import java.awt.*;

class Blood {
    private int x, y;

    private boolean live = true;

    private final Image image;

    Blood(int x, int y) {
        this.x = x;
        this.y = y;
        this.image = Tools.getImage("blood.png");
    }

    boolean isLive() {
        return live;
    }

    void setLive(boolean live) {
        this.live = live;
    }

    void draw(Graphics g) {
        g.drawImage(image, x, y, null);
    }

    Rectangle getRectangle() {
        return new Rectangle(x, y, image.getWidth(null), image.getHeight(null));
    }
}
