package Compilatore.AnalizzatoreSintattico;
import java.io.*;

import Compilatore.AnalizzatoreLessicale.Lexer;
import Compilatore.AnalizzatoreLessicale.Tag;
import Compilatore.AnalizzatoreLessicale.Token;


public class Parser {             /*ESERCIZIO 3.2 */
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Parser(Lexer l, BufferedReader br) {
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

    public void prog() {

        statlist(); // leggo l'espressione
        match(Tag.EOF); // chiudo la stringa

    }

    private void statlist() { // espressione generale

        stat();
        statlistp();

    }

    private void statlistp() {

        switch (look.tag) {

            case ';':
                match(look.tag);
                stat();
                statlistp();
                break;

            default: // caso produzione vuota
                break;
        }

    }

    private void stat() {

        switch (look.tag) {

            case Tag.ASSIGN:
                match(look.tag);
                assignlist();
                break;

            case Tag.PRINT:
                match(look.tag);
                match('(');
                exprlist();
                match(')');
                break;

            case Tag.READ:
                match(look.tag);
                match('(');
                idlist();
                match(')');
                break;

            case Tag.FOR:
                match(look.tag);
                match('(');
                if (look.tag == Tag.ID) {
                    match(Tag.ID);
                    match(Tag.INIT);
                    expr();
                    match(';');
                }
                bexpr();
                match(')');
                match(Tag.DO);
                stat();
                break;

            case Tag.IF:
                match(look.tag);
                match('(');
                bexpr();
                match(')');
                stat();
                if (look.tag == Tag.ELSE) {
                    match(look.tag);
                    stat();
                }
                match(Tag.END);
                break;

            case '{':
                match(look.tag);
                statlist();
                match('}');
                break;

            default:
                System.out.println(look.tag);
                error("StatNullError");
                break;

        }
    }

    private void assignlist() {

        match('[');
        expr();
        match(Tag.TO);
        idlist();
        match(']');
        assignlistp();

    }

    private void assignlistp() {

        switch (look.tag) {

            case '[':
                assignlist();
                break;

            default:
                break;
        }
    }

    private void idlist() {

        match(Tag.ID);
        idlistp();
    }

    private void idlistp() {

        switch (look.tag) {

            case ',':
                match(look.tag);
                match(Tag.ID);
                idlistp();
                break;

            default: // produzione vuota
                break;
        }
    }

    private void bexpr() {

        match(Tag.RELOP);
        expr();
        expr();

    }

    private void expr() {

        switch (look.tag) {

            case '+':
            case '*':
            //operatori AND e OR da 2 o piu' argomenti
            case Tag.AND:
            case Tag.OR:
                match(look.tag);
                match('(');
                exprlist();
                match(')');
                break;
            case '-':
            case '/':
                match(look.tag);
                expr();
                expr();
                break;
            case Tag.ID:
            case Tag.NUM:
                match(look.tag);
                break;

            case '!': //operatore di negazione unario 
                match('!');
                expr();
                break;
               
            default:
                System.err.println(look.tag);
                error("ExprNullError");
                break;

        }
    }

    private void exprlist() {

        expr();
        exprlistp();

    }

    private void exprlistp() {

        switch (look.tag) {

            case ',':
                match(',');
                expr();
                exprlistp();
                break;

            default:
                break;
        }
    }

    public static void main(String[] args) {

        Lexer lex = new Lexer();
        String path = "Compilatore/test.lft"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser parser = new Parser(lex, br);
            parser.prog();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}