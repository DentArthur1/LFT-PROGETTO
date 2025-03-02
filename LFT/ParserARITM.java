
import java.io.*;


public class ParserARITM {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public ParserARITM(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }

    void move() {
        look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }

    void error(String s) {
        throw new Error("near line " + lex.line + ": " + s);
    }

    void match(int t) {
        if (look.tag == t) {
            if (look.tag != Tag.EOF)
                move();
        } else
            error("syntax error");
    }

    public void start() {

        expr(); // leggo l'espressione
        match(Tag.EOF); // chiudo la stringa

    }

    private void expr() { // espressione generale

        term(); // terminale
        exprp(); // operazione

    }

    private void exprp() { // addizioni, sottrazioni etc

        switch (look.tag) {
            case '+':
            case '-':
                match(look.tag);
                term();
                exprp();
                break;

            default:
                // Caso produzione vuota
                break;
        }

    }

    private void term() { // termine tra -,+

        fact();
        termp();

    }

    private void termp() { // divisioni, moltiplicazioni,..etc

        switch (look.tag) {
            case '*': // unisco casi * e /
            case '/':
                match(look.tag);
                fact();
                termp();
                break;

            default: // caso produzione vuota consentito
                break;
        }

    }

    private void fact() { // parentesi --> NUM o (expr)

        switch (look.tag) {
            case '(':
                match('(');
                expr();
                match(')');
                break;

            case Tag.NUM:
                match(Tag.NUM);
                break;

            default: // caso produzione vuota non permesso
                error("Syntax error");
                break;
        }

    }

    public static void main(String[] args) {

        Lexer lex = new Lexer();
        String path = "test.lft"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            ParserARITM parser = new ParserARITM(lex, br);
            parser.start();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}