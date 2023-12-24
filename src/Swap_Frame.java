import java.awt. *;
import javax.swing.*;
public class Swap_Frame extends JFrame {
    public Swap_Frame(Swap_Panel panel) {
        this.add(panel);
        panel.setFrame(this);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setAlwaysOnTop(true);
        this.setVisible(true);
        ImageIcon icon = new ImageIcon("src/imgs/Swapper.png");
        Image image = icon.getImage();
        this.setIconImage(image);
        this.setTitle("Swap Emulator");


    }

    public static void main(String[] args) {
    }
}
