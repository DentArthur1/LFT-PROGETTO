package Compilatore.AnalizzatoreLessicale;

import java.io.*;


                                    /*ESERCIZI 2.1, 2.2, 2.3 */

public class Lexer {                /*ESERCIZIO 2.1 */

    public int line = 1;
    private char peek = ' ';

    private void readch(BufferedReader br) { // legge il carattere corrente e sposta il puntatore peek a quello
                                             // successivo
        try {
            peek = (char) br.read();
        } catch (IOException exc) {
            peek = (char) -1; // ERROR
        }
    }

    private void mark_pos(BufferedReader br, int limit) { 
     //funzione per marcare la posizione al peek attuale in modo da poter
     //effettuare un "fallback", se necessario
        try {
            br.mark(limit);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void reset_pos(BufferedReader br) {
     //funzione per resettare la posizione di lettura al punto di fallback
        try {
            br.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean comment_loop(BufferedReader br) {             /*ESERCIZIO 2.3 */
        //funzione per ignorare i commenti di tipo "/*comment*/"
        //Ad ogni iterazione del loop si cerca la sequenza di caratteri */ 
        //in caso positivo, rientra nel loop di lettura del lexer
        //in caso di raggiungimento della fine del file senza aver prima trovato la sequenza cercata, errore
        while (true) {
            if (peek == '*') {
                mark_pos(br, 1);
                readch(br);
                if (peek == '/') {
                    break;
                } else {
                    reset_pos(br);
                }
            } else if (peek == (char) -1) { // file finito e commento mai chiuso
                System.out.println("Error: Comment was never closed.");
                return false;
            }
            readch(br);
        }
        return true;
    }

    private void comment_2_loop(BufferedReader br) {            /*ESERCIZIO 2.3 */
     //funzione per ignorare i commenti del tipo "//comment"
     //termina se si raggiunge la fine del file o si incontra un carattere newline
        while (true) {
            if (peek == '\n' || peek == (char) -1) {
                return;
            }
            readch(br);
        }
    }

    public Token lexical_scan(BufferedReader br) { // tokenizza l'intero codice
        while (peek == ' ' || peek == '\t' || peek == '\n' || peek == '\r') {
            if (peek == '\n')
                line++;
            readch(br);
        }
        switch (peek) {
            case '!':
                peek = ' ';
                return Token.not;
            case '(':
                peek = ' ';
                return Token.lpt;
            case ')':
                peek = ' ';
                return Token.rpt;
            case '[':
                peek = ' ';
                return Token.lpq;
            case ']':
                peek = ' ';
                return Token.rpq;
            case '{':
                peek = ' ';
                return Token.lpg;
            case '}':
                peek = ' ';
                return Token.rpg;
            case '+':
                peek = ' ';
                return Token.plus;
            case '-':
                peek = ' ';
                return Token.minus;
            case '*':
                peek = ' ';
                return Token.mult;

            case '/':                     /*ESERCZIO 2.3 */
                mark_pos(br, 1); //marco la posizione a prima di aver letto '/'
                readch(br);
                if (peek == '*') { //caso commento /* */
                    peek = ' ';
                    boolean response = comment_loop(br);
                    if (!response) { //commento mai chiuso, return errore
                        return null;
                    }
                    peek = ' ';
                    //ho finito di leggere il commento, ritorno nel loop principale
                    return this.lexical_scan(br); 

                } else if (peek == '/') { //caso commento //comment
                    peek = ' ';
                    comment_2_loop(br);
                    //ho finito di leggere il commento, ritorno nel loop principale
                    return this.lexical_scan(br);

                } else { //caso divisione
                    reset_pos(br);
                    peek = ' ';
                    return Token.div;
                }
            case ';':
                peek = ' ';
                return Token.semicolon;

            case ',':
                peek = ' ';
                return Token.comma;

            case '&':
                readch(br);
                if (peek == '&') {
                    peek = ' ';
                    return Word.and;
                } else {
                    System.err.println("Erroneous character"
                            + " after '&' : " + peek);
                    return null;
                }
            case '|':
                readch(br);
                if (peek == '|') {
                    peek = ' ';
                    return Word.or;
                } else {
                    System.err.println("Erroneous character"
                            + " after '|' : " + peek);
                    return null;
                }
            case ':':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.init;
                } else {
                    System.err.println("Erroneous character"
                            + " after ':' : " + peek);
                    return null;
                }

            case '=':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.eq;
                } else {
                    System.err.println("Erroneous character"
                            + " after '=' : " + peek);
                    return null;
                }

            case '<':
                //gestisco i casi "<" "<=" "<>"
                mark_pos(br, 1); //marco la posizione a prima di aver letto "<"
                readch(br);
                if (peek == '=') { //caso minore uguale
                    peek = ' ';
                    return Word.le;
                } else if (peek == '>') { //caso diverso
                    peek = ' ';
                    return Word.ne;
                } else { //caso minore stretto
                    reset_pos(br); //ripristino la posizione
                    peek = ' ';
                    return Word.lt;
                }
            case '>':
                //gestisco i casi ">" ">=" "<>"
                mark_pos(br, 1); //marco la posizione a prima di aver letto "<"
                readch(br);
                if (peek == '=') { //caso maggiore uguale
                    peek = ' ';
                    return Word.ge;
                } else { //caso maggiore stretto
                    reset_pos(br); //ripristino la posizione
                    peek = ' ';
                    return Word.gt;
                }

            case (char) -1:
                return new Token(Tag.EOF);

            default:                 /*ESERCZIO 2.2 */
                
                if (Character.isLetter(peek) || peek == '_') {  //identificatore iniziano per lettera o _

                    String str = "";
                    int counter = 0; //inizializzo contatore caratteri per caso peek == '_'
                    boolean underscore = false;
                    while (Character.isLetter(peek) || Character.isDigit(peek) || peek == '_') {
                        str = str + peek; //compongo l'indentificatore
                        counter++; //aggiorno il contatore dei caratteri
                        if (peek == '_') { //setto underscore true nel caso sia presente
                            underscore = true;
                        }
                        readch(br);
                    }

                    // controllo underscore
                    if (counter == 1 && underscore) { //se la parola è composta solo da '_', errore
                        System.err.println("Erroneous character: Identificators cannot be composed only of '_' -->"
                                + peek);
                        return null;
                    }
                    //gestione parole chiave
                    String string = str.toString();
                    switch (string) {
                        case "if":
                            return Word.iftok;
                        case "else":
                            return Word.elsetok;
                        case "do":
                            return Word.dotok;
                        case "for":
                            return Word.fortok;
                        case "begin":
                            return Word.begin;
                        case "end":
                            return Word.end;
                        case "print":
                            return Word.print;
                        case "read":
                            return Word.read;
                        case "assign":
                            return Word.assign;
                        case "to":
                            return Word.to;
                        default: //Se il caso non rientra nei precedenti, allora è un identificatore
                            return new Word(Tag.ID, string); // identificatore
                    }

                } else if (Character.isDigit(peek)) { //caso numero

                    String n = ""; //inizializzo stringa numero
                    while (Character.isDigit(peek)) {
                        n = n + peek; //compongo il numero
                        readch(br);
                        if (Character.isLetter(peek) || peek == '_') { //nel caso incontri una lettera o il carattere underscore, errore
                            System.err.println("Erroneous character: Unidentified number-char sequence."
                                    + peek);
                            return null;
                        }
                    }

                    return new NumberTok(Tag.NUM, Integer.parseInt(n)); //tokenizzo il numero

                } else {
                    System.err.println("Erroneous character: "
                            + peek);
                    return null;
                }
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "Compilatore/test.lft"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Token tok;
            do {
                tok = lex.lexical_scan(br);
                System.out.println("Scan: " + tok);
            } while (tok.tag != Tag.EOF);
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
