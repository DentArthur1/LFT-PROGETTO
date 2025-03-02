package Automi;

public class TreZeri {

  public static boolean es1(String s) {
    int state = 0;
    int i = 0;
    while (state >= 0 && i < s.length()) {
      char ch = s.charAt(i++);
      switch (state) {
        case 0:
          if (ch == '1') {
            state = 0;
          } else if (ch == '0') {
            state = 1;
          } else {
            state = -1;
          }
          break;

        case 1:
          if (ch == '1') {
            state = 0;
          } else if (ch == '0') {
            state = 2;
          } else {
            state = -1;
          }
          break;

        case 2:
          if (ch == '1') {
            state = 0;
          } else if (ch == '0') {
            state = 3;
          } else {
            state = -1;
          }
          break;

        case 3:
          if (ch == '0' || ch == '1') {
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
    System.out.println(es1("1000000"));
  }

}