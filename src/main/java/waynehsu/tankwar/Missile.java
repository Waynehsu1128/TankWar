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
        switch (direction) {
            case UP:
                y -= SPEED;
                break;
            case LEFT_UP:
                y -= SPEED;
                x -= SPEED;
                break;
            case RIGHT_UP:
                y -= SPEED;
                x += SPEED;
                break;
            case DOWN:
                y += SPEED;
                break;
            case LEFT_DOWN:
                y += SPEED;
                x -= SPEED;
                break;
            case RIGHT_DOWN:
                y += SPEED;
                x += SPEED;
                break;
            case LEFT:
                x -= SPEED;
                break;
            case RIGHT:
                x += SPEED;
                break;
        }
    }

    void draw(Graphics g) {
        move();
        if (x < 0 || x > 800 || y < 0 || y > 600) {
            return;
        }
        g.drawImage(getImage(), x, y, null);
    }
}
