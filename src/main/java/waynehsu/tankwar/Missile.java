package waynehsu.tankwar;

import java.awt.*;

class Missile {
    private static final int SPEED = 10;
    private int x;
    private int y;

    private final boolean enemy;

    private boolean live = true;

    boolean isLive() {
        return live;
    }

    void setLive(boolean live) {
        this.live = live;
    }

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
            this.live = false;
            return;
        }
        Rectangle rec = this.getRectangle();
        //與牆發生重疊
        for (Wall wall : GameClient.getInstance().getWalls()) {
            if (rec.intersects(wall.getRectangle())) {
                this.setLive(false);
                return;
            }
        }

        // 跟坦克發生碰撞 我方坦克與敵人的碰撞
        if (enemy) {
            Tank playerTank = GameClient.getInstance().getPlayerTank();
            if (rec.intersects(playerTank.getRectangle())) {
                addExplosion();
                playerTank.setHp(playerTank.getHp() - 20);
                if (playerTank.getHp() <= 0) {
                    playerTank.setLive(false);
                }
                this.setLive(false);
            }
        } else {
            for (Tank tank : GameClient.getInstance().getEnemyTanks()) {
                if (rec.intersects(tank.getRectangle())) {
                    addExplosion();
                    tank.setLive(false);
                    this.setLive(false);
                    break;
                }
            }
        }
        g.drawImage(getImage(), x, y, null);
    }

    private void addExplosion() {
        GameClient.getInstance().addExplosion(new Explosion(x, y));
        Tools.playAudio("explode.wav");
    }

    Rectangle getRectangle() {
        return new Rectangle(x, y, getImage().getWidth(null), getImage().getHeight(null));
    }
}
