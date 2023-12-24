public class Tile  {
    public char letter;
    public int value;

    public Tile(char letter) {
        this.letter = letter;
        String[][] letterScores = {
            {"A", "1"},
            {"B", "3"},
            {"C", "3"},
            {"D", "2"},
            {"E", "1"},
            {"F", "4"},
            {"G", "2"},
            {"H", "4"},
            {"I", "1"},
            {"J", "8"},
            {"K", "5"},
            {"L", "1"},
            {"M", "3"},
            {"N", "1"},
            {"O", "1"},
            {"P", "3"},
            {"Q", "10"},
            {"R", "1"},
            {"S", "1"},
            {"T", "1"},
            {"U", "1"},
            {"V", "4"},
            {"W", "4"},
            {"X", "8"},
            {"Y", "4"},
            {"Z", "10"}
        };

        int ind = 0;

        if(letter == '-') {
            value = 1;
            return;
        }

        if((int)letter > 96 && (int)letter < 123) {
            ind = letter - 97;
            value = Integer.parseInt(letterScores[ind][1]);
        }

        else {
            ind = letter - 65;
            value = Integer.parseInt(letterScores[ind][1]);
        }

        if((int)letter > 64 && (int)letter < 91) {
            letter = (char)(letter + 32);
        }

    }

    protected Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            // This should not happen, as we are Cloneable
            throw new InternalError(e);
        }
    }
}
