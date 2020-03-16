package waynehsu.tankwar;

import javax.swing.*;
import java.awt.*;

public class GameClient extends JComponent {

    private GameClient() {
        this.setPreferredSize(new Dimension(800, 600));
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(new ImageIcon("/Users/waynehsu/Documents/tank project/TankProject/assets/images/tankD.gif").getImage(), 400, 300, null);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setTitle("Tank War");
        frame.setIconImage(new ImageIcon("assets/images/icon.png").getImage());
        GameClient client = new GameClient();
        client.repaint();
        frame.add(client);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);  // stop運行when closed
        frame.pack();   //  讓窗口fit preferred size
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
