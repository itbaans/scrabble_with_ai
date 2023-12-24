import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class TileBag {

    private ArrayList<Tile> tiles = new ArrayList<Tile>();
    private ArrayList<Tile> rack_player_1 = new ArrayList<>();
    private ArrayList<Tile> rack_player_2 = new ArrayList<>();

    private int[] remaining = new int[26];
    public TileBag() {
        //generation of tiles in a descending order based on value
        //however a random generation will be applied so it evens out.
        tiles.add(new Tile('Q'));
        tiles.add(new Tile('Z'));
        tiles.add(new Tile('J'));
        tiles.add(new Tile('X'));
        tiles.add(new Tile('K'));
        for (int i = 0; i <2 ; i++) {
            //tiles.add(new Tile('-'));
            tiles.add(new Tile('F'));
            tiles.add(new Tile('H'));
            tiles.add(new Tile('V'));
            tiles.add(new Tile('W'));
            tiles.add(new Tile('Y'));
            tiles.add(new Tile('B'));
            tiles.add(new Tile('C'));
            tiles.add(new Tile('M'));
            tiles.add(new Tile('P'));
        }
        for (int i = 0; i <3 ; i++) {
            tiles.add(new Tile('G'));
        }
        for (int i = 0; i <4 ; i++) {
            tiles.add(new Tile('D'));
            tiles.add(new Tile('L'));
            tiles.add(new Tile('S'));
            tiles.add(new Tile('U'));
        }
        for (int i = 0; i <6 ; i++) {
            tiles.add(new Tile('N'));
            tiles.add(new Tile('R'));
            tiles.add(new Tile('T'));
        }
        for (int i = 0; i <8 ; i++) {
            tiles.add(new Tile('O'));
        }
        for (int i = 0; i <9 ; i++) {
            tiles.add(new Tile('A'));
            tiles.add(new Tile('I'));
        }
        for (int i = 0; i <12 ; i++) {
            tiles.add(new Tile('E'));
        }
        rack_update(1);
        rack_update(2);
    }

    public void rack_update(int player) {
        //use this to update the rack after every move, it will automatically fill all
        //IN FINAL SELECTION MAKE SURE TO REMOVE FROM RACK
        if(player == 1) {
            for (int i = rack_player_1.size(); i <7 ; i++) {
                if (tiles.size()!=0){
                    rack_player_1.add(tiles.remove((int)(Math.random()*tiles.size())));
                }
                else
                    return;
            }
        }

        if(player == 2) {
            for (int i = rack_player_2.size(); i <7 ; i++) {
                if (tiles.size()!=0){
                    rack_player_2.add(tiles.remove((int)(Math.random()*tiles.size())));
                }
                else
                    return;
            }
        }
        remaining_tiles();
    }
    public void remaining_tiles(){
        Arrays.fill(remaining, 0);
        for (int i = 0; i < tiles.size() ; i++) {{
            remaining[tiles.get(i).letter - 65]++;
        }
        }
    }

    public ArrayList<Tile> getRack_player_1() {
        return rack_player_1;
    }

    public ArrayList<Tile> getRack_player_2() {
        return rack_player_2;
    }

    public ArrayList<Tile> getTiles() {
        return tiles;
    }

    public void displayTileRack(int player) {

        if(player == 1) {
            for (Tile t : rack_player_1) {
                System.out.print(t.letter+" ");
            }
        }
        if(player == 2) {
            for (Tile t : rack_player_2) {
                System.out.print(t.letter+" ");
            }
        }
        System.out.println();

    }

    public int[] getRemaining() {
        return remaining;
    }

    public boolean isContains(char c, int player) {

        if((int)c > 96 && (int)c < 123 ) {
            c = (char)(c - 32);
        }

        if(player == 1) {
            for (Tile t : rack_player_1) {
                if(t.letter == c) {
                    rack_player_1.remove(t);
                    return true;
                }
            }
        }
        if(player == 2) {
            for (Tile t : rack_player_2) {
                if(t.letter == c) {
                    rack_player_2.remove(t);
                    return true;
                }
            }
        }
        return false;
    }


    
}
