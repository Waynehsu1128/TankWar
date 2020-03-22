package waynehsu.tankwar;

import java.awt.*;

public class Explosion {

    private int x;
    private int y;

    Explosion(int x, int y) {
        this.x = x;
        this.y = y;
    }

    private int step = 0;
    private boolean live = true;

    boolean isLive() {
        return live;
    }

    void setLive(boolean live) {
        this.live = live;
    }

    void draw(Graphics g) {
        if (step > 10) {
            this.setLive(false);
            return;
        }
        g.drawImage(Tools.getImage(step++ + ".gif"), x, y, null);
    }
}
