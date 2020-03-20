package waynehsu.tankwar;

import java.awt.*;

class Missile {
    private static final int SPEED = 10;
    private int x;
    private int y;

    private final boolean enemy;

    private final Direction direction; // 子彈方向一旦定義了就不能改方向

    public Missile(int x, int y, boolean enemy, Direction direction) {
        this.x = x;
        this.y = y;
        this.enemy = enemy;
        this.direction = direction;
    }

    private Image getImage() {
        return direction.getImage("missile");
    }

    void move() {
        x += direction.xFactor * SPEED;
        y += direction.yFactor * SPEED;
    }

    void draw(Graphics g) {
        move();
        if (x < 0 || x > 800 || y < 0 || y > 600) {
            return;
        }
        g.drawImage(getImage(), x, y, null);
    }
}
