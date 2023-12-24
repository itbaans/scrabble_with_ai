import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Board {

    public BoardCell[][] getTheBoard() {
        return theBoard;
    }

    private BoardCell[][] theBoard = new BoardCell[15][15];
    private BoardCell[][] transposedBoard;
    ArrayList<int[]> occupiedCells = new ArrayList<>();
    MyTrie dictionary = new MyTrie("src\\Dictionaries\\words.txt");
    ArrayList<ArrayList<Integer>> player1moves = new ArrayList<>();
    ArrayList<ArrayList<Integer>> player2moves = new ArrayList<>();
    int p1Score = 0;
    int p2Score = 0;
    int AI_score = 0;
    ArrayList<Tile> aiRack;
    TheAI AI;
    Move potentialMove;
    String stuff = "";


    //normal cell = 0
    //dbl letter = 1;
    //triple letter = 2;
    //dbl word = 3;
    //triple word = 4;

    public void setAiRack(ArrayList<Tile> aiRack) {
        this.aiRack = aiRack;
    }
    public Board() {

        AI = new TheAI(theBoard);

        // Triple Word Score (TW) Squares
        int[] twRows = {0,0,0,7,7,14,14,14};
        int[] twCols = {0,7,14,0,14,0,7,14};

        // Double Word Score (DW) Squares
        int[] dwRows = {1,1,2,2,3,3,4,4,7,13,13,12,12,11,11,10,10};
        int[] dwCols = {1,13,2,12,3,11,4,10,7,1,13,2,12,3,11,4,10};

        // Triple Letter Score (TL) Squares
        int[] tlRows = {1,1,5,5,5,5,9,9,9,9,13,13};
        int[] tlCols = {5,9,1,5,9,13,1,5,9,13,5,9};

        // Double Letter Score (DL) Squares
        int[] dlRows = {0,0,2,2,3,3,3,6,6,6,6,7,7,8,8,8,8,11,11,11,12,12,14,14};
        int[] dlCols = {3,11,6,8,0,7,14,2,6,8,12,3,11,2,6,8,12,0,7,14,6,8,3,11};

        // Initialize the special squares in the 2D array
        for (int i = 0; i < twRows.length; i++) {
            theBoard[twRows[i]][twCols[i]] = new BoardCell(4); // Triple Word Score
        }

        for (int i = 0; i < dwRows.length; i++) {
            theBoard[dwRows[i]][dwCols[i]] = new BoardCell(3); // Double Word Score
        }

        for (int i = 0; i < tlRows.length; i++) {
            theBoard[tlRows[i]][tlCols[i]] = new BoardCell(2); // Triple Letter Score
        }

        for (int i = 0; i < dlRows.length; i++) {
            theBoard[dlRows[i]][dlCols[i]] = new BoardCell(1); // Double Letter Score
        }

        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                if(theBoard[row][col] == null) theBoard[row][col] = new BoardCell(0);
            }
        }

        try {
            transposedBoard  = transposeLeft(theBoard);
        }
        catch(CloneNotSupportedException e) {
            throw new InternalError(e);
        }

    }

    public void writeData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("moves.txt"))) {
            // Iterate through the ArrayList and write each element to the file
            writer.write(stuff);
        } catch (IOException e) {
            e.printStackTrace();
        }

        
    }

    public void AI_placeWord(int player) {

        if(potentialMove != null) {
            for(int i = 0; i < potentialMove.positions.size(); i++) {

            int r = decryptPosition(potentialMove.positions.get(i))[0];
            int c = decryptPosition(potentialMove.positions.get(i))[1];

            if(!theBoard[r][c].isOccupied) {

                for(Tile t : aiRack) {
                    if(t.letter == potentialMove.tiles.get(i).letter) {
                        aiRack.remove(t);
                        break;
                    }
                }

                theBoard[r][c].setTileOnCell(potentialMove.tiles.get(i));
                transposedBoard[getTransposedCoordinates(r,c,15,15)[0]][getTransposedCoordinates(r,c,15,15)[1]].setTileOnCell(potentialMove.tiles.get(i));
                if(theBoard[r][c].speciality != 0) theBoard[r][c].speciality = 0;

                occupiedCells.add(decryptPosition(potentialMove.positions.get(i)));
                addCellAnchors(decryptPosition(potentialMove.positions.get(i)));
            }

        }

            for(ArrayList<Integer> pos : potentialMove.everyPositions) {
                if(player == 2) player2moves.add(pos);
                else player1moves.add(pos);
            }

            stuff += "PLAYER: "+player+"\n";

            for(String s : potentialMove.everyWords) {
                
                stuff += s+"\n";

            }
            stuff += "SCORE ON THE MOVE: "+potentialMove.score+"\n----------------\n";

            for(int[] rc : occupiedCells) {
                updateAnchorSqrs(rc);
                updateAnchorSqrsTransposed(rc);
            }

            crossCheckUpdate();
            crossCheckUpdateTransposed();

            if(player == 2) p2Score += potentialMove.score;
            else p1Score += potentialMove.score;

        }

    }


    public boolean placeWord(ArrayList<Tile> tiles, ArrayList<Integer> positions, int player) {

        if(tiles.size() > 7) {
            System.out.println("exceeded max selection");
            return false;
        }

        //making sure in initial stage placement contains the middle sqr
        if(isEmpty()) {
            boolean check = false;
            for(int i : positions) {
                if( i != 0) {
                    if(decryptPosition(i)[0] == 7 && decryptPosition(i)[1] == 7) {
                        //System.out.println(i);
                        check = true;
                    }
                }
            }
            if(!check) {
                System.out.println("mid sqr check");
                return false;
            }
        }
        
        //making sure move is either vertical or horizental
        boolean rLock = true;
        boolean cLock = true;
        int r = decryptPosition(positions.get(0))[0];
        int c = decryptPosition(positions.get(0))[1];
        int lastPosition = 0;

        for (int i : positions) {
            if(i != 0) {
                if(decryptPosition(i)[0] != r) rLock = false;
                if(decryptPosition(i)[1] != c) cLock = false;
                lastPosition = i;
            }
        }

        if(!rLock && !cLock) {
            System.out.println("vertical/horizental check");
            return false;
        }

        if(rLock) {
            int cL = decryptPosition(lastPosition)[1];
            for(int i = c; i <= cL; i++) {
                if(!positions.contains(encryptPostion(r, i))) {
                    if(!theBoard[r][i].isOccupied) {
                        System.out.println("rLock gap");
                        return false;
                    }
                }
            }
        }
        if(cLock) {
            int rL = decryptPosition(lastPosition)[0];
            for(int i = r; i <= rL; i++) {
                if(!positions.contains(encryptPostion(i, c))) {
                    if(!theBoard[i][c].isOccupied) {
                        System.out.println("cLock gap");
                        return false;
                    }
                }
            }
        }

        //making sure the placement doesnot contain any occupied cells
        for(int i : positions) {
            if(i != 0) {
                int row = decryptPosition(i)[0];
                int col = decryptPosition(i)[1];
                if(theBoard[row][col].isOccupied) {
                System.out.println("occupancy check");
                    return false;
            }
            }
        }

        //making sure that placement contains an anjant tile
        boolean adjuncyCheck = false;
        for(int i : positions) {
            if(i != 0) {
                int row = decryptPosition(i)[0];
                int col = decryptPosition(i)[1];

                if(isAdjant(row, col)) {
                    adjuncyCheck = true;
                    break;
                }
            }
        }

        if(!adjuncyCheck && !isEmpty()) {
            System.out.println("adjancy check");
            return false;
        }

        //crosschecks
        if(rLock) {
            for(int i = 0; i < tiles.size(); i++) {
                int ro = decryptPosition(positions.get(i))[0];
                int co = decryptPosition(positions.get(i))[1];

                int ind = tiles.get(i).letter - 65;
                if(!theBoard[ro][co].crossChecks[ind]) {
                    System.out.println("rLock crossCheck");
                    return false;
                }        
            }

            if(tiles.size() == 1) moveSearchNormal(positions, tiles, player);
        }
        if(cLock) {
            for(int i = 0; i < tiles.size(); i++) {
                int ro = decryptPosition(positions.get(i))[0];
                int co = decryptPosition(positions.get(i))[1];

                int tR = getTransposedCoordinates(ro, co, 15, 15)[0];
                int tC = getTransposedCoordinates(ro, co, 15, 15)[1];

                int ind = tiles.get(i).letter - 65;
                if(!transposedBoard[tR][tC].crossChecks[ind]) {
                    System.out.println("cLock crossCheck");
                    return false;
                }        
            }
            if(tiles.size() == 1) moveSearchTransposed(positions, tiles, player);
        }

        //placing the word after all conditions met
        if(tiles.size() == 1) {

            int ro = decryptPosition(positions.get(0))[0];
            int co = decryptPosition(positions.get(0))[1]; 
            theBoard[ro][co].setTileOnCell(tiles.get(0));

            int tR = getTransposedCoordinates(ro, co, 15, 15)[0];
            int tC = getTransposedCoordinates(ro, co, 15, 15)[1];
            transposedBoard[tR][tC].setTileOnCell(tiles.get(0));

            occupiedCells.add(decryptPosition(positions.get(0)));
            addCellAnchors(decryptPosition(positions.get(0)));

            for(int[] rc : occupiedCells) {
                updateAnchorSqrs(rc);
                updateAnchorSqrsTransposed(rc);
            }

            crossCheckUpdate();
            crossCheckUpdateTransposed();
            return true;

        }

        if((rLock && moveSearchNormal(positions, tiles, player)) || (cLock && moveSearchTransposed(positions, tiles, player))) {

            
            return true;

        }


        return false;


    }

    private boolean isAdjant(int r, int c) {

        if(r > 0 && r < 14) {
            if(theBoard[r+1][c].isOccupied || theBoard[r-1][c].isOccupied) return true;
        }

        if(r == 0) {
            if(theBoard[r+1][c].isOccupied) return true;
        }

        if(r == 14) {
            if(theBoard[r-1][c].isOccupied) return true;
        }

        if(c > 0 && c < 14) {
            if(theBoard[r][c+1].isOccupied || theBoard[r][c-1].isOccupied) return true;
        }

        if(c == 0) {
            if(theBoard[r][c+1].isOccupied) return true;
        }

        if(c == 14) {
            if(theBoard[r][c-1].isOccupied) return true;
        }

        return false;

    }

    public void addCellAnchors(int[] rc) {

        int r = rc[0];
        int c = rc[1];

        int tR = getTransposedCoordinates(r, c, 15, 15)[0];
        int tC = getTransposedCoordinates(r, c, 15, 15)[1];

        if(r > 0 && r < 14) {
            if(!theBoard[r+1][c].isOccupied) {
                theBoard[r][c].anchors.add(encryptPostion(r+1, c));
            }
            if(!theBoard[r-1][c].isOccupied)
                theBoard[r][c].anchors.add(encryptPostion(r-1, c));
        }

        if(r == 0) {
            if(!theBoard[r+1][c].isOccupied) {
                theBoard[r][c].anchors.add(encryptPostion(r+1, c));
            }
        }

        if(r == 14) {
            if(!theBoard[r-1][c].isOccupied)
                theBoard[r][c].anchors.add(encryptPostion(r-1, c));
        }

        if(c > 0 && c < 14) {
            if(!theBoard[r][c+1].isOccupied) {
                theBoard[r][c].anchors.add(encryptPostion(r, c+1));
            }
            if(!theBoard[r][c-1].isOccupied)
                theBoard[r][c].anchors.add(encryptPostion(r, c-1));
        }

        if(c == 0) {
            if(!theBoard[r][c+1].isOccupied) {
                theBoard[r][c].anchors.add(encryptPostion(r, c+1));
            }
        }

        if(c == 14) {
            if(!theBoard[r][c-1].isOccupied)
                theBoard[r][c].anchors.add(encryptPostion(r, c-1));
        }



        if(tR > 0 && tR < 14) {
            if(!transposedBoard[tR+1][tC].isOccupied) {
                transposedBoard[tR][tC].anchors.add(encryptPostion(tR+1, tC));
            }
            if(!transposedBoard[tR-1][tC].isOccupied)
                transposedBoard[tR][tC].anchors.add(encryptPostion(tR-1, tC));
        }

        if(tR == 0) {
            if(!transposedBoard[tR+1][tC].isOccupied) {
                transposedBoard[tR][tC].anchors.add(encryptPostion(tR+1, tC));
            }
        }

        if(tR == 14) {
            if(!transposedBoard[tR-1][tC].isOccupied)
                transposedBoard[tR][tC].anchors.add(encryptPostion(tR-1, tC));
        }

        if(tC > 0 && tC < 14) {
            if(!transposedBoard[tR][tC+1].isOccupied) {
                transposedBoard[tR][tC].anchors.add(encryptPostion(tR, tC+1));
            }
            if(!transposedBoard[tR][tC-1].isOccupied)
                transposedBoard[tR][tC].anchors.add(encryptPostion(tR, tC-1));
        }

        if(tC == 0) {
            if(!transposedBoard[tR][tC+1].isOccupied) {
                transposedBoard[tR][tC].anchors.add(encryptPostion(tR, tC+1));
            }
        }

        if(tC == 14) {
            if(!transposedBoard[tR][tC-1].isOccupied)
                transposedBoard[tR][tC].anchors.add(encryptPostion(tR, tC-1));
        }



    }

    public void updateAnchorSqrs(int[] rc) {
       
        if(theBoard[rc[0]][rc[1]].anchors.size() > 0) {

            for(int i = 0; i < theBoard[rc[0]][rc[1]].anchors.size();) {
            
                int r = decryptPosition(theBoard[rc[0]][rc[1]].anchors.get(i))[0];
                int c = decryptPosition(theBoard[rc[0]][rc[1]].anchors.get(i))[1];

                if(theBoard[r][c].isOccupied) {
                    theBoard[rc[0]][rc[1]].anchors.remove(theBoard[rc[0]][rc[1]].anchors.get(i));
                    if(theBoard[rc[0]][rc[1]].anchors.size() == 0) break;
                }
                else i++;

            }
        }

    }

    public void updateAnchorSqrsTransposed(int[] rc) {

        int tR = getTransposedCoordinates(rc[0], rc[1], 15, 15)[0];
        int tC = getTransposedCoordinates(rc[0], rc[1], 15, 15)[1];

        if(transposedBoard[tR][tC].anchors.size() > 0) {

            for(int i = 0; i < transposedBoard[tR][tC].anchors.size();) {

                int r = decryptPosition(transposedBoard[tR][tC].anchors.get(i))[0];
                int c = decryptPosition(transposedBoard[tR][tC].anchors.get(i))[1];

                //r = getTransposedCoordinates(r, c, 15, 15)[0];
                //c = getTransposedCoordinates(r, c, 15, 15)[1];

                if(transposedBoard[r][c].isOccupied) {
                    transposedBoard[tR][tC].anchors.remove(transposedBoard[tR][tC].anchors.get(i));
                    if(transposedBoard[tR][tC].anchors.size() == 0) break;
                }
                else i++;

            }

        }

    }

    public void AI_TEST() {

        AI.setMyRack(aiRack);
        AI.setNormalCheck(true);
        
        if(isEmpty()) {

            AI.setRC(7, 7);
            AI.leftPart("", dictionary.getRoot(), 7);

        }

        else {
            for(int[] rc : occupiedCells) {

                for(int anc : theBoard[rc[0]][rc[1]].anchors) {

                    int r = decryptPosition(anc)[0];
                    int c = decryptPosition(anc)[1];

                    AI.setBoard(theBoard);
                    int i = c - 1;
                    int i2 = r + 1;
                    int i3 = r - 1;
                    int limit = -1;

                    //i < 0 || i2 > 14 || i3 < 0

                    if(i >= 0 && i2 <= 14 && i3 >= 0) {

                        while(!theBoard[r][i].isOccupied && !theBoard[i2][i].isOccupied && !theBoard[i3][i].isOccupied) {

                            limit++;
                            i--;
                            if(i < 0 || i2 > 14 || i3 < 0) {
                                limit++;
                                break;
                            }

                        }

                    }

                    AI.setRC(r, c);

                    if(c - 1 >= 0 && !theBoard[r][c - 1].isOccupied) AI.leftPart("", dictionary.getRoot(), limit);

                    if(c - 1 >= 0 && theBoard[r][c - 1].isOccupied) {
                        
                        String partialWord = "";

                        int m = c-1;
                        while(theBoard[r][m].isOccupied) {
                            partialWord = theBoard[r][m].tile.letter + partialWord;
                            m--;
                            if(m < 0) break;
                        }
                        //System.out.println(partialWord);
                        TrieNode n = dictionary.getChainNode(partialWord);
                        if(n != null)
                            AI.extendRight(partialWord, n, r, c);

                    }


                }


            }
        }

    }

    public void transAI_TEST() {

        AI.setNormalCheck(false);

        if(isEmpty()) {

            AI.setRC(7, 7);
            AI.leftPart("", dictionary.getRoot(), 7);

        }

        else {
            for(int[] rc : occupiedCells) {

            int tr = getTransposedCoordinates(rc[0], rc[1], 15, 15)[0];
            int tc = getTransposedCoordinates(rc[0], rc[1], 15, 15)[1];

                for (int anc : transposedBoard[tr][tc].anchors) {

                    int ar = decryptPosition(anc)[0];
                    int ac = decryptPosition(anc)[1];

                    AI.setBoard(transposedBoard);
                    int ti = ac - 1;
                    int ti2 = ar + 1;
                    int ti3 = ar - 1;
                    int tLimit = -1;

                    if(ti >= 0 && ti2 <= 14 && ti3 >= 0) {

                        while(!transposedBoard[ar][ti].isOccupied && !transposedBoard[ti2][ti].isOccupied && !transposedBoard[ti3][ti].isOccupied) {
                            //System.out.println("ye");
                            tLimit++;
                            ti--;
                            if(ti < 0 || ti2 > 14 || ti3 < 0) {
                                tLimit++;
                                break;
                            }

                        }
                    }

                    //System.out.println("LIMIT FOR ANCHOR VERTICAL: "+anc+" "+tLimit);

                    AI.setRC(ar, ac);
                    if(ac- 1 >= 0 && !transposedBoard[ar][ac - 1].isOccupied) AI.leftPart("", dictionary.getRoot(), tLimit);

                    if(ac- 1 >= 0 && transposedBoard[ar][ac - 1].isOccupied) {
                        
                        String partialWord = "";

                        int m = ac-1;
                        while(transposedBoard[ar][m].isOccupied) {
                            partialWord = transposedBoard[ar][m].tile.letter + partialWord;
                            m--;
                            if(m < 0) break;
                        }

                        TrieNode n = dictionary.getChainNode(partialWord);
                        if(n != null)
                            AI.extendRight(partialWord, n, ar, ac);

                    }


                }
            }
        }

    }

    //plz ignore the func name :P
    public int[] decryptPosition(int position) {

        //(row * length_of_row) + column
        int i = 1;
        int[] rc = new int[2];

        outerLoop:
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                if(i == position) {
                    rc[0] = row;
                    rc[1] = col;
                    break outerLoop;
                }
                i++;
            }
        }

        return rc;
    }

    //plz ignore the func name :P
    public  int encryptPostion(int r, int c) {

        return (r * 15) + c + 1;

    }

    public boolean isEmpty() {

        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                if(theBoard[row][col].isOccupied) return false;
            }
        }

        return true;
    }

    public void displayBoard() {

        int i = 1;
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                if(!theBoard[row][col].isOccupied) {
                    if(i < 10) System.out.print(0+""+0+""+i+" ");
                    else if(i < 100) System.out.print(0+""+i+" ");
                    else System.out.print(i+" "); 
                }
                else System.out.print("_"+theBoard[row][col].tile.letter+"_ ");
                i++;
            }
            System.out.println();
        }

    }

    public void displayBoardTransposed() {

        int i = 1;
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                if(!transposedBoard[row][col].isOccupied) {
                    if(i < 10) System.out.print(0+""+0+""+i+" ");
                    else if(i < 100) System.out.print(0+""+i+" ");
                    else System.out.print(i+" "); 
                }
                else System.out.print("_"+transposedBoard[row][col].tile.letter+"_ ");
                i++;
            }
            System.out.println();
        }

    }

    public void crossCheckUpdate() {

        for(int[] rc : occupiedCells) {

            for(int pos : theBoard[rc[0]][rc[1]].anchors) {

                int r = decryptPosition(pos)[0];
                int c = decryptPosition(pos)[1];

                if(r > 0 && r < 14 && theBoard[r-1][c].isOccupied && !theBoard[r+1][c].isOccupied || (r == 14 && theBoard[r-1][c].isOccupied)) {
                    String partialWord = "";
                    int i = r - 1;
                    while(theBoard[i][c].isOccupied) {
                        partialWord = theBoard[i][c].tile.letter + partialWord;
                        i = i - 1;
                        if(i < 0) break;
                    }
        
                    for(int j = 0; j < 26; j++) {
                        String word = partialWord + (char)(j + 97);
                        if(!dictionary.validateWord(word)){
                            theBoard[r][c].crossChecks[j] = false;
                        }
                    }
                }

                if(r > 0 && r < 14 && theBoard[r-1][c].isOccupied && theBoard[r+1][c].isOccupied) {
                    String partialWord1 = "";
                    int i = r - 1;
                    while(theBoard[i][c].isOccupied) {
                        partialWord1 = theBoard[i][c].tile.letter + partialWord1;
                        i = i - 1;
                        if(i < 0) break;
                    }

                    String partialWord2 = "";
                    int i1 = r + 1;
                    while(theBoard[i1][c].isOccupied) {
                        partialWord2 = partialWord2 + theBoard[i1][c].tile.letter;
                        i1 = i1 + 1;
                        if(i1 > 14) break;
                    }

                    for(int j = 0; j < 26; j++) {
                        String word = partialWord1 + (char)(j + 97) + partialWord2;
                        if(!dictionary.validateWord(word)){
                            theBoard[r][c].crossChecks[j] = false;
                        }                      
                    }

                }
        
                if(r > 0 && r < 14 && !theBoard[r-1][c].isOccupied && theBoard[r+1][c].isOccupied || (r == 0 && theBoard[r+1][c].isOccupied)) {
        
                    String partialWord = "";
                    int i = r + 1;
                    while(theBoard[i][c].isOccupied) {
                        partialWord = partialWord + theBoard[i][c].tile.letter;
                        i = i + 1;
                        if(i > 14) break;
                    }
        
                    for(int j = 0; j < 26; j++) {
                        String word = (char)(j + 97) + partialWord;
                        if(!dictionary.validateWord(word)){
                            theBoard[r][c].crossChecks[j] = false;
                        }
                    }
                }
        
            }
        }

    }

    public void crossCheckUpdateTransposed() {

        for(int[] rc : occupiedCells) {

            int tR = getTransposedCoordinates(rc[0], rc[1], 15, 15)[0];
            int tC = getTransposedCoordinates(rc[0], rc[1], 15, 15)[1];

            for(int pos : transposedBoard[tR][tC].anchors) {

                int r = decryptPosition(pos)[0];
                int c = decryptPosition(pos)[1];

                if(r > 0 && transposedBoard[r-1][c].isOccupied && r < 14 && !transposedBoard[r+1][c].isOccupied || (r == 14 && transposedBoard[r-1][c].isOccupied)) {
                    String partialWord = "";
                    int i = r - 1;
                    while(transposedBoard[i][c].isOccupied) {
                        partialWord = partialWord + transposedBoard[i][c].tile.letter;
                        i = i - 1;
                        if(i < 0) break;
                    }
        
                    for(int j = 0; j < 26; j++) {
                        String word = (char)(j + 97)+partialWord;
                        if(!dictionary.validateWord(word)){
                            transposedBoard[r][c].crossChecks[j] = false;
                        }
                    }
                }

                if(r > 0 && transposedBoard[r-1][c].isOccupied && r < 14 && transposedBoard[r+1][c].isOccupied) {

                    String partialWord1 = "";
                    int i = r - 1;
                    while(transposedBoard[i][c].isOccupied) {
                        partialWord1 = partialWord1 + transposedBoard[i][c].tile.letter;
                        i = i - 1;
                        if(i < 0) break;
                    }

                    String partialWord2 = "";
                    int i1 = r + 1;
                    while(transposedBoard[i1][c].isOccupied) {
                        partialWord2 = transposedBoard[i1][c].tile.letter + partialWord2;
                        i1 = i1 + 1;
                        if(i1 > 14) break;
                    }

                    for(int j = 0; j < 26; j++) {
                        String word = partialWord2 + (char)(j + 97) + partialWord1;
                        if(!dictionary.validateWord(word)){
                            transposedBoard[r][c].crossChecks[j] = false;
                        }                      
                    }

                }
        
                if(r > 0 && !transposedBoard[r-1][c].isOccupied && r < 14 && transposedBoard[r+1][c].isOccupied || (r == 0 && transposedBoard[r+1][c].isOccupied)) {
        
                    String partialWord = "";
                    int i = r + 1;
                    while(transposedBoard[i][c].isOccupied) {
                        partialWord = transposedBoard[i][c].tile.letter + partialWord;
                        i = i + 1;
                        if(i > 14) break;
                    }
        
                    for(int j = 0; j < 26; j++) {
                        String word = partialWord + (char)(j + 97);
                        //System.out.println(word);
                        if(!dictionary.validateWord(word)){
                            transposedBoard[r][c].crossChecks[j] = false;
                        }
                    }
                }
        
            }
        }

    }

    public BoardCell[][] transposeLeft(BoardCell[][] array) throws CloneNotSupportedException {
        int rows = array.length;
        int columns = array[0].length;

        // Create a new array with swapped dimensions
        BoardCell[][] transposedArray = new BoardCell[columns][rows];

        // Transpose the array leftward
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                transposedArray[i][j] = (BoardCell)(array[j][columns - 1 - i].clone());
            }
        }

        return transposedArray;
    }

    public static int[] getTransposedCoordinates(int originalRow, int originalColumn, int originalRows, int originalColumns) {
        int[] transposedCoordinates = new int[2];

        // Calculate transposed coordinates
        transposedCoordinates[0] = originalColumns - 1 - originalColumn;
        transposedCoordinates[1] = originalRow;

        return transposedCoordinates;
    }

    public void readData(String file, boolean normal) {

        BufferedReader reader = null;
        try {

            reader = new BufferedReader(new FileReader(file));

            String word = reader.readLine();
            String poses = reader.readLine();

            while (word != null) {
                
                String[] split = poses.split(" ");
                ArrayList<Integer> selections = new ArrayList<>();

                for(String s : split) {
                    selections.add(Integer.parseInt(s));
                }

                if(normal) aiCombos(selections, word);
                else aiCombos_Trans(selections, word);

                word = reader.readLine();
                poses = reader.readLine();
            }
            

        } catch (IOException e) {
            e.printStackTrace();

        } finally {

            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
 
                e.printStackTrace();
            }
        }
        



    }

    public void aiCombos(ArrayList<Integer> selections, String potentialWord) {

        ArrayList<ArrayList<Integer>> allWordsOnTheMove = new ArrayList<>();
        ArrayList<String> combos = new ArrayList<>();

        allWordsOnTheMove.add(selections);
        combos.add(potentialWord);

        for(int sel : selections) {

            int c = decryptPosition(sel)[1];

            for(int i = 0; i < 15; i++) {

                int rc = encryptPostion(i, c);
                String word = "";
                ArrayList<Integer> pos = new ArrayList<>();

                while(theBoard[i][c].isOccupied || selections.contains(rc)) {

                    if(selections.contains(rc)) {
                        char t = potentialWord.charAt(selections.indexOf(rc));
                        pos.add(encryptPostion(i, c));
                        word += t;
                        i++;
                        rc = encryptPostion(i, c);
                    }

                    else {
                        pos.add(encryptPostion(i, c));
                        word += theBoard[i][c].tile.letter;
                        i++;
                        rc = encryptPostion(i, c);
                    }

                    if(i > 14) break;

                }

                if(word.length() > 1 && dictionary.validateWord(word) && (!player1moves.contains(pos) && !player2moves.contains(pos))) {

                    combos.add(word);
                    allWordsOnTheMove.add(pos);

                }

            }

        }

        phantomScorer(allWordsOnTheMove, combos);

    }

    public void aiCombos_Trans(ArrayList<Integer> selections, String potentialWord) {

        ArrayList<ArrayList<Integer>> allWordsOnTheMove = new ArrayList<>();
        ArrayList<String> combos = new ArrayList<>();

        allWordsOnTheMove.add(selections);
        combos.add(potentialWord);

        for(int sel : selections) {

            int tC = decryptPosition(sel)[0];

            for(int i = 14; i >= 0; i--) {

                int rc = encryptPostion(tC, (15 - i - 1));
                String word = "";
                ArrayList<Integer> pos = new ArrayList<>();

                while(transposedBoard[i][tC].isOccupied || selections.contains(rc)) {

                    if(selections.contains(rc)) {
                        char t = potentialWord.charAt(selections.indexOf(rc));
                        pos.add(encryptPostion(tC, (15 - i - 1)));
                        word += t;
                        i--;
                        rc = encryptPostion(tC, (15 - i - 1));
                    }

                    else {
                        pos.add(encryptPostion(tC, (15 - i - 1)));
                        word += transposedBoard[i][tC].tile.letter;
                        i--;
                        rc = encryptPostion(tC, (15 - i - 1));
                    }

                    if(i < 0) break;

                }

                if(word.length() > 1 && dictionary.validateWord(word) && (!player1moves.contains(pos) && !player2moves.contains(pos))) {

                    combos.add(word);
                    allWordsOnTheMove.add(pos);

                }

            }

        }

        phantomScorer(allWordsOnTheMove, combos);

    }

    public void phantomScorer(ArrayList<ArrayList<Integer>> movePositions, ArrayList<String> words) {

        ArrayList<ArrayList<Tile>> wordsTile = new ArrayList<>();

        for(String w : words) {

            ArrayList<Tile> wT = new ArrayList<>();

            for(int i = 0; i < w.length(); i++) {

                wT.add(new Tile(w.charAt(i)));

            }

            wordsTile.add(wT);

        }

        boolean ch = true;

        if(movePositions.get(0).size() < 7) ch = false;
        
        if(movePositions.get(0).size() >= 7) {

            int co = 0;

            for(int p : movePositions.get(0)) {
                
                if(!theBoard[decryptPosition(p)[0]][decryptPosition(p)[1]].isOccupied) {
                    co++;
                }

            }
            
            //bingo
            ch = (co == 7) ? true : false;

        }
        

        int score = 0;
        
        boolean[][] spVisited = new boolean[15][15];
        
        for(int i = 0; i < movePositions.size(); i++) {

            boolean dw = false;
            boolean tw = false;

            for(int j = 0; j < movePositions.get(i).size(); j++) {

                int r = decryptPosition(movePositions.get(i).get(j))[0];
                int c = decryptPosition(movePositions.get(i).get(j))[1];

                if(theBoard[r][c].speciality == 0) {
                    score += wordsTile.get(i).get(j).value;
                }

                if(theBoard[r][c].speciality == 1) {
                    if(!spVisited[r][c]) score += (wordsTile.get(i).get(j).value * 2);
                    spVisited[r][c] = true;
                    
                    //theBoard[r][c].speciality = 0;
                }

                if(theBoard[r][c].speciality == 2) {
                    if(!spVisited[r][c]) score += (wordsTile.get(i).get(j).value * 3);
                    spVisited[r][c] = true;
                    //theBoard[r][c].speciality = 0;
                }

                if(theBoard[r][c].speciality == 3) {
                    score += wordsTile.get(i).get(j).value;
                    if(!spVisited[r][c]) dw = true;
                    spVisited[r][c] = true;
                    //theBoard[r][c].speciality = 0;
                }

                if(theBoard[r][c].speciality == 4 ) {
                    score += wordsTile.get(i).get(j).value;
                    if(!spVisited[r][c]) tw = true;
                    spVisited[r][c] = true;
                    //theBoard[r][c].speciality = 0;
                }

            }

            if(tw) score *= 3;
            if(dw) score *= 2;


        }

        if(ch) score += 50;

        if(potentialMove == null) {
            potentialMove = new Move(wordsTile.get(0), movePositions.get(0), score);
            potentialMove.setEveryPositions(movePositions);
            potentialMove.setEveryWords(words);
        }
        
        else {

            if(potentialMove.score <= score) {
                potentialMove = new Move(wordsTile.get(0), movePositions.get(0), score);
                potentialMove.setEveryPositions(movePositions);
                potentialMove.setEveryWords(words);
            }

        }

    }

    public boolean moveSearchNormal(ArrayList<Integer> selections, ArrayList<Tile> tiles, int player) {

        int r = decryptPosition(selections.get(0))[0];
        ArrayList<ArrayList<Integer>> allWordsOnTheMove = new ArrayList<>();

        for(int i = 0; i < 15; i++) {

            int rc = encryptPostion(r, i);
            String word = "";
            ArrayList<Integer> pos = new ArrayList<>();

            while(theBoard[r][i].isOccupied || selections.contains(rc)) {
                
                if(selections.contains(rc)) {
                    Tile t = tiles.get(selections.indexOf(rc));
                    pos.add(encryptPostion(r, i));
                    word += t.letter;
                    i++;
                    rc = encryptPostion(r, i);
                }

                else {
                    pos.add(encryptPostion(r, i));
                    word += theBoard[r][i].tile.letter;
                    i++;
                    rc = encryptPostion(r, i);
                }

                if(i > 14) break;

            }

            if(word.length() > 1 && !dictionary.validateWord(word)) return false;

            if(word.length() > 1 && dictionary.validateWord(word) && (!player1moves.contains(pos) && !player2moves.contains(pos))) {
                if(player == 1) player1moves.add(pos);
                else player2moves.add(pos);
                allWordsOnTheMove.add(pos);                
            }


        }

        for(int sel : selections) {

            int c = decryptPosition(sel)[1];

            for(int i = 0; i < 15; i++) {

                int rc = encryptPostion(i, c);
                String word = "";
                ArrayList<Integer> pos = new ArrayList<>();

                while(theBoard[i][c].isOccupied || selections.contains(rc)) {
                    
                    if(selections.contains(rc)) {
                        Tile t = tiles.get(selections.indexOf(rc));
                        pos.add(encryptPostion(i, c));
                        word += t.letter;
                        i++;
                        rc = encryptPostion(i, c);
                    }

                    else {
                        pos.add(encryptPostion(i, c));
                        word += theBoard[i][c].tile.letter;
                        i++;
                        rc = encryptPostion(i, c);
                    }

                    if(i > 14) break;

                }

                if(word.length() > 1 && dictionary.validateWord(word) && (!player1moves.contains(pos) && !player2moves.contains(pos))) {
                    if(player == 1) player1moves.add(pos);
                    else player2moves.add(pos);  
                    allWordsOnTheMove.add(pos);                
                }

            }


        }

        for(int i = 0; i < tiles.size(); i++) {

                int ro = decryptPosition(selections.get(i))[0];
                int co = decryptPosition(selections.get(i))[1];

                theBoard[ro][co].setTileOnCell(tiles.get(i));
                int tr = getTransposedCoordinates(ro, co, 15, 15)[0];
                int tc = getTransposedCoordinates(ro, co, 15, 15)[1];
                transposedBoard[tr][tc].setTileOnCell(tiles.get(i));

                occupiedCells.add(decryptPosition(selections.get(i)));
                addCellAnchors(decryptPosition(selections.get(i)));
            }

            for(int[] rc : occupiedCells) {
                updateAnchorSqrs(rc);
                updateAnchorSqrsTransposed(rc);
            }

            crossCheckUpdate();
            crossCheckUpdateTransposed();

        if(allWordsOnTheMove.size() > 0) stuff += "PLAYER: "+player+"\n";

        for(ArrayList<Integer> dd : allWordsOnTheMove) {
            String word = "";
            for(int d : dd) {
                word += theBoard[decryptPosition(d)[0]][decryptPosition(d)[1]].tile.letter;
            }
            //System.out.println(word);
            stuff += word+"\n";
        }

        scoreCalculator(allWordsOnTheMove, player);
        return true;


    }

    public boolean moveSearchTransposed(ArrayList<Integer> selections, ArrayList<Tile> tiles, int player) {

        int r = decryptPosition(selections.get(0))[0];
        int tR = getTransposedCoordinates(r, decryptPosition(selections.get(0))[1], 15, 15)[0];
        ArrayList<ArrayList<Integer>> allWordsOnTheMove = new ArrayList<>();

        for(int i = 0; i < 15; i++) {

            int rc = encryptPostion(i, decryptPosition(selections.get(0))[1]);
            String word = "";
            ArrayList<Integer> pos = new ArrayList<>();

            while(transposedBoard[tR][i].isOccupied || selections.contains(rc)) {
                
                if(selections.contains(rc)) {
                    Tile t = tiles.get(selections.indexOf(rc));
                    pos.add(encryptPostion(i, decryptPosition(selections.get(0))[1]));
                    word += t.letter;
                    i++;
                    rc = encryptPostion(i, decryptPosition(selections.get(0))[1]);
                }

                else {
                    pos.add(encryptPostion(i, decryptPosition(selections.get(0))[1]));
                    word += transposedBoard[tR][i].tile.letter;
                    i++;
                    rc = encryptPostion(i, decryptPosition(selections.get(0))[1]);
                }

                if(i > 14) break;

            }


            if(word.length() > 1 && !dictionary.validateWord(word)) return false;

            if(word.length() > 1 && dictionary.validateWord(word) && (!player1moves.contains(pos) && !player2moves.contains(pos))) {
                if(player == 1) player1moves.add(pos);
                else player2moves.add(pos);
                allWordsOnTheMove.add(pos);                 
            }


        }

        for(int sel : selections) {
 
            int tC = decryptPosition(sel)[0];

            for(int i = 14; i >= 0; i--) {
                //System.out.println("R: "+tC+" C: "+i);

                int rc = encryptPostion(tC, (15 - i - 1));
                String word = "";
                ArrayList<Integer> pos = new ArrayList<>();

                while(transposedBoard[i][tC].isOccupied || selections.contains(rc)) {
                    
                    if(selections.contains(rc)) {
                        Tile t = tiles.get(selections.indexOf(rc));
                        pos.add(encryptPostion(tC, (15 - i - 1)));
                        word += t.letter;
                        i--;
                        rc = encryptPostion(tC, (15 - i - 1));
                    }

                    else {
                        pos.add(encryptPostion(tC, (15 - i - 1)));
                        word += transposedBoard[i][tC].tile.letter;
                        i--;
                        rc = encryptPostion(tC, (15 - i - 1));
                    }
                    //System.out.println("im stuck");
                    if(i < 0) break;
                }

                //System.out.println(word);

                if(word.length() > 1 && dictionary.validateWord(word) && (!player1moves.contains(pos) && !player2moves.contains(pos))) {
                    if(player == 1) player1moves.add(pos);
                    else player2moves.add(pos);
                    allWordsOnTheMove.add(pos);                   
                }

            }


        }

        for(int i = 0; i < tiles.size(); i++) {
                //System.out.println("kk");
                int ro = decryptPosition(selections.get(i))[0];
                int co = decryptPosition(selections.get(i))[1];

                theBoard[ro][co].setTileOnCell(tiles.get(i));
                int tr = getTransposedCoordinates(ro, co, 15, 15)[0];
                int tc = getTransposedCoordinates(ro, co, 15, 15)[1];
                transposedBoard[tr][tc].setTileOnCell(tiles.get(i));

                occupiedCells.add(decryptPosition(selections.get(i)));
                addCellAnchors(decryptPosition(selections.get(i)));
                //System.out.println(selections.get(i));
            }

            for(int[] rc : occupiedCells) {

                updateAnchorSqrs(rc);
                updateAnchorSqrsTransposed(rc);
            }

            crossCheckUpdate();
            crossCheckUpdateTransposed();

        if(allWordsOnTheMove.size() > 0) stuff += "PLAYER: "+player+"\n";

        for(ArrayList<Integer> dd : allWordsOnTheMove) {
            String word = "";
            for(int d : dd) {
                word += theBoard[decryptPosition(d)[0]][decryptPosition(d)[1]].tile.letter;
            }
            //System.out.println(word);
            stuff += word+"\n";
        }

        scoreCalculator(allWordsOnTheMove, player);
        return true;

    }

    private void scoreCalculator(ArrayList<ArrayList<Integer>> list, int player) {

        int score = 0;
        
        
        for(ArrayList<Integer> poses : list) {

            boolean dw = false;
            boolean tw = false;

            for(int i : poses) {

                int r = decryptPosition(i)[0];
                int c = decryptPosition(i)[1];

                //normal cell = 0
                //dbl letter = 1;
                //triple letter = 2;
                //dbl word = 3;
                //triple word = 4;

                if(theBoard[r][c].speciality == 0 ) {
                    score += theBoard[r][c].tile.value;
                }

                if(theBoard[r][c].speciality == 1 ) {
                    score += (theBoard[r][c].tile.value * 2);
                    theBoard[r][c].speciality = 0;
                }

                if(theBoard[r][c].speciality == 2 ) {
                    score += (theBoard[r][c].tile.value * 3);
                    theBoard[r][c].speciality = 0;
                }

                if(theBoard[r][c].speciality == 3 ) {
                    score += theBoard[r][c].tile.value;
                    dw = true;
                    theBoard[r][c].speciality = 0;
                }

                if(theBoard[r][c].speciality == 4 ) {
                    score += theBoard[r][c].tile.value;
                    tw = true;
                    theBoard[r][c].speciality = 0;
                }

            }

            if(tw) score *= 3;
            if(dw) score *= 2;


        }

        if(list.size() > 0 && list.get(0).size() == 7) score += 50;

        if(player == 1) p1Score += score;
        else p2Score += score;
        //System.out.println("Score on the move: "+score);
        if(list.size() > 0) {
            stuff += "SCORE ON THE MOVE: "+score+"\n----------------\n";
            
        }


    }

    //test functions
    public void getAnchorCountTransposed() {

        if(!isEmpty()) {
            for(int[] rc : occupiedCells) {
                int r = getTransposedCoordinates(rc[0], rc[1], 15, 15)[0];
                int c = getTransposedCoordinates(rc[0], rc[1], 15, 15)[1];
                for(int pos : transposedBoard[r][c].anchors) {
                    System.out.print(transposedBoard[r][c].tile.letter+" ---> "+pos+" ");
                }
                System.out.println();
            }
        }
    }

    public void getAnchorCount() {

        if(!isEmpty()) {
            for(int[] rc : occupiedCells) {
                int r = rc[0];
                int c = rc[1];
                for(int pos : theBoard[r][c].anchors) {
                    System.out.print(theBoard[r][c].tile.letter+" ---> "+pos+" ");
                }
                System.out.println();
            }
        }
    }


    public void crossCheckTest() {
        if(!isEmpty()) {
            for(int[] rc : occupiedCells) {
                for(int pos : theBoard[rc[0]][rc[1]].anchors) {
                    int r = decryptPosition(pos)[0];
                    int c = decryptPosition(pos)[1];
                    for(int i = 0; i < 26; i++) {
                        if(theBoard[r][c].crossChecks[i]) System.out.println(pos+" --> cross checks: "+(char)(i + 97));
                    }

                }
                
            }
        }
    }

}

class BoardCell implements Cloneable {

    Tile tile;
    boolean isOccupied;
    int speciality;
    boolean[] crossChecks = new boolean[26];
    ArrayList<Integer> anchors = new ArrayList<>();

    public BoardCell(int sp) {

        speciality = sp;
        isOccupied = false;
        tile = null;

        for(int i = 0; i < 26; i++) {
            crossChecks[i] = true;
        }
    }

    public void setTileOnCell(Tile tile) {
        this.tile = tile;
        isOccupied = true;
    }

    public void unsetTileOnCell() {
        tile = null;
        isOccupied = false;
    }

    protected Object clone() {
        try {
            BoardCell cloned = (BoardCell)super.clone();
            cloned.anchors = new ArrayList<>(this.anchors);
            cloned.crossChecks = new boolean[26];
            for(int i = 0; i < 26; i++) {
                cloned.crossChecks[i] = true;
            }
            if(tile != null) {
                cloned.tile = (Tile)tile.clone();
            }
            return cloned;
        } catch (CloneNotSupportedException e) {
            // This should not happen, as we are Cloneable
            throw new InternalError(e);
        }
    }

}

class Move {

    ArrayList<Tile> tiles;
    ArrayList<Integer> positions;
    ArrayList<ArrayList<Integer>> everyPositions;
    ArrayList<String> everyWords;
    public void setEveryWords(ArrayList<String> everyWords) {
        this.everyWords = everyWords;
    }

    public void setEveryPositions(ArrayList<ArrayList<Integer>> everyPositions) {
        this.everyPositions = everyPositions;
    }

    int score;

    Move(ArrayList<Tile> ts, ArrayList<Integer> poses, int s) {

        tiles = ts;
        positions = poses;
        score = s;

    }



}