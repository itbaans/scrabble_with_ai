import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.Frame;
import java.awt.event.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.*;
import javax.swing.border.Border;

public class Panel extends JPanel implements ActionListener {
    public boolean swap_active = false;
    private int B_WIDTH = 1280;
    private int B_HEIGHT = 720;
    Board board = new Board();
    TileBag tileBag = new TileBag();
    BoardCell[][] ref_board;
    private JButton[][] buttons = new JButton[15][15];
    private JButton[] tile_rack_player_1 = new JButton[7];
    private JButton[] tile_rack_player_2 = new JButton[7];
    private ArrayList<Tile> tiles_present_player1;
    private ArrayList<Tile> tiles_present_player2;
    private JButton[] options_buttons = new JButton[6];
    private int player = 1;
    private Tile current_letter_selected;
    private ArrayList<Integer> current_tile_selected = new ArrayList<>();
    private ArrayList<Tile> tiles_selected_from_rack = new ArrayList<>();
    private ArrayList<int[]> temp_positions = new ArrayList<>();
    private int Player_score_1 = 0;
    private int Player_score_2 = 0;
    private boolean[][] tempBoard;
    Swap_Panel swap_panel = new Swap_Panel();
    private JButton[] shuffle = new JButton[2];

    private boolean gameEnd;

    Panel(){
        ref_board = board.getTheBoard();
        tiles_present_player1 =tileBag.getRack_player_1();
        tiles_present_player2 = tileBag.getRack_player_2();
        swap_panel.setPanel(this);
        initBoard();
    }

    private void initBoard() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        setFocusable(true);
        button_ini();
        tile_rack_ini();
        options_ini();
    }
    public void button_ini(){
        tempBoard = new boolean[15][15];

        for (int i = 0; i <buttons.length ; i++) {
            for (int j = 0; j <buttons[0].length ; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].addActionListener(this);

                if (i == 7 & j == 7){
                    ImageIcon icon = new ImageIcon("src/imgs/Star.png");
                    Image image = icon.getImage().getScaledInstance(40,40, Image.SCALE_SMOOTH);
                    icon = new ImageIcon(image);
                    buttons[i][j].setIcon(icon);
                    buttons[i][j].setBorder(new RoundedButton(10));
                    buttons[i][j].setBackground(new Color(255,255,255));
                    tempBoard[i][j] = true;
            }
                else if (ref_board[i][j].speciality == 1){
                    ImageIcon icon = new ImageIcon("src/imgs/DL.png");
                    Image image = icon.getImage().getScaledInstance(30,30, Image.SCALE_SMOOTH);
                    icon = new ImageIcon(image);
                    buttons[i][j].setIcon(icon);
                    buttons[i][j].setBorder(new RoundedButton(10));
                    buttons[i][j].setBackground(new Color(0,104,102));
                    tempBoard[i][j] = true;
                }
                else if (ref_board[i][j].speciality == 2){
                    ImageIcon icon = new ImageIcon("src/imgs/TL.png");
                    Image image = icon.getImage().getScaledInstance(30,30, Image.SCALE_SMOOTH);
                    icon = new ImageIcon(image);
                    buttons[i][j].setIcon(icon);
                    buttons[i][j].setBorder(new RoundedButton(10));
                    buttons[i][j].setBackground(new Color(238,215,161));
                    tempBoard[i][j] = true;
                }
                else if (ref_board[i][j].speciality == 3){
                    ImageIcon icon = new ImageIcon("src/imgs/DW.png");
                    Image image = icon.getImage().getScaledInstance(30,30, Image.SCALE_SMOOTH);
                    icon = new ImageIcon(image);
                    buttons[i][j].setIcon(icon);
                    buttons[i][j].setBorder(new RoundedButton(10));
                    buttons[i][j].setBackground(new Color(161,207,203));
                    tempBoard[i][j] = true;
                }
                else if (ref_board[i][j].speciality == 4){
                    ImageIcon icon = new ImageIcon("src/imgs/TW.png");
                    Image image = icon.getImage().getScaledInstance(30,30, Image.SCALE_SMOOTH);
                    icon = new ImageIcon(image);
                    buttons[i][j].setIcon(icon);
                    buttons[i][j].setBorder(new RoundedButton(10));
                    buttons[i][j].setBackground(new Color(255,155,155));
                    tempBoard[i][j] = true;
                }
                else {
                    buttons[i][j].setBorder(new RoundedButton(10));
                    buttons[i][j].setBackground(new Color(204,204,204));
                }
                this.add(buttons[i][j]);
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D graphics2D = (Graphics2D)g;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        the_grid(50,50,40,40,graphics2D);
        tile_grid_player_1(720,50,40,40, graphics2D);
        tile_grid_player_2(720,500,40,40, graphics2D);
        options_grid(680,600,100,100,graphics2D);
        draw_remaining(680,250,graphics2D);
    }

    @Override
    public void paintComponents(Graphics g) {
    }
    public void the_grid(int x_p,int y_p,int width, int height,Graphics2D g){
        int x = x_p;
        int y = y_p;
        for (int i = 1; i <=15 ; i++) {
            for (int j = 1; j <=15; j++) {
                buttons[i-1][j-1].setBounds(x,y,width,height);

                x = x + width;
            }
            x = x_p;
            y = y+height;
        }
        g.setColor(Color.BLACK);
        Font font = new Font("Times New Roman",Font.BOLD,20);
        g.setFont(font);
        x = x_p - (width/2 + 10);
        y = y_p + height/2;
        for (int i = 1; i <=15; i++) {
//            g.drawString(String.valueOf(i-1),x,y);
            g.drawString(String.valueOf(i),x,y);
            y = y+ height;
        }
        x = x_p + (width/2-10);
        y = y_p - height/2;
        for (int i = 1; i <=15 ; i++) {

//            g.drawString(String.valueOf(i-1),x,y);
            g.drawString(String.valueOf((char)(64 + i)),x,y);
            x = x + width;
        }
    }



    private void tile_rack_ini(){
        for (int i = 0; i <shuffle.length ; i++) {
            ImageIcon icon = new ImageIcon("src/imgs/Shuffle.png");
            Image image = icon.getImage().getScaledInstance(40,40, Image.SCALE_SMOOTH);
            icon = new ImageIcon(image);
            shuffle[i] = new JButton(icon);
            shuffle[i].addActionListener(this);
            shuffle[i].setBorder(new RoundedButton(10));
            shuffle[i].setBackground(new Color(204,204,204));
            this.add(shuffle[i]);
        }
        for (int i = 0; i <tile_rack_player_1.length ; i++) {
            ImageIcon icon = new ImageIcon("src/imgs/" + String.valueOf(tiles_present_player1.get(i).letter).toUpperCase() + ".png");
            Image image = icon.getImage().getScaledInstance(40,40, Image.SCALE_SMOOTH);
            icon = new ImageIcon(image);
            tile_rack_player_1[i] = new JButton(icon);
            tile_rack_player_1[i].addActionListener(this);
            tile_rack_player_1[i].setBorder(new RoundedButton(10));
            tile_rack_player_1[i].setBackground(new Color(242,191,118));
            this.add(tile_rack_player_1[i]);
            icon = new ImageIcon("src/imgs/" + String.valueOf(tiles_present_player2.get(i).letter).toUpperCase() + ".png");
            image = icon.getImage().getScaledInstance(40,40, Image.SCALE_SMOOTH);
            icon = new ImageIcon(image);
            tile_rack_player_2[i] = new JButton(icon);
            tile_rack_player_2[i].addActionListener(this);
            tile_rack_player_2[i].setBorder(new RoundedButton(10));
            tile_rack_player_2[i].setBackground(new Color(242,191,118));
            this.add(tile_rack_player_2[i]);
            }
    }
    public void tile_rack_rearrange() {
        if (player == 1) {
            for (int i = 0; i <tile_rack_player_1.length ; i++) {
                if (i<tiles_present_player1.size()){
                    ImageIcon icon = new ImageIcon("src/imgs/" + String.valueOf(tiles_present_player1.get(i).letter).toUpperCase() + ".png");
                    Image image = icon.getImage().getScaledInstance(40,40,Image.SCALE_SMOOTH);
                    icon = new ImageIcon(image);
                    tile_rack_player_1[i].setIcon(icon);
                }
                else {
                    ImageIcon icon = new ImageIcon("src/imgs/Empty.png");
                    Image image = icon.getImage().getScaledInstance(40,40,Image.SCALE_SMOOTH);
                    icon = new ImageIcon(image);
                    tile_rack_player_1[i].setIcon(icon);
                }
            }
        }
        else {
            for (int i = 0; i <tile_rack_player_2.length ; i++) {
                if (i<tiles_present_player2.size()){
                    ImageIcon icon = new ImageIcon("src/imgs/" + String.valueOf(tiles_present_player2.get(i).letter).toUpperCase() + ".png");
                    Image image = icon.getImage().getScaledInstance(40,40,Image.SCALE_SMOOTH);
                    icon = new ImageIcon(image);
                    tile_rack_player_2[i].setIcon(icon);
                }
                else {
                    ImageIcon icon = new ImageIcon("src/imgs/Empty.png");
                    Image image = icon.getImage().getScaledInstance(40,40,Image.SCALE_SMOOTH);
                    icon = new ImageIcon(image);
                    tile_rack_player_2[i].setIcon(icon);
                }
            }
        }
    }
    private void options_ini(){

        for (int i = 0; i <options_buttons.length ; i++) {
            options_buttons[i] = new JButton();
            options_buttons[i].addActionListener(this);
            this.add(options_buttons[i]);
            if (i == 0){
                ImageIcon icon = new ImageIcon("src/imgs/Reset.png");
                Image image = icon.getImage().getScaledInstance(100,100, Image.SCALE_SMOOTH);
                icon = new ImageIcon(image);
                options_buttons[i].setIcon(icon);
                options_buttons[i].setBorder(new RoundedButton(10));
                options_buttons[i].setBackground(new Color(204,204,204));

            }
            else if (i == 1) {
                ImageIcon icon = new ImageIcon("src/imgs/AI.png");
                Image image = icon.getImage().getScaledInstance(100,100, Image.SCALE_SMOOTH);
                icon = new ImageIcon(image);
                options_buttons[i].setIcon(icon);
                options_buttons[i].setBorder(new RoundedButton(10));
                options_buttons[i].setBackground(new Color(255,188,218));
        }
            else if (i == 2) {
                ImageIcon icon = new ImageIcon("src/imgs/Skip.png");
                Image image = icon.getImage().getScaledInstance(100,100, Image.SCALE_SMOOTH);
                icon = new ImageIcon(image);
                options_buttons[i].setIcon(icon);
                options_buttons[i].setBorder(new RoundedButton(10));
                options_buttons[i].setBackground(new Color(238,215,161));
        }
            else if (i == 3){
                ImageIcon icon = new ImageIcon("src/imgs/Submit.png");
                Image image = icon.getImage().getScaledInstance(100,100, Image.SCALE_SMOOTH);
                icon = new ImageIcon(image);
                options_buttons[i].setIcon(icon);
                options_buttons[i].setBorder(new RoundedButton(10));
                options_buttons[i].setBackground(new Color(144,238,144));
            }
            else if (i == 4){
                ImageIcon icon = new ImageIcon("src/imgs/Swap.png");
                Image image = icon.getImage().getScaledInstance(100,100, Image.SCALE_SMOOTH);
                icon = new ImageIcon(image);
                options_buttons[i].setIcon(icon);
                options_buttons[i].setBorder(new RoundedButton(10));
                options_buttons[i].setBackground(new Color(65,105,225));
            }
            else if (i == 5){
                ImageIcon icon = new ImageIcon("src/imgs/Resign.png");
                Image image = icon.getImage().getScaledInstance(90,90, Image.SCALE_SMOOTH);
                icon = new ImageIcon(image);
                options_buttons[i].setIcon(icon);
                options_buttons[i].setBorder(new RoundedButton(10));
                options_buttons[i].setBackground(new Color(235,45,58));
            }

        }
    }

    private void options_grid(int x_p, int y_p, int width, int height, Graphics2D g){
        int x = x_p;
        for (int i = 0; i <  options_buttons.length; i++) {
            options_buttons[i].setBounds(x,y_p,width,height);
            x = x+width;
        }
    }
    private void draw_remaining(int x, int y, Graphics2D graphics2D){
        int x_p = x;
        int y_p = y;
        graphics2D.drawRoundRect(x-10,y-80,550,250,25,25);
        graphics2D.setFont(new Font("Herona",Font.PLAIN, 25));
        graphics2D.drawString("Remaining Tiles:",x, y-50);

        graphics2D.setFont(new Font("Herona",Font.PLAIN, 20));
        for (int i = 1; i <=tileBag.getRemaining().length ; i++) {
            graphics2D.drawString(String.valueOf((char)(65+(i-1))).toUpperCase(Locale.ROOT) + " x " + tileBag.getRemaining()[i-1],x_p,y_p);
            x_p = x_p + 80;
            if (i%7 == 0) {
                x_p = x;
                y_p = y_p + 50;
        }
        }
        graphics2D.setFont(null);
    }
    private void tile_grid_player_1(int x_p, int y_p, int width, int height, Graphics2D g){
        g.drawString("Player 1", x_p+100,y_p-10);
        if (player == 1)
            g.drawString("(Your turn)", x_p + 180,y_p-10);
        shuffle[0].setBounds(x_p-50,y_p,40,40);
        g.drawString("Score = " + String.valueOf(Player_score_1),x_p+300 ,y_p + width/2+10);
        int x =x_p;
        for (int i = 0; i <tile_rack_player_1.length ; i++) {
            tile_rack_player_1[i].setBounds(x,y_p,width,height);
            x = x + width;
        }
    }
    private void tile_grid_player_2(int x_p, int y_p, int width, int height, Graphics2D g){

        g.drawString("Player 2", x_p+100,y_p-10);
        shuffle[1].setBounds(x_p-50,y_p,40,40);
        if (player == 2)
            g.drawString("(Your turn)", x_p + 180,y_p-10);
        g.drawString("Score = " + String.valueOf(Player_score_2),x_p+300 ,y_p + width/2+10);
        int x =x_p;
        for (int i = 0; i <tile_rack_player_2.length ; i++) {
            tile_rack_player_2[i].setBounds(x,y_p,width,height);
            x = x + width;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
            for (int i = 0; i < buttons.length; i++) {
                for (int j = 0; j <buttons[0].length ; j++) {
                    if (e.getSource() == buttons[i][j]){
                        current_tile_selected.clear();
                        current_tile_selected.add(i);
                        current_tile_selected.add(j);
                    //System.out.println(i + " " + j);
                    }
                }
            }
            for (int i = 0; i <tiles_present_player1.size() ; i++) {
                if (e.getSource() == tile_rack_player_1[i] && player == 1) {
                    if (swap_active && swap_panel.getCurrent_selected_array() == tiles_present_player1) {
                        swap_panel.swap_selections.add(tiles_present_player1.remove(i));
                        tile_rack_rearrange();
                        swap_panel.swap_button_rearrange();
                    } else if (!swap_active) {
                        current_letter_selected = tiles_present_player1.get(i);
                        tile_setter(i);
                    }
                }
            }
        for (int i = 0; i <tiles_present_player2.size(); i++) {
            if (e.getSource() == tile_rack_player_2[i] && player==2){
                    if (swap_active && swap_panel.getCurrent_selected_array() == tiles_present_player2){
                        swap_panel.swap_selections.add(tiles_present_player2.remove(i));
                        tile_rack_rearrange();
                        swap_panel.swap_button_rearrange();
                    }
                    else if (!swap_active){
                        current_letter_selected = tiles_present_player2.get(i);
                        tile_setter(i);
                    }
                }
            }
            if (e.getSource() == options_buttons[0] && !swap_active){
                Reset_Tiles(player);
                System.out.println("Reset");
            }
            else if (e.getSource() == options_buttons[1] && !swap_active){
                Reset_Tiles(player);
                System.out.println("AI MOVE");

                if(player == 1) board.setAiRack(tileBag.getRack_player_1());
                else board.setAiRack(tileBag.getRack_player_2());

                
                if(!gameEnd) {

                    board.AI_TEST();
                    board.transAI_TEST();
                    board.AI.writeToFile();
                    board.readData("horiMoves.txt", true);
                    board.readData("vertiMoves.txt", false);
                    board.AI_placeWord(player);
                    AI_tileSetter();
                    update_score(player);
                    tileBag.rack_update(player);
                    tile_rack_rearrange();

                    if(tileBag.getTiles().size() == 0 && (board.potentialMove == null || (tiles_present_player1.size() == 0 || tiles_present_player2.size() == 0))) {
                        gameEnd = true;
                        if(tiles_present_player1.size() == 0 && tiles_present_player2.size() != 0) {
                            for(Tile t : tiles_present_player2) {
                                board.p1Score += t.value;
                            }
                        }

                        if(tiles_present_player2.size() == 0 && tiles_present_player1.size() != 0) {
                            for(Tile t : tiles_present_player1) {
                                board.p2Score += t.value;
                            }
                        }

                        if(tiles_present_player1.size() != 0 && tiles_present_player2.size() != 0) {
                            
                            for(Tile t : tiles_present_player1) {
                                board.p1Score -= t.value;
                            }

                            for(Tile t : tiles_present_player2) {
                                board.p2Score -= t.value;
                            }
                        }

                        board.stuff += "GAME ENDED"+"\n----------\nFINAL SCORE: \nPlayer1: "+board.p1Score+"\nPlayer2: "+board.p2Score+"\n";
                        //if(board.p1Score > board.p2Score)
                        tileBag.getTiles().addAll(tiles_present_player1);
                        tileBag.getTiles().addAll(tiles_present_player2);
                        tiles_present_player2.clear();
                        tiles_present_player1.clear();
                        tile_rack_rearrange();
                        refresh();

                    }
                    board.writeData();
                }

                //System.out.println("HIGHEST SCORE: "+board.potentialMove.score);

                // for(int i : board.potentialMove.positions) {
                //     System.out.print(i+" ");
                // }
                // System.out.println();

                // for(Tile t : board.potentialMove.tiles) {
                //     System.out.print(t.letter+" ");
                // }

                board.potentialMove = null;
                board.AI.legalMoves.clear();
                board.AI.legalMoves_Trans.clear();
                board.AI.hor_positions.clear();
                board.AI.ver_positions.clear();
                tileBag.remaining_tiles();
                SwingUtilities.updateComponentTreeUI(this);
                change_turn();

            }
            else if (e.getSource() == options_buttons[2] && !swap_active){
                System.out.println("Skip");
                Reset_Tiles(player);
                change_turn();
                refresh();
            }
            else if (e.getSource() == options_buttons[3] && !swap_active){

                if(!gameEnd) {
                    ArrayList<Integer> pos = new ArrayList<>();
                    for (int i = 0; i < temp_positions.size(); i++) {
                        //(row * length_of_row) + column
                        pos.add((temp_positions.get(i)[0] * 15 ) + temp_positions.get(i)[1] + 1);
                    }

                    if(board.placeWord(tiles_selected_from_rack, pos, player)) {
                        update_score(player);
                        tileBag.rack_update(player);
                        tile_rack_rearrange();
                        change_turn();
                        board.writeData();
                        //System.out.println(board.p1Score);
                        //System.out.println(Player_score_1);
                        //System.out.println("Normal");
                        //board.getAnchorCount();
                        //System.out.println();
                        //System.out.println("Transposed");
                        //board.getAnchorCountTransposed();
                        //board.crossCheckTest();
                        SwingUtilities.updateComponentTreeUI(this);
                    }
                    else {
                        Reset_Tiles(player);
                        pos.clear();
                    }
                }
                //board.displayBoard();
                //System.out.println();
                //board.displayBoardTransposed();
        }
            else if (e.getSource() == options_buttons[4] && !swap_active) {
                Reset_Tiles(player);
                System.out.println("Swap");
                swap_active = true;
                System.out.println(player);
                if (player == 1) {
                    swap_panel.setCurrent_selected_array(getTiles_present_player1());
                } else{
                    swap_panel.setCurrent_selected_array(getTiles_present_player2());
            }
                Swap_Frame swap_frame = new Swap_Frame(swap_panel);
            }
            else if (e.getSource() == options_buttons[5] && !swap_active) {
                Reset_Tiles(player);
                System.out.println("Resign");
                gameEnd = true;
                        if(tiles_present_player1.size() == 0 && tiles_present_player2.size() != 0) {
                            for(Tile t : tiles_present_player2) {
                                board.p1Score += t.value;
                            }
                        }

                        if(tiles_present_player2.size() == 0 && tiles_present_player1.size() != 0) {
                            for(Tile t : tiles_present_player1) {
                                board.p2Score += t.value;
                            }
                        }

                        if(tiles_present_player1.size() != 0 && tiles_present_player2.size() != 0) {
                            
                            for(Tile t : tiles_present_player1) {
                                board.p1Score -= t.value;
                            }

                            for(Tile t : tiles_present_player2) {
                                board.p2Score -= t.value;
                            }
                        }
                        board.stuff += "GAME ENDED"+"\n----------\nFINAL SCORE: \nPlayer1: "+board.p1Score+"\nPlayer2: "+board.p2Score+"\n";
                        board.writeData();


                tileBag.getTiles().addAll(tiles_present_player1);
                tileBag.getTiles().addAll(tiles_present_player2);
                tiles_present_player2.clear();
                tiles_present_player1.clear();
                tile_rack_rearrange();
                refresh();
            }
            else if (e.getSource() == shuffle[0] && player == 1){
                Collections.shuffle(tiles_present_player1);
                tile_rack_rearrange();
                refresh();
            }
            else if (e.getSource() == shuffle[1] && player == 2){
                Collections.shuffle(tiles_present_player2);
                tile_rack_rearrange();
                refresh();
            }

    }
    public void update_score(int player){
        if (player == 1) {
            Player_score_1 = board.p1Score;
    }
        else
            Player_score_2 = board.p2Score;
    }
    public void placeWordMessage() {

        ArrayList<Integer> pos = new ArrayList<>();
        for (int i = 0; i < temp_positions.size(); i++) {
            //(row * length_of_row) + column
            pos.add((temp_positions.get(i)[0] * 15 ) + temp_positions.get(i)[1]);
        }
        if(board.placeWord(tiles_selected_from_rack, pos, player)) {
            tileBag.rack_update(player);
            change_turn();
            //board.getAnchorCount();
            //board.crossCheckTest();
        }


    }


    private void tile_setter(int i){
        if (current_letter_selected != null && !current_tile_selected.isEmpty()) {
            if (!IsFreeTile(current_tile_selected.get(0), current_tile_selected.get(1))) {
                current_tile_selected.clear();
                current_letter_selected = null;
                return;
            }

                ImageIcon icon = new ImageIcon("src/imgs/" + String.valueOf(current_letter_selected.letter) +".png");
            Image image = icon.getImage().getScaledInstance(40,40, Image.SCALE_SMOOTH);
            icon = new ImageIcon(image);
            buttons[current_tile_selected.get(0)][current_tile_selected.get(1)].setIcon(icon);


            if (player == 1) {
                tiles_present_player1.remove(current_letter_selected);
                tiles_selected_from_rack.add(current_letter_selected);
            }
            else
            {
                tiles_present_player2.remove(current_letter_selected);
                tiles_selected_from_rack.add(current_letter_selected);
            }
            tile_rack_rearrange();
            int[] rc = {current_tile_selected.get(0),current_tile_selected.get(1)};
            if (!IsSpecialTile(current_tile_selected.get(0), current_tile_selected.get(1))){
                buttons[current_tile_selected.get(0)][current_tile_selected.get(1)].setBackground(new Color(242,191,118));
            }
            temp_positions.add(rc);
            current_letter_selected = null;
            current_tile_selected.clear();
            //System.out.println(tiles_selected_from_rack.toString());
        }
    }

    public void AI_tileSetter() {

        for(int r = 0; r < 15; r++) {
            for(int c = 0; c < 15; c++) {

                if(ref_board[r][c].isOccupied) {

                    int h = 40;
                    int w = 40;
                    if (!tempBoard[r][c]) buttons[r][c].setBackground(new Color(242,191,118));
                    
                    ImageIcon icon = new ImageIcon("src/imgs/" + String.valueOf(ref_board[r][c].tile.letter) +".png");
                    Image image = icon.getImage().getScaledInstance(w,h, Image.SCALE_SMOOTH);
                    icon = new ImageIcon(image);
                    buttons[r][c].setIcon(icon);
                }
            }
        }
    }

    private void Reset_Tiles(int player){
        if (player == 1){
            tiles_present_player1.addAll(tiles_selected_from_rack);
        }
        else{
            tiles_present_player2.addAll(tiles_selected_from_rack);
            }
        for (int i = 0; i <temp_positions.size() ; i++) {
            int x = temp_positions.get(i)[0];
            int y = temp_positions.get(i)[1];
            if (x == 7 & y == 7){
                ImageIcon icon = new ImageIcon("src/imgs/Star.png");
                Image image = icon.getImage().getScaledInstance(30,30, Image.SCALE_SMOOTH);
                icon = new ImageIcon(image);
                buttons[x][y].setIcon(icon);
                buttons[x][y].setBorder(new RoundedButton(10));
                buttons[x][y].setBackground(new Color(255,255,255));

            }
            else if (ref_board[x][y].speciality == 1){
                ImageIcon icon = new ImageIcon("src/imgs/DL.png");
                Image image = icon.getImage().getScaledInstance(30,30, Image.SCALE_SMOOTH);
                icon = new ImageIcon(image);
                buttons[x][y].setIcon(icon);
                buttons[x][y].setBorder(new RoundedButton(10));
                buttons[x][y].setBackground(new Color(0,104,102));
            }
            else if (ref_board[x][y].speciality == 2){
                ImageIcon icon = new ImageIcon("src/imgs/TL.png");
                Image image = icon.getImage().getScaledInstance(30,30, Image.SCALE_SMOOTH);
                icon = new ImageIcon(image);
                buttons[x][y].setIcon(icon);
                buttons[x][y].setBorder(new RoundedButton(10));
                buttons[x][y].setBackground(new Color(238,215,161));
            }
            else if (ref_board[x][y].speciality == 3){
                ImageIcon icon = new ImageIcon("src/imgs/DW.png");
                Image image = icon.getImage().getScaledInstance(30,30, Image.SCALE_SMOOTH);
                icon = new ImageIcon(image);
                buttons[x][y].setIcon(icon);
                buttons[x][y].setBorder(new RoundedButton(10));
                buttons[x][y].setBackground(new Color(161,207,203));
            }
            else if (ref_board[x][y].speciality == 4){
                ImageIcon icon = new ImageIcon("src/imgs/TW.png");
                Image image = icon.getImage().getScaledInstance(30,30, Image.SCALE_SMOOTH);
                icon = new ImageIcon(image);
                buttons[x][y].setIcon(icon);
                buttons[x][y].setBorder(new RoundedButton(10));
                buttons[x][y].setBackground(new Color(255,155,155));
            }
            else {
                buttons[x][y].setIcon(null);
                buttons[x][y].setBorder(new RoundedButton(10));
                buttons[x][y].setBackground(new Color(204,204,204));
            }
        }
        temp_positions.clear();
        tile_rack_rearrange();
        tiles_selected_from_rack.clear();
    }
    private boolean IsFreeTile(int i, int j){
        return !ref_board[i][j].isOccupied;
    }
    private boolean IsSpecialTile(int i, int j){
        return ref_board[i][j].speciality == 1 || ref_board[i][j].speciality == 2 || ref_board[i][j].speciality == 3 || ref_board[i][j].speciality == 4;
    }
    public void refresh(){
        SwingUtilities.updateComponentTreeUI(this);
    }

    public void change_turn() {
        if (player == 1){
            player = 2;
        }

        else{
            player = 1;
        }

        temp_positions.clear();
        tiles_selected_from_rack.clear();
        current_letter_selected = null;
        current_tile_selected.clear();
    }

    public ArrayList<Tile> getTiles_present_player1() {
        return tiles_present_player1;
    }

    public ArrayList<Tile> getTiles_present_player2() {
        return tiles_present_player2;
    }
}
