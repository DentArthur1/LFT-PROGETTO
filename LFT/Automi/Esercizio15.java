package Automi;

public class Esercizio15 {

    public static boolean es15(String s) {
        int state = 0;

        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            switch (state) {

                case 0:
                    if (ch == '/') {
                        state = 1;
                    } else {
                        state = -1;
                    }
                    break;
                case 1:
                    if (ch == '*') {
                        state = 2;
                    } else {
                        state = -1;
                    }
                    break;
                case 2:
                    if (ch == '/' || ch == 'a') {
                        state = 2;
                    } else if (ch == '*') {
                        state = 3;
                    } else {
                        state = -1;
                    }
                    break;
                case 3:
                    if (ch == 'a') {
                        state = 2;
                    } else if (ch == '/') {
                        state = 4;
                    } else if (ch == '*') {
                        state = 3;
                    } else {
                        state = -1;
                    }
                    break;
                case 4:
                    state = -1;
                    break;
            }

        }
        return state == 4;
    }

    public static void main(String[] args) {
        System.out.println(es15("/**a///a/a**/"));

    }
}
