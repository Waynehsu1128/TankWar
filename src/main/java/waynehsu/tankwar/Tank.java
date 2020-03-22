package waynehsu.tankwar;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.nio.file.DirectoryNotEmptyException;
import java.util.Random;

class Tank {

    private static final int MOVE_SPEED = 5;
    private int x;
    private int y;

    private boolean enemy;

    private boolean live = true;

    private static final int MAX_HP = 100;

    private int hp = 100;

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public boolean isLive() {
        return live;
    }

    void setLive(boolean live) {
        this.live = live;
    }

    boolean isEnemy() {
        return enemy;
    }

    private  Direction direction;

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
        x += direction.xFactor * MOVE_SPEED;
        y += direction.yFactor * MOVE_SPEED;
    }

    // 方向圖標
    Image getImage() {
        String prefix = enemy ? "e" : "";
        return direction.getImage(prefix + "tank");
    }

    boolean isDying() {
        return this.hp <= MAX_HP * 0.2;
    }

    void draw(Graphics g) {
        int oldX = x;
        int oldY = y;
        if (!this.enemy) {
            this.determineDirection();
        }
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
            if (tank != this && rec.intersects(tank.getRectangle())) {      // tank是敵方坦克時
                x = oldX;
                y = oldY;
                break;
            }
        }
        // 如果當前是一個enemy的tank 且 和我方坦克碰撞
        if (this.enemy && rec.intersects(GameClient.getInstance().getPlayerTank().getRectangle())) {
            x = oldX;
            y = oldY;
        }

        if (!enemy) {
            // 與血包的交集
            Blood blood = GameClient.getInstance().getBlood();
            if (blood.isLive() && this.getRectangle().intersects(blood.getRectangle())) {
                this.hp = MAX_HP;
                Tools.playAudio("revive.wav");
                GameClient.getInstance().getBlood().setLive(false);
            }

            g.setColor(Color.WHITE);
            g.fillRect(x, y - 10, this.getImage().getWidth(null), 10);

            g.setColor(Color.RED);
            int width = hp * this.getImage().getWidth(null) / MAX_HP;
            g.fillRect(x, y - 10, width, 10);

            Image petImage = Tools.getImage("pet-camel.gif");
            g.drawImage(petImage, this.x - petImage.getWidth(null) - DISTANCE_TO_PET, this.y, null);
        }
        g.drawImage(this.getImage(), this.x, this.y, null);
    }

    private static final int DISTANCE_TO_PET = 4;

    private Rectangle getRectangle() {
        if (enemy) {
            return new Rectangle(x, y, getImage().getWidth(null), getImage().getHeight(null));
        } else {
            Image petImage = Tools.getImage("pet-camel.gif");
            int delta = petImage.getWidth(null) + DISTANCE_TO_PET;
            return new Rectangle(x - delta, y,
                    getImage().getWidth(null) + delta, getImage().getHeight(null));
        }
    }

    Rectangle getRectangleForHitDetection() {
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
            case KeyEvent.VK_F2 : GameClient.getInstance().restart(); break;
        }
    }

    private void fire() {
         Missile missile = new Missile(x + getImage().getWidth(null) / 2 - 6,
                 y + getImage().getHeight(null) / 2 - 6, enemy, direction);
         GameClient.getInstance().getMissiles().add(missile);    // 每fire一次加一個missile

         // import sound
        Tools.playAudio("shoot.wav");
    }

    private void superFire() {
        for (Direction direction : Direction.values()) {
            Missile missile = new Missile(x + getImage().getWidth(null) / 2 - 6,
                    y + getImage().getHeight(null) / 2 - 6, enemy, direction);
            GameClient.getInstance().getMissiles().add(missile);    // 每fire一次加一個missile
        }

        // import sound
        String audioFile = new Random().nextBoolean() ? "supershoot.aiff" : "supershoot.wav";
        Tools.playAudio(audioFile);
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

    private final Random random = new Random();
    private int step = random.nextInt(12) + 3;

    void actRandomly() {
        // 隨機方向 隨機開火
        Direction[] dirs = Direction.values();
        if (step == 0) {
            step = random.nextInt(12) + 3;    //偏移量？
            this.direction = dirs[random.nextInt(dirs.length)];
            if (random.nextBoolean()) {
                this.fire();
            }
        }
        step--;
    }
}
