package Compilatore.Traduttore;
import java.io.*;

import Compilatore.AnalizzatoreLessicale.Lexer;
import Compilatore.AnalizzatoreLessicale.NumberTok;
import Compilatore.AnalizzatoreLessicale.Tag;
import Compilatore.AnalizzatoreLessicale.Token;

public class ValutatoreARITM {             /*ESERCIZIO 4.1*/
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public ValutatoreARITM(Lexer l, BufferedReader br) {
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
        int expr_val;

        expr_val = expr();
        match(Tag.EOF);

        System.out.println(expr_val);

    }

    private int expr() {
        int term_val, exprp_val;

        term_val = term();
        exprp_val = exprp(term_val);

        return exprp_val;
    }

    private int exprp(int exprp_i) {

        int term_val, exprp_val = 0;

        switch (look.tag) {
            case '+':
                match('+');
                term_val = term();
                exprp_val = exprp(exprp_i + term_val);
                break;

            case '-':
                match('-');
                term_val = term();
                exprp_val = exprp(exprp_i - term_val);
                break;

            default: // caso produzione vuota
                exprp_val = exprp_i;
                break;

        }
        return exprp_val;
    }

    private int term() {

        int expr_val, termp_val;
        expr_val = fact();
        termp_val = termp(expr_val);

        return termp_val;

    }

    private int termp(int termp_i) {
        int expr_val, termp_val = 0;

        switch (look.tag) {

            case '*':
                match('*');
                expr_val = fact();
                termp_val = termp(termp_i * expr_val);
                break;

            case '/':
                match('/');
                expr_val = fact();
                termp_val = termp(termp_i / expr_val);
                break;

            default: // caso produzione vuota
                termp_val = termp_i;
                break;
        }

        return termp_val;
    }

    private int fact() {
        int expr_val = 0;
        switch (look.tag) {

            case '(':
                match('(');
                expr_val = expr();
                match(')');
                break;

            case Tag.NUM:
                NumberTok numb = (NumberTok) look;
                expr_val = numb.number;
                match(look.tag);
                break;

            default: // caso produzione vuota non consentito
                error("FactNullError");
                break;
        }

        return expr_val;
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "Compilatore/test.lft"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            ValutatoreARITM valutatore = new ValutatoreARITM(lex, br);
            valutatore.start();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
