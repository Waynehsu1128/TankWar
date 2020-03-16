package waynehsu.tankwar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameClient extends JComponent {

    private Tank playerTank;

    private GameClient() {
        this.playerTank = new Tank(400, 100, Direction.DOWN);
        this.setPreferredSize(new Dimension(800, 600));
    }

    @Override
    public void paintComponent(Graphics g) {
       playerTank.draw(g);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setTitle("Tank War");
        frame.setIconImage(new ImageIcon("assets/images/icon.png").getImage());
        final GameClient client = new GameClient();
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
