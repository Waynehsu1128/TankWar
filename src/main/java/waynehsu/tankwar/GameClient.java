package waynehsu.tankwar;

import com.alibaba.fastjson.JSON;
import javafx.geometry.Pos;
import org.apache.commons.io.FileUtils;
import waynehsu.tankwar.Save.Position;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class GameClient extends JComponent {

    static final int WIDTH = 800, HEIGHT = 600;
    // singleton
    private static final GameClient INSTANCE = new GameClient();

    private static final String GAME_SAV = "game.sav";

    static GameClient getInstance() {
        return INSTANCE;
    }

    private Tank playerTank;

    private List<Tank> enemyTanks;

    private final AtomicInteger enemyKilled = new AtomicInteger(0);

    private List<Wall> walls;

    private List<Missile> missiles;

    private List<Explosion> explosions;

    private Blood blood;

    Blood getBlood() {
        return blood;
    }

    void addExplosion(Explosion explosion) {
        explosions.add(explosion);
    }

    void add(Missile missile) {
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
        this.missiles = new CopyOnWriteArrayList<>();
        this.explosions = new ArrayList<>();
        this.blood = new Blood(400, 250);
        this.walls = Arrays.asList(
                new Wall(280, 140, true, 12),
                new Wall(280, 540, true, 12),
                new Wall(100, 160, false, 12),
                new Wall(700, 160, false, 12)
        );
        this.initEnemyTanks();
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
    }

    private void initEnemyTanks() {
        // initialize enemy tank
        this.enemyTanks = new CopyOnWriteArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                Tank enemyTank = new Tank(200 + j * 120, 400 + 40 * i, true, Direction.UP);
                if (enemyTank.isCollidedWith(playerTank)) {
                    continue;
                }
                this.enemyTanks.add(enemyTank);
            }
        }
    }

    private final static Random rand = new Random();

    // 繪製
    @Override
    public void paintComponent(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        if (!playerTank.isLive()) {
            g.setColor(Color.RED);
            g.setFont(new Font(null, Font.BOLD, 100));
            g.drawString("GAME OVER", 100, 200);
            g.setFont(new Font(null, Font.BOLD, 60));
            g.drawString("PRESS F2 To Restart", 100, 360);
        } else {
            g.setColor(Color.WHITE);
            g.setFont(new Font(null, Font.BOLD, 16));
            g.drawString("Missiles:" + missiles.size(), 10, 30);
            g.drawString("Explosions:" + explosions.size(), 10, 50);
            g.drawString("Tank Hp:" + playerTank.getHp(), 10, 70);
            g.drawString("Enemy Left:" + enemyTanks.size(), 10, 90);
            g.drawString("Enemy Killed:" + enemyKilled.get(), 10, 110);
            g.drawImage(Tools.getImage("tree.png"), 720, 10, null);
            g.drawImage(Tools.getImage("tree.png"), 10, 520, null);

            playerTank.draw(g);
            //判斷血包
            if (playerTank.isDying() && rand.nextInt(3) == 2) {
                blood.setLive(true);
            }
            if (blood.isLive()) {
                blood.draw(g);
            }

            int count = enemyTanks.size();
            enemyTanks.removeIf(t -> !t.isLive());
            enemyKilled.addAndGet(count - enemyTanks.size());
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
    }

    public static void main(String[] args) {
        // start audio (javafx toolkit initialize)
        com.sun.javafx.application.PlatformImpl.startup(() -> {});

        JFrame frame = new JFrame();
        frame.setTitle("Tank War");
        frame.setIconImage(new ImageIcon("assets/images/icon.png").getImage());
        final GameClient client = GameClient.getInstance();
        frame.add(client);
        // save
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    client.save();
                    System.exit(0);
                } catch (IOException ex) {  //  如果確實有異常,給用戶報警
                    JOptionPane.showMessageDialog(null, "Failed to save current game!",
                            "Oops! Error Occurred",JOptionPane.ERROR_MESSAGE);
                    System.exit(4);
                }
            }
        });
        //frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);  // stop運行when closed
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

        try {
            client.load();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to load previous game!",
                    "Oops! Error Occurred",JOptionPane.ERROR_MESSAGE);
        }
        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                client.repaint();
                if (client.playerTank.isLive()) {
                    for (Tank tank : client.enemyTanks) {
                        tank.actRandomly();
                    }
                }
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void load() throws IOException {
        File file = new File(GAME_SAV);
        if (file.exists() && file.isFile()) {
            String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            Save save = JSON.parseObject(json, Save.class);
            if (save.isGameContinued()) {   //如果遊戲還繼續的話
                this.playerTank = new Tank(save.getPlayerPosition(), false);
                this.enemyTanks.clear();
                List<Position> enemyPositions = save.getEnemyPositions();
                if (enemyPositions != null && !enemyPositions.isEmpty()) {
                    for (Position position : enemyPositions) {
                        this.enemyTanks.add(new Tank(position, true));
                    }
                }
            }
        }
    }

    void save(String destination) throws IOException {
        Save save = new Save(playerTank.isLive(), playerTank.getPosition(),
                enemyTanks.stream().filter(Tank::isLive)    // 滿足isLive()的條件 (using method reference)
                        .map(Tank::getPosition)            // 轉換成Position(using method reference)
                        .collect(Collectors.toList()));         // 轉成一個list
//        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(destination)))) {
//            out.println(JSON.toJSONString(save, true));
//        } // 沒有建立一個destination的文件夾所以會報錯
        // use commons io tool
        FileUtils.write(new File(destination), JSON.toJSONString(save, true),
                StandardCharsets.UTF_8);
    }

    void save() throws IOException {
        this.save(GAME_SAV);
    }

    void restart() {
        if (!playerTank.isLive()) {
            playerTank = new Tank(400, 100, Direction.DOWN);
        }
        this.initEnemyTanks();
    }
}
