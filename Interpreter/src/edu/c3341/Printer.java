package edu.c3341;

import java.io.IOException;

public class Printer {

    public static void printProgram(ParseTree p) throws IOException {
        System.out.println("program ");
        printDeclSeq(p.children.entry(0));
        System.out.println("begin ");
        printStmtSeq(p.children.entry(1), "    ");
        System.out.println("end");
    }

    public static void printDeclSeq(ParseTree p) throws IOException {
        for (int i = 0; i < p.children.length(); i++) {
            System.out.print("    ");
            printDecl(p.children.entry(i));
        }
    }

    public static void printDecl(ParseTree p) throws IOException {
        System.out.print("int ");
        printIdList(p.children.entry(0));
        System.out.println("; ");
    }

    public static void printIdList(ParseTree p) throws IOException {
        for (int i = 0; i < p.children.length(); i++) {
            printId(p.children.entry(i));
            if (i != p.children.length() - 1) {
                System.out.print(", ");
            }
        }
    }

    public static void printId(ParseTree p) throws IOException {
        System.out.print(p.attributes);
    }

    public static void printStmtSeq(ParseTree p, String space)
            throws IOException {
        for (int i = 0; i < p.children.length(); i++) {
            System.out.print(space);
            printStmt(p.children.entry(i), space);
        }
    }

    public static void printStmt(ParseTree p, String space) throws IOException {
        String nodeKind = p.kind;
        if (nodeKind.equals("stmtAssign")) {
            printAssign(p.children.entry(0));
        } else if (nodeKind.equals("stmtIf")) {
            printIf(p.children.entry(0), space);
        } else if (nodeKind.equals("stmtLoop")) {
            printLoop(p.children.entry(0), space);
        } else if (nodeKind.equals("stmtIn")) {
            printIn(p.children.entry(0));
        } else if (nodeKind.equals("stmtOut")) {
            printOut(p.children.entry(0));
        }

    }

    public static void printAssign(ParseTree p) throws IOException {
        printId(p.children.entry(0));
        System.out.print("=");
        printExp(p.children.entry(1));
        System.out.println("; ");
    }

    public static void printIf(ParseTree p, String space) throws IOException {
        String nodeKind = p.kind;
        System.out.print("if ");
        printCond(p.children.entry(0));
        System.out.println(" then ");
        String sublineSpace = space + "    ";
        printStmtSeq(p.children.entry(1), sublineSpace);
        if (nodeKind.equals("if")) {
            System.out.println(space + "end; ");
        } else {
            System.out.println(space + "else ");
            printStmtSeq(p.children.entry(2), sublineSpace);
            System.out.println(space + "end; ");
        }
    }

    public static void printLoop(ParseTree p, String space) throws IOException {
        System.out.print("while ");
        printCond(p.children.entry(0));
        System.out.println("loop ");
        printStmtSeq(p.children.entry(1), space + "    ");
        System.out.println(space + "end; ");
    }

    public static void printIn(ParseTree p) throws IOException {
        System.out.print("read ");
        printIdList(p.children.entry(0));
        System.out.println("; ");
    }

    public static void printOut(ParseTree p) throws IOException {
        System.out.print("write ");
        printIdList(p.children.entry(0));
        System.out.println("; ");
    }

    public static void printCond(ParseTree p) throws IOException {
        String nodeKind = p.kind;
        if (nodeKind.equals("cond")) {
            printComp(p.children.entry(0));
        } else if (nodeKind.equals("cond!")) {
            System.out.print("!");
            printCond(p.children.entry(0));
        } else if (nodeKind.equals("cond&&")) {
            System.out.print("[");
            printCond(p.children.entry(0));
            System.out.print(" && ");
            printCond(p.children.entry(1));
            System.out.print("] ");
        } else if (nodeKind.equals("cond||")) {
            System.out.print("[");
            printCond(p.children.entry(0));
            System.out.print(" || ");
            printCond(p.children.entry(1));
            System.out.print("]");
        }
    }

    public static void printComp(ParseTree p) throws IOException {
        System.out.print("(");
        printOp(p.children.entry(0));
        printCompOp(p.children.entry(1));
        printOp(p.children.entry(2));
        System.out.print(")");
    }

    public static void printExp(ParseTree p) throws IOException {
        printTrm(p.children.entry(0));
        if (p.kind.equals("exp+")) {
            System.out.print("+");
            printExp(p.children.entry(1));
        } else if (p.kind.equals("exp-")) {
            System.out.print("-");
            printExp(p.children.entry(1));
        }
    }

    public static void printTrm(ParseTree p) throws IOException {
        printOp(p.children.entry(0));
        if (p.kind.equals("trm*")) {
            System.out.print("*");
            printExp(p.children.entry(1));
        }
    }

    public static void printOp(ParseTree p) throws IOException {
        String nodeKind = p.kind;
        if (nodeKind.equals("opNo")) {
            System.out.print(p.attributes);
        } else if (nodeKind.equals("opId")) {
            printId(p.children.entry(0));
        } else if (nodeKind.equals("opExp")) {
            System.out.print("(");
            printExp(p.children.entry(0));
            System.out.print(")");
        }
    }

    public static void printCompOp(ParseTree p) throws IOException {
        String nodeKind = p.kind;
        if (nodeKind.equals("compOp!=")) {
            System.out.print("!=");
        } else if (nodeKind.equals("compOp==")) {
            System.out.print("==");
        } else if (nodeKind.equals("compOp<")) {
            System.out.print("<");
        } else if (nodeKind.equals("compOp>")) {
            System.out.print(">");
        } else if (nodeKind.equals("compOp<=")) {
            System.out.print("<=");
        } else if (nodeKind.equals("compOp>=")) {
            System.out.print(">=");
        }
    }
}
