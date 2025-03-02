package Compilatore.Traduttore;
import java.io.*;

import Compilatore.AnalizzatoreLessicale.Lexer;
import Compilatore.AnalizzatoreLessicale.NumberTok;
import Compilatore.AnalizzatoreLessicale.Tag;
import Compilatore.AnalizzatoreLessicale.Token;
import Compilatore.AnalizzatoreLessicale.Word;

public class Translator {                /*ESERCIZIO 5.1*/
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    SymbolTable st = new SymbolTable();
    CodeGenerator code = new CodeGenerator();
    int count = 0;

    public Translator(Lexer l, BufferedReader br) {
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
    
    private int check_address(Token look){ //Controlla se l'id e' nella symbol table, in caso contrario lo aggiunge

        int id_addr = st.lookupAddress(((Word) look).lexeme); //controllo se l'ID e presente nella symbol table
        if (id_addr == -1) { //aggiunta ID alla symbol table
            id_addr = count;
            code.emit(OpCode.istore, count);
            st.insert(((Word) look).lexeme, count++);
            return -1; //per le parti nel codice che richiedono di sapere se una variabile e' stata inizializzata in precedenza
            
        }
        return id_addr;
    }

    private void modify_stack(Token saved_look){ //se la variabile era stata gia inizializzata, me aggiorna il valore
        int addr = check_address(saved_look);
        if (addr != -1){
              code.emit(OpCode.istore, addr);
        }
    }

    public void prog() {
      
        int actual_label = code.newLabel();
        statlist(actual_label);
        match(Tag.EOF);
        try {
            code.toJasmin();
        } catch (java.io.IOException e) {
            System.out.println("IO error\n");
        }
        

    }
    
    public void statlist(int actual_label){
        stat(actual_label);
        statlistp(code.newLabel());
    }

    public void statlistp(int actual_label){
        
        switch(look.tag){

            case ';':
               match(';');
               stat(actual_label);
               statlistp(code.newLabel());
               break;

            default:
               break;

        }
    }

    public void stat(int actual_label) { 

        switch (look.tag) {

            case Tag.READ:
                match(Tag.READ);
                match('(');
                idlist(actual_label, true); //attributo true dovuto alla necessità di letture sequenziali di variabili dal terminale
                match(')');
                break;

            case Tag.PRINT:
                match(Tag.PRINT);
                match('(');
                exprlist(actual_label,2, true); //il due non ha significato, messo per ignorare il blocco di if in exprlist
                //attributo true per i print sequenziali delle expr
                match(')');
                code.emit(OpCode.invokestatic, 1); //print finale
                break;

            case Tag.ASSIGN:
                match(Tag.ASSIGN);
                assignlist(actual_label);
                break;

            case Tag.FOR:
                match(Tag.FOR);
                match('(');
                int INIT_FOR = actual_label; //label INIT_FOR
                

                if (look.tag == Tag.ID){ //caso for completo
                    //inizializzo solo una volta il valore "id := val"
                    Token id = look; 
                    match(Tag.ID);
                    match(Tag.INIT);
                    expr(actual_label);
                    int addr = check_address(id); //controlla se la variabile dichiarata nel for è gia stata utilizzata
                    if(addr != -1){
                        code.emit(OpCode.istore, addr); //in caso positivo la aggiorna
                    } 
                    match(';');
                } 

                code.emitLabel(INIT_FOR); //label INIT_FOR (dopo eventuale inizializzazione variabile)
                int IF_LABEL = code.newLabel();
                bexpr(IF_LABEL); //condizione label COND

                int END_FOR = code.newLabel();
                code.emit(OpCode.GOto, END_FOR); //GOTO END_FOR
                code.emitLabel(IF_LABEL);  //label COND

                match(')');
                match(Tag.DO);

                stat(code.newLabel()); //nuova stat

                code.emit(OpCode.GOto, INIT_FOR); //GOTO INIT_FOR
                code.emitLabel(END_FOR); //label END_FOR
                break;
            
            case Tag.IF:
                match(Tag.IF);
                match('(');
                int INIT_IF = actual_label;
                bexpr(INIT_IF); //COND LABEL = INIT_IF
                match(')');
                 
                int END_IF = code.newLabel();
                code.emit(OpCode.GOto, END_IF); //GOTO END_IF
                code.emitLabel(INIT_IF);  //label INIT_IF
               
                stat(code.newLabel());
  
                int END_IF_NEW = code.newLabel();
                code.emit(OpCode.GOto, END_IF_NEW); //goto END_IF nuova (per il salto del blocco else)
                code.emitLabel(END_IF);  //label END_IF

                if (look.tag == Tag.ELSE){ //blocco if completo 
                    match(Tag.ELSE);
                    stat(code.newLabel());   
                } 
                code.emitLabel(END_IF_NEW); //label END_IF nuova
                match(Tag.END);
                break;

            case '{':
                match('{');
                statlist(actual_label);
                match('}');
                break;

            default:
                error("StatNullError");



        }
    }

    private void idlist(int actual_label, boolean bool) { //bool = 1 read mode, bool = 0 assign mode

        
        Token saved_id = look;
        boolean pop_bool = false; //inizializzazione valore
        match(Tag.ID);

        if (!bool){ //assign mode, nel caso idlist sia stato chiamato ad un istruzione assign
            if (look.tag == ','){ //in caso di assegnazione di uno stesso valore a variabili multiple, occorre duplicare
                code.emit(OpCode.dup); //duplico valore nello stack
                pop_bool = true;
    
            }
        } else { //read mode
            code.emit(OpCode.invokestatic, 0); //leggo l'input prima di fare un istore
        }
        
        modify_stack(saved_id); //modifica stack(istore valore)
        idlistp(actual_label, bool); 
        if (pop_bool){
              code.emit(OpCode.pop); //per la assign_mode, pop finale della variabile duplicata non utilizzata
        }     

    }

    private void idlistp(int actual_label, boolean bool){ //bool = 1 read mode, bool = 0 assign mode

        switch(look.tag){
            case ',':
               match(',');
               if (!bool){ //assign mode
                   code.emit(OpCode.dup); //duplica valore attuale da assegnare agli id successivi
               } else { //read mode
                   code.emit(OpCode.invokestatic, 0); //legge sequenzialmente i valori in input dall'utente
               }
               modify_stack(look); //istore della variabile in entrambi read_mode e assign_mode
               match(Tag.ID);
               idlistp(actual_label, bool);
               break;

            default:
               break;

        }
    }

    private void expr(int actual_label) { 
        switch (look.tag) {
    
            case '-':
                match('-');
                expr(actual_label);
                expr(actual_label);
                code.emit(OpCode.isub);
                break;

            case '/':
                match('/');
                expr(actual_label);
                expr(actual_label);
                code.emit(OpCode.idiv);
                break;

            case '*':
                match('*');
                match('(');
                //assegno op per indicare il tipo di operazione sequenziale da eseguire
                //bool_print = false per ignorare il blocco di print in exprlist
                exprlist(actual_label, 1, false); 
                match(')');
                break;

            case '+':
                match('+');
                match('(');
                //assegno op per indicare il tipo di operazione sequenziale da eseguire
                //bool_print = false per ignorare il blocco di print in exprlist
                exprlist(actual_label, 0, false);
                match(')');
                break;

            case Tag.AND:
                match(Tag.AND);
                match('(');
                //assegno op per indicare il tipo di operazione sequenziale da eseguire
                //bool_print = false per ignorare il blocco di print in exprlist
                exprlist(actual_label, -1, false);
                match(')');
                break;
                
            case Tag.OR:
                match(Tag.OR);
                match('(');
                //assegno op per indicare il tipo di operazione sequenziale da eseguire
                //bool_print = false per ignorare il blocco di print in exprlist
                exprlist(actual_label, -2, false);
                match(')');
                break;

            case '!':
                match('!');
                expr(actual_label); //operatore unario di negazione bitwise 
                code.emit(OpCode.ineg);
                break;

            case Tag.NUM:
                NumberTok numero = (NumberTok) look; //cast del Token a numero per emettere l'aggiunta della costante allo stack
                code.emit(OpCode.ldc, numero.number);
                match(Tag.NUM);
                break;

            case Tag.ID:
                int id_addr = check_address(look);
                if (id_addr == -1){ //se la variabile referenziata non è stata inizializzata, genero errore
                    error("Can't find: " + ((Word)look).lexeme + " in symbol table.");
                }
                code.emit(OpCode.iload, id_addr); //in caso positivo la carico sullo stack
                match(Tag.ID);
                break;



        }
    }

    private void exprlist(int actual_label, int op, boolean bool_print){

        expr(actual_label);
        exprlistp(actual_label, op, bool_print);

    }

    private void exprlistp(int actual_label, int op, boolean bool_print){

        switch(look.tag){

            case ',':

                match(',');
                if (bool_print){ //se il boolean e' positivo effettuo un print della variabile i-esima dell'idlist
                    code.emit(OpCode.invokestatic,1);
                }
                expr(actual_label);
                //gestione operazioni sequenziali con 2 o piu' argomenti
                if (op == 1){ //check if op si mul
                    code.emit(OpCode.imul);
                } else if (op == 0) { //checks if op is add
                    code.emit(OpCode.iadd);
                } else if (op == -1){ //checks if op is and
                    code.emit(OpCode.iand);
                } else if (op == -2){ //checks if op is or
                    code.emit(OpCode.ior);
                }
                exprlistp(actual_label, op, bool_print); //continuo le operazioni con la prossima iterazione
                break;

            default:
               break;
        }

    }

    private void bexpr(int actual_label){
        
        Word previous_look = (Word)look; //salvataggio token RELOP 

        match(Tag.RELOP);
        expr(actual_label);  
        expr(actual_label);  

        //gestisco la generazione del bytecode dei blocchi condizionali
        switch(previous_look.lexeme){
             case "<":
                code.emit(OpCode.if_icmplt, actual_label);
                break;
             case "<=":
                code.emit(OpCode.if_icmple, actual_label);
                break;
             case "<>":
                code.emit(OpCode.if_icmpne, actual_label);
                break;
             case ">=":
                code.emit(OpCode.if_icmpge, actual_label);
                break;
             case ">":
                code.emit(OpCode.if_icmpgt, actual_label);
                break;
             case "==":
                code.emit(OpCode.if_icmpeq, actual_label);
                break;
        }

    }
    
    private void assignlist(int actual_label){

        match('[');
        expr(actual_label);
        match(Tag.TO);
        idlist(actual_label,false); //bool = false in quanto non ho necessita di effettuare dei print
        match(']');
        assignlistp(actual_label);

    }

    private void assignlistp(int actual_label){
        switch (look.tag) {

            case '[':
                assignlist(actual_label);
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
            Translator translator = new Translator(lex, br);
            translator.prog();
            System.out.println("------------Bytecode generated-----------");
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
