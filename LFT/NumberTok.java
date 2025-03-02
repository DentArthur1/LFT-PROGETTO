
public class NumberTok extends Token {

    public int number;

    public NumberTok(int tag, int n) {
        super(tag);
        number = n;

    }

    public String toString() {
        return "<" + tag + ", " + number + ">";
    }

    // public static final NumberTok numb = new NumberTok(Tag.NUM, number);

}