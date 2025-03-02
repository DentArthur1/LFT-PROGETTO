package Automi;

public class Esercizio12 {

    public static boolean es12(String s) {
        int i;
        int state = 0;

        for (i = 0; state >= 0 && i < s.length(); i++) {
            char ch = s.charAt(i);

            switch (state) {

                case 0:
                    if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
                        state = 2;
                    } else if (ch == '_') {
                        state = 1;
                    } else {
                        state = -1;
                    }
                    break;
                case 1:
                    if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9')) {
                        state = 2;
                    } else if (ch == '_') {
                        state = 1;
                    } else {
                        state = -1;
                    }
                    break;
                case 2:
                    if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9')
                            || (ch == '_')) {
                        state = 2;
                    } else {
                        state = -1;
                    }
                    break;

            }

        }
        return state == 2;
    }

    public static void main(String[] args) {
        System.out.println(es12("ciao__5"));

    }
}
