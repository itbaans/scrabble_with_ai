import java.util.ArrayList;
import java.util.HashMap;

public class test {
    public static void main(String[] args) {
        String str = "hat56";
        System.out.println(str.substring(0,str.length()-2));
        ArrayList<Integer> pos = new ArrayList<>();
        ArrayList<Integer> nos = new ArrayList<>();
        for (int i = 0; i <3 ; i++) {
            pos.add(i);
            nos.add(i);
        }
        HashMap<ArrayList<Integer>,String> map = new HashMap<>();
        map.put(pos,"word");
        map.put(nos,"nowd");


        System.out.println(map.get(pos));
    }


}
