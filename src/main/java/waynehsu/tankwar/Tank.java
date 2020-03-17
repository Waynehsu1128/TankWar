package waynehsu.tankwar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class Tank {

    private int x;
    private int y;

    private boolean enemy;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return y;
    }

    private Direction direction;

    public Tank(int x, int y, Direction direction) {
        this(x, y, false, direction.DOWN);
    }

    public Tank(int x, int y, boolean enemy, Direction direction) {
        this.x = x;
        this.y = y;
        this.enemy = enemy;
        this.direction = direction;
    }

    // 移動方向
    void move() {
        if (this.stopped) {
            return;
        }
        switch (direction) {
            case UP:
                y -= 5;
                break;
            case UPLEFT:
                y -= 5;
                x -= 5;
                break;
            case UPRIGHT:
                y -= 5;
                x += 5;
                break;
            case DOWN:
                y += 5;
                break;
            case DOWNLEFT:
                y += 5;
                x -= 5;
                break;
            case DOWNRIGHT:
                y += 5;
                x += 5;
                break;
            case LEFT:
                x -= 5;
                break;
            case RIGHT:
                x += 5;
                break;
        }
    }

    // 方向圖標
    Image getImage() {
        String prefix = enemy ? "e" : "";
        switch (direction) {
            case UP:
                return new ImageIcon("/Users/waynehsu/Documents/tank project/TankProject/assets/images/" + prefix + "tankU.gif").getImage();
            case UPLEFT:
                return new ImageIcon("/Users/waynehsu/Documents/tank project/TankProject/assets/images/" + prefix + "tankLU.gif").getImage();
            case UPRIGHT:
                return new ImageIcon("/Users/waynehsu/Documents/tank project/TankProject/assets/images/" + prefix + "tankRU.gif").getImage();
            case DOWN:
                return new ImageIcon("/Users/waynehsu/Documents/tank project/TankProject/assets/images/" + prefix + "tankD.gif").getImage();
            case DOWNLEFT:
                return new ImageIcon("/Users/waynehsu/Documents/tank project/TankProject/assets/images/" + prefix + "tankLD.gif").getImage();
            case DOWNRIGHT:
                return new ImageIcon("/Users/waynehsu/Documents/tank project/TankProject/assets/images/" + prefix + "tankRD.gif").getImage();
            case LEFT:
                return new ImageIcon("/Users/waynehsu/Documents/tank project/TankProject/assets/images/" + prefix + "tankL.gif").getImage();
            case RIGHT:
                return new ImageIcon("/Users/waynehsu/Documents/tank project/TankProject/assets/images/" + prefix + "tankR.gif").getImage();
        }
        return null;
    }

    void draw(Graphics g) {
        this.determineDirection();
        this.move();
        g.drawImage(this.getImage(), this.getX(), this.getY(), null);
    }

    // 每個鍵位方向的boolean use to determine the movement
    private boolean up, down, left, right;

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP : up = true; break;
            case KeyEvent.VK_DOWN : down = true; break;
            case KeyEvent.VK_LEFT : left = true; break;
            case KeyEvent.VK_RIGHT : right = true; break;
        }
    }

    private boolean stopped;

    private void determineDirection() {
        if (!up && !left && !down && !right) {
            this.stopped = true;
        } else {
            if (up && left && !down && !right) {
                this.direction = Direction.UPLEFT;
            } else if (up && !left && !down && right) {
                this.direction = Direction.UPRIGHT;
            } else if (up && !left && !down && !right) {
                this.direction = Direction.UP;
            } else if (!up && !left && down && !right) {
                this.direction = Direction.DOWN;
            } else if (!up && left && down && !right) {
                this.direction = Direction.DOWNLEFT;
            } else if (!up && !left && down && right) {
                this.direction = Direction.DOWNRIGHT;
            } else if (!up && left && !down && !right) {
                this.direction = Direction.LEFT;
            } else if (!up && !left && !down && right) {
                this.direction = Direction.RIGHT;
            }
            this.stopped = false;
        }
    }

    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP : up = false; break;
            case KeyEvent.VK_DOWN : down = false; break;
            case KeyEvent.VK_LEFT : left = false; break;
            case KeyEvent.VK_RIGHT : right = false; break;
        }
    }
}
