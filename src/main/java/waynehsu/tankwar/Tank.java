package waynehsu.tankwar;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Random;

class Tank {

    private int x;
    private int y;

    private boolean enemy;

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
    private void move() {
        if (this.stopped) {
            return;
        }
        switch (direction) {
            case UP:
                y -= 5;
                break;
            case LEFT_UP:
                y -= 5;
                x -= 5;
                break;
            case RIGHT_UP:
                y -= 5;
                x += 5;
                break;
            case DOWN:
                y += 5;
                break;
            case LEFT_DOWN:
                y += 5;
                x -= 5;
                break;
            case RIGHT_DOWN:
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
        return direction.getImage(prefix + "tank");
    }

    void draw(Graphics g) {
        int oldX = x;
        int oldY = y;
        this.determineDirection();
        this.move();

        // 對 x y 進行檢查 有沒有超出視窗
        if (x < 0) {
            x = 0;
        } else if (x > (800 - getImage().getWidth(null))) {
            x = 800 - getImage().getWidth(null);
        }
        if (y < 0) {
            y = 0;
        } else if (y > (600 - getImage().getHeight(null))) {
            y = 600 - getImage().getHeight(null);
        }

        // tank interact with wall
        Rectangle rec = this.getRectangle();
        // GameClient is a singleton
        for (Wall wall : GameClient.getInstance().getWalls()) {
            if (rec.intersects(wall.getRectangle())) {
                x = oldX;
                y = oldY;
                break;
            }
        }

        // tank interact with enemyTanks
        for (Tank tank : GameClient.getInstance().getEnemyTanks()) {
            if (rec.intersects(tank.getRectangle())) {
                x = oldX;
                y = oldY;
                break;
            }
        }

        g.drawImage(this.getImage(), this.x, this.y, null);
    }

    Rectangle getRectangle() {
        return new Rectangle(x, y, getImage().getWidth(null), getImage().getHeight(null));
    }
    // 每個鍵位方向的boolean use to determine the movement
    private boolean up, down, left, right;

    void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP : up = true; break;
            case KeyEvent.VK_DOWN : down = true; break;
            case KeyEvent.VK_LEFT : left = true; break;
            case KeyEvent.VK_RIGHT : right = true; break;
            case KeyEvent.VK_SPACE : fire(); break;
            case KeyEvent.VK_A : superFire(); break;
        }
    }

    private void fire() {
         Missile missile = new Missile(x + getImage().getWidth(null) / 2 - 6,
                 y + getImage().getHeight(null) / 2 - 6, enemy, direction);
         GameClient.getInstance().getMissiles().add(missile);    // 每fire一次加一個missile

         // import sound
        playAudio("shoot.wav");
    }

    private void superFire() {
        for (Direction direction : Direction.values()) {
            Missile missile = new Missile(x + getImage().getWidth(null) / 2 - 6,
                    y + getImage().getHeight(null) / 2 - 6, enemy, direction);
            GameClient.getInstance().getMissiles().add(missile);    // 每fire一次加一個missile
        }

        // import sound
        String audioFile = new Random().nextBoolean() ? "supershoot.aiff" : "supershoot.wav";
        playAudio(audioFile);
    }

    private void playAudio(String fileName) {
        Media sound = new Media(new File("assets/audios/" + fileName).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }

    private boolean stopped;

    private void determineDirection() {
        if (!up && !left && !down && !right) {
            this.stopped = true;
        } else {
            if (up && left && !down && !right) {
                this.direction = Direction.LEFT_UP;
            } else if (up && !left && !down && right) {
                this.direction = Direction.RIGHT_UP;
            } else if (up && !left && !down && !right) {
                this.direction = Direction.UP;
            } else if (!up && !left && down && !right) {
                this.direction = Direction.DOWN;
            } else if (!up && left && down && !right) {
                this.direction = Direction.LEFT_DOWN;
            } else if (!up && !left && down && right) {
                this.direction = Direction.RIGHT_DOWN;
            } else if (!up && left && !down && !right) {
                this.direction = Direction.LEFT;
            } else if (!up && !left && !down && right) {
                this.direction = Direction.RIGHT;
            }
            this.stopped = false;
        }
    }

    void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP : up = false; break;
            case KeyEvent.VK_DOWN : down = false; break;
            case KeyEvent.VK_LEFT : left = false; break;
            case KeyEvent.VK_RIGHT : right = false; break;
        }
    }
}
