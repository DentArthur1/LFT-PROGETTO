package Automi;

public class Esercizio14 {

    public static boolean es14(String s) {
        int state = 0;

        for (int i = 0; i < s.length(); i++) { // 2 stati finali
            char ch = s.charAt(i);

            switch (state) {
                case 0:
                    if ((ch >= '0' && ch <= '9')) {
                        state = 1;
                    } else if ((ch == '-') || (ch == '+')) {
                        state = 2;
                    } else if (ch == '.') {
                        state = 3;
                    } else {
                        state = -1;
                    }
                    break;
                case 2:
                    if ((ch >= '0' && ch <= '9')) {
                        state = 1;
                    } else if (ch == '.') {
                        state = 3;
                    } else {
                        state = -1;
                    }
                    break;
                case 3:
                    if ((ch >= '0' && ch <= '9')) {
                        state = 1;
                    } else {
                        state = -1;
                    }
                    break;
                case 1:
                    if ((ch >= '0' && ch <= '9')) {
                        state = 1;
                    } else if (ch == '.') {
                        state = 4;
                    } else if (ch == 'e') {
                        state = 0;
                    }
                    break;
                case 4:
                    if ((ch >= '0' && ch <= '9')) {
                        state = 5;
                    } else {
                        state = -1;
                    }
                    break;
                case 5:
                    if (ch == 'e') {
                        state = 0;
                    } else {
                        state = -1;
                    }
                    break;

            }
        }
        return state == 1 || state == 5;
    }

    public static void main(String[] args) {
        System.out.println(es14(".5e-5"));

    }
}
