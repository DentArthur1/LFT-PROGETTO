package Automi;

public class Esercizio16 {

    public static boolean es16(String s) {

        int state = 0;

        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);

            switch (state) { // 2 stati di terminazione
                case 0:
                    if ((ch == 'a') || (ch == '*')) {
                        state = 0;
                    } else if (ch == '/') {
                        state = 1;
                    } else {
                        state = -1;
                    }
                    break;
                case 1:
                    if (ch == 'a') {
                        state = 0;
                    } else if (ch == '*') {
                        state = 2;
                    } else {
                        state = -1;
                    }
                    break;
                case 2:
                    if ((ch == 'a') || (ch == '/')) {
                        state = 2;
                    } else if (ch == '*') {
                        state = 3;
                    } else {
                        state = -1;
                    }
                    break;
                case 3:
                    if (ch == '/') {
                        state = 0;
                    } else if ((ch == 'a') || (ch == '*')) {
                        state = 3;
                    } else {
                        state = -1;
                    }
                    break;

            }
        }
        return state == 1 || state == 0;
    }

    public static void main(String[] args) {
        System.out.print(es16("aaa/****/aa"));
    }

}
