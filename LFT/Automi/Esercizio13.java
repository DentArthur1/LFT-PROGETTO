package Automi;

public class Esercizio13 {

    public static boolean es13(String s) {
        int state = 0;

        for (int i = 0; i < s.length() && state >= 0; i++) {
            char ch = s.charAt(i);

            switch (state) {
                case 0:
                    if ((ch >= '0' && ch <= '9')) {
                        state = 1;
                    } else {
                        state = -1;
                    }
                    break;
                case 1:
                    if ((ch >= '0' && ch <= '9')) { // pari
                        int num = Character.getNumericValue(ch);
                        if (num % 2 == 0) {
                            state = 1;
                        } else {
                            state = 2;
                        }
                    } else if ((ch >= 'a' && ch <= 'k') || (ch >= 'A' && ch <= 'K')) {
                        state = 3;
                    } else {
                        state = -1;
                    }
                    break;
                case 2:
                    if ((ch >= '0' && ch <= '9')) { // dispari
                        int num = Character.getNumericValue(ch);
                        if (num % 2 == 0) {
                            state = 1;
                        } else {
                            state = 2;
                        }
                    } else if ((ch >= 'l' && ch <= 'z') || (ch >= 'L' && ch <= 'Z')) {
                        state = 3;
                    } else {
                        state = -1;
                    }
                    break;

                case 3:
                    if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
                        state = 3;
                    } else {
                        state = -1;
                    }
                    break;
            }
        }
        return state == 3;

    }

    public static void main(String[] args) {
        System.out.println(es13("654322"));

    }

}
