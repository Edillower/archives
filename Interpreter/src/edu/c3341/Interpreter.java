package edu.c3341;

import java.io.IOException;

import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;

public class Interpreter {
    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     * @throws IOException
     */
    public static void main(String[] args) {
        System.out.print("Enter your program file: ");
        SimpleReader sysIn = new SimpleReader1L();
        SimpleReader programIn = new SimpleReader1L(sysIn.nextLine());

        Tokenizer t = new Tokenizer1(programIn);
        t.skipToken();
        try {
            ParseTree program = Parser.parseProgram(t);
            Printer.printProgram(program);
            Executer.mainExecuter(program);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        /*
         * Close input stream
         */
        sysIn.close();
        programIn.close();
    }
}
