package Compilatore.AnalizzatoreLessicale;

public class NumberTok extends Token {

    public int number;

    public NumberTok(int tag, int n) {
        super(tag);
        number = n;

    }

    public String toString() {
        return "<" + tag + ", " + number + ">";
    }

   

}