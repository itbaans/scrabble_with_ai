import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MyTrie {

    public TrieNode getRoot() {
        return root;
    }

    TrieNode root = new TrieNode(false);

    public MyTrie(String fileName) {
        readFile(fileName);
    }

    public void print() {
        printREC(root, "");
    }  

    private void printREC(TrieNode n, String s) {

        if(n.isEnd) {
            System.out.println(s);
        }

        for (int i = 0; i < 26; i++) {
            if(n.childs[i] != null) {
                char c = (char)(i + 97);
                printREC(n.childs[i], s + c);
            }
        }
    }

    private void readFile(String fileName) {
        BufferedReader reader = null;
        try {
            //specify the file name and path
            //initialize the reader with the file
            reader = new BufferedReader(new FileReader(fileName));
            //read the first line of the file
            String line = reader.readLine();
            //loop until the end of the file
            while (line != null) {
                //print the line to the console
                insert(line);
                //read the next line of the file
                line = reader.readLine();
            }
        } catch (IOException e) {
            //handle the exception
            e.printStackTrace();
        } finally {
            //close the reader
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                //handle the exception
                e.printStackTrace();
            }
        }
    }

    public boolean validateWord(String word) {
        return validateWordREC(root, word);
    }

    private boolean validateWordREC(TrieNode n ,String word) {

        if(word.length() == 0) return false;

        word = word.toLowerCase();
        int ind = word.charAt(0) - 97;      
        if(word.length() == 1) {
            
            if(n.childs[ind] == null) return false;
            if(n.childs[ind] != null && !n.childs[ind].isEnd) {
                //System.out.println("incomplete word");
                return false;
            }
            return true;
        }

        if(n.childs[ind] == null) return false;
        return validateWordREC(n.childs[ind], word.substring(1));


    }

    public TrieNode getChainNode(String word) {
        return getChainNodeRec(root, word);
    }

    private TrieNode getChainNodeRec(TrieNode n, String word) {

        if(word.length() == 0) return null;

        word = word.toLowerCase();
        int ind = word.charAt(0) - 97;      
        if(word.length() == 1) {
            
            if(n.childs[ind] == null) return null;
            if(n.childs[ind] != null && !n.childs[ind].isEnd) {
                //System.out.println("incomplete word");
                return n.childs[ind];
            }
            return n.childs[ind];
        }

        if(n.childs[ind] == null) return null;
        return getChainNodeRec(n.childs[ind], word.substring(1));


    }

    
    public void insert(String s) {
        insertREC(s, root);
    }

    private void insertREC(String s, TrieNode n) {

        if(s.length() == 0) return;
        
        s = s.toLowerCase();

        int charInd = (int)(s.charAt(0)) - 97;

        if(s.length() == 1) {
            if(n.childs[charInd] == null) n.childs[charInd] = new TrieNode(true);
            else n.childs[charInd].isEnd = true;
            return;
        }
  
        if(n.childs[charInd] == null) n.childs[charInd] = new TrieNode(false);
        insertREC(s.substring(1), n.childs[charInd]);

    }

}


