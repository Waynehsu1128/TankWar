package waynehsu.tankwar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;

public class GameClient extends JComponent {

    // singleton
    private static final GameClient INSTANCE = new GameClient();
    static GameClient getInstance() {
        return INSTANCE;
    }

    private Tank playerTank;

    private List<Tank> enemyTanks;

    private List<Wall> walls;

    private List<Missile> missiles;

    private List<Explosion> explosions;

    void addExplosion(Explosion explosion) {
        explosions.add(explosion);
    }

    synchronized void add(Missile missile) {
        missiles.add(missile);
    }

    Tank getPlayerTank() {
        return playerTank;
    }

    List<Missile> getMissiles() {
        return missiles;
    }

    List<Tank> getEnemyTanks() {
        return enemyTanks;
    }

    List<Wall> getWalls() {
        return walls;
    }

    private GameClient() {
        this.playerTank = new Tank(400, 100, Direction.DOWN);
        this.missiles = new ArrayList<>();
        this.explosions = new ArrayList<>();
        this.walls = Arrays.asList(
                new Wall(200, 140, true, 15),
                new Wall(200, 540, true, 15),
                new Wall(100, 80, false, 15),
                new Wall(700, 80, false, 15)
        );
        this.initEnemyTanks();
        this.setPreferredSize(new Dimension(800, 600));
    }

    private void initEnemyTanks() {
        // initialize enemy tank
        this.enemyTanks = new ArrayList<>(12);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                this.enemyTanks.add(new Tank(200 + j * 120, 400 + 40 * i, true, Direction.UP));
            }
        }
    }

    // 繪製
    @Override
    public void paintComponent(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 800, 600);
        playerTank.draw(g);

        enemyTanks.removeIf(t -> !t.isLive());
        if (enemyTanks.isEmpty()) {
            this.initEnemyTanks();
        }
        for (Tank tank : enemyTanks) {
           tank.draw(g);
        }
        for (Wall wall : walls) {
           wall.draw(g);
        }
        missiles.removeIf(m -> !m.isLive());
        for (Missile missile : missiles) {
            missile.draw(g);
        }
        explosions.removeIf(e -> !e.isLive());
            for (Explosion explosion : explosions) {
                explosion.draw(g);
        }
    }

    public static void main(String[] args) {
        // start audio (javafx toolkit initialize)
        com.sun.javafx.application.PlatformImpl.startup(() -> {});

        JFrame frame = new JFrame();
        frame.setTitle("Tank War");
        frame.setIconImage(new ImageIcon("assets/images/icon.png").getImage());
        final GameClient client = GameClient.getInstance();
        client.repaint();
        frame.add(client);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);  // stop運行when closed
        frame.pack();   //  讓窗口fit preferred size
        frame.addKeyListener(new KeyAdapter() { //對按鍵的響應
            @Override
            public void keyPressed(KeyEvent e) {
                client.playerTank.keyPressed(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                client.playerTank.keyReleased(e);
            }
        });
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        //noinspection InfiniteLoopStatement
        while (true) {
            client.repaint();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
