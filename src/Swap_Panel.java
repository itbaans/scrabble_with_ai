import javax.swing.*;
import java.awt.*;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Swap_Panel extends JPanel implements ActionListener {
    private JButton[] swap_buttons = new JButton[7];
    private JButton confirm_button = new JButton();
    private JButton reset_button = new JButton();
    private JButton close_button = new JButton();
    public ArrayList<Tile> swap_selections = new ArrayList<>();
    private ArrayList<Tile> current_selected_array;
    private Panel panel;
    private Frame frame;
    Swap_Panel(){
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(480, 300));
        setFocusable(true);
        swap_buttons_ini();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        swap_grid(100,100,40,40, (Graphics2D)g);
        options_buttons_grid(100,150,100,100);
    }

    @Override
    public void paintComponents(Graphics g) {
        super.paintComponents(g);
    }
    public void swap_buttons_ini(){
        for (int i = 0; i <swap_buttons.length ; i++) {

            swap_buttons[i] = new JButton();
            swap_buttons[i].setBorder(new RoundedButton(10));
            swap_buttons[i].setBackground(new Color(229,221,200));
            swap_buttons[i].addActionListener(this);
            this.add(swap_buttons[i]);
        }
        ImageIcon icon = new ImageIcon("src/imgs/Confirm.png");
        Image image = icon.getImage().getScaledInstance(100,100, Image.SCALE_SMOOTH);
        icon = new ImageIcon(image);
        confirm_button = new JButton(icon);
        icon = new ImageIcon("src/imgs/Reset.png");
        image = icon.getImage().getScaledInstance(100,100, Image.SCALE_SMOOTH);
        icon = new ImageIcon(image);
        reset_button = new JButton(icon);
        icon = new ImageIcon("src/imgs/Close.png");
        image = icon.getImage().getScaledInstance(100,100, Image.SCALE_SMOOTH);
        icon = new ImageIcon(image);
        close_button = new JButton(icon);
        confirm_button.setBorder(new RoundedButton(10));
        reset_button.setBorder(new RoundedButton(10));
        close_button.setBorder(new RoundedButton(10));
        confirm_button.addActionListener(this);
        confirm_button.setBackground(new Color(144,238,144));
        reset_button.addActionListener(this);
        reset_button.setBackground(new Color(204,204,204));
        close_button.addActionListener(this);
        close_button.setBackground(new Color(235,45,58));
        this.add(confirm_button);
        this.add(reset_button);
        this.add(close_button);
    }
    public void swap_grid(int x, int y, int width, int height,Graphics2D graphics2D){
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setFont(new Font("Roboto",Font.BOLD,25));
        graphics2D.drawString("Please select tiles to swap from Board",x-width*2,y-height);
        int x_p = x;
        for (int i = 0; i <swap_buttons.length ; i++) {
            swap_buttons[i].setBounds(x_p,y,width,height);
            x_p = x_p + width;
        }
        graphics2D.setFont(null);
    }
    public void options_buttons_grid(int x, int y, int width, int height){
        confirm_button.setBounds(x,y,width,height);
        reset_button.setBounds(x+width,y,width,height);
        close_button.setBounds(x+2*width,y,width,height);
    }
    public void swap_button_rearrange(){
        int h = 40;
        int w = 40;
        for (int i = 0; i <swap_selections.size() ; i++) {
            ImageIcon icon = new ImageIcon("src/imgs/" + String.valueOf(swap_selections.get(i).letter).toUpperCase() +".png");
            Image image = icon.getImage().getScaledInstance(w,h, Image.SCALE_SMOOTH);
            icon = new ImageIcon(image);
            swap_buttons[i].setIcon(icon);
        }
        for (int i = swap_selections.size(); i <swap_buttons.length ; i++) {
            ImageIcon icon = new ImageIcon("src/imgs/Empty.png");
            Image image = icon.getImage().getScaledInstance(w,h, Image.SCALE_SMOOTH);
            icon = new ImageIcon(image);
            swap_buttons[i].setIcon(icon);
        }
    }

    public void swap_tiles_on_confirm(){
        for (int i = 0; i < swap_selections.size(); i++) {
            current_selected_array.add(panel.tileBag.getTiles().remove((int)(Math.random()*panel.tileBag.getTiles().size())));
        }
        panel.tileBag.getTiles().addAll(swap_selections);
        swap_selections.clear();
        panel.tile_rack_rearrange();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < swap_selections.size(); i++) {
            if (e.getSource() == swap_buttons[i]){
                getCurrent_selected_array().add(swap_selections.remove(i));
            }
            swap_button_rearrange();
            panel.tile_rack_rearrange();
        }

        if (e.getSource() == confirm_button){
            swap_tiles_on_confirm();
            swap_button_rearrange();
            panel.tile_rack_rearrange();
            panel.swap_active = false;
            panel.change_turn();
            panel.tileBag.remaining_tiles();
            panel.refresh();
            frame.dispose();
        }
        else if (e.getSource() == reset_button){
            Reset();
            swap_button_rearrange();
            panel.tile_rack_rearrange();
        }
        else if (e.getSource() == close_button){
            Reset();
            swap_button_rearrange();
            panel.tile_rack_rearrange();
            panel.swap_active = false;
            frame.dispose();
        }
    }
    public void Reset(){
        current_selected_array.addAll(swap_selections);
        swap_selections.clear();
    }
    public ArrayList<Tile> getCurrent_selected_array() {
        return current_selected_array;
    }

    public void setCurrent_selected_array(ArrayList<Tile> current_selected_array) {
        this.current_selected_array = current_selected_array;
    }

    public void setPanel(Panel panel) {
        this.panel = panel;
    }
    public void setFrame(Frame frame){
        this.frame = frame;
    }
}
