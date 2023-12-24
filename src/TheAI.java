import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class TheAI {

    ArrayList<Tile> myRack;
    ArrayList<Word> legalMoves = new ArrayList<>();
    ArrayList<Word> legalMoves_Trans = new ArrayList<>();
    ArrayList<ArrayList<Integer>> hor_positions = new ArrayList<>();
    ArrayList<ArrayList<Integer>> ver_positions = new ArrayList<>();

    int aR;
    int aC;
    BoardCell[][] board;
    boolean normalCheck;

    public void cal_hor_positions(){

        for (int i = 0; i <legalMoves.size() ; i++) {
            
            ArrayList<Integer> pos = new ArrayList<>();

            int c = legalMoves.get(i).c;
            int r = legalMoves.get(i).r;

            for (int j = legalMoves.get(i).word.length() - 1; j >=0 ; j--) {
                pos.add(encryptPostion(r,c-j));
            }
            hor_positions.add(pos);
            

        }

    }

    public void cal_ver_positions() {

        for (int i = 0; i <legalMoves_Trans.size() ; i++) {

            ArrayList<Integer> pos = new ArrayList<>();

            int r = legalMoves_Trans.get(i).c;
            int c = 15 - legalMoves_Trans.get(i).r - 1;

            
            for (int j = legalMoves_Trans.get(i).word.length() - 1; j >=0 ; j--) {
                pos.add(encryptPostion(r-j,c));
            }
            ver_positions.add(pos);
            

        }

    }

    public void writeToFile() {

        cal_hor_positions();
        cal_ver_positions();
        System.out.println(hor_positions.size());
        System.out.println(ver_positions.size());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("horiMoves.txt"))) {
            // Iterate through the ArrayList and write each element to the file
            for (int i = 0; i < legalMoves.size(); i++) {
                writer.write(legalMoves.get(i).word+"\n");
                //writer.newLine(); // Add a newline character to separate lines
                for(int pos : hor_positions.get(i)) {
                    writer.write(pos+" ");
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("vertiMoves.txt"))) {
            // Iterate through the ArrayList and write each element to the file
            for (int i = 0; i < legalMoves_Trans.size(); i++) {
                writer.write(legalMoves_Trans.get(i).word+"\n");
                //writer.newLine(); // Add a newline character to separate lines
                for(int pos : ver_positions.get(i)) {
                    writer.write(pos+" ");
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }



    public void setMyRack(ArrayList<Tile> myRack) {

        this.myRack = myRack;
    }

    public  int encryptPostion(int r, int c) {

        return (r * 15) + c + 1;

    }

    public void setNormalCheck(boolean normalCheck) {
        this.normalCheck = normalCheck;
    }

    public void setRC(int r , int c) {
        aR = r;
        aC = c;
    }


    public TheAI(BoardCell[][] b) {
        board = b;
        
    }

    public void setBoard(BoardCell[][] board) {
        this.board = board;
    }



    public void extendRight(String partialWord, TrieNode N, int r, int c) {

        if(c <= 14) {
            if(!board[r][c].isOccupied) {

                if(N != null && N.isEnd && c != aC) {
                    if(normalCheck) legalMoves.add(new Word(partialWord, r, c - 1));
                    else legalMoves_Trans.add(new Word(partialWord, r, c - 1));
                }

                for(int i = 0; i < 26; i++) {

                    Tile l = null;

                    for(Tile t : myRack) {

                        if(N.childs[i] != null && t.letter == (char)(i + 65) && board[r][c].crossChecks[i]) {

                            l = t;
                            break;

                        }

                    }

                    if(l != null) {
                        myRack.remove(l);
                        extendRight(partialWord + (char)(i + 65), N.childs[i], r, c + 1);
                        myRack.add(l);
                    }

                }

            }

            else {

                char l = board[r][c].tile.letter;

                for(int i = 0; i < 26; i++) {

                    if(N.childs[i] != null && (char)(i + 65) == l) {

                        extendRight(partialWord + (char)(i + 65), N.childs[i], r, c + 1);

                    }

                }



            }
        }


    } 

    public void leftPart(String partialWord, TrieNode N, int limit) {

        extendRight(partialWord, N, aR, aC);
        if(limit > 0) {

            for(int i = 0; i < 26; i++) {

                Tile l = null;

                for(Tile t : myRack) {

                    if(N.childs[i] != null && t.letter == (char)(i + 65) ) {

                        l = t;
                        break;

                    }

                }

                if(l != null) {
                    myRack.remove(l);
                    leftPart(partialWord + (char)(i + 65), N.childs[i], limit - 1);
                    myRack.add(l);
                }

            }

        }

    }

    
}

class Word {
    int r;
    int c;
    String word;

    Word(String w, int r1, int c1) {
        word = w;
        r = r1;
        c = c1;
    }
}
