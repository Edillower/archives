package edu.c3341;

import java.io.IOException;

import components.map.Map;
import components.map.Map2;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;

public class Executer {

    static Map<String, Integer> ids = new Map2<String, Integer>();
    static boolean headercheck = false;
    static SimpleReader fileIn = null;

    public static void mainExecuter(ParseTree p) throws IOException {

        while (true) {
            ids.clear();
            headercheck = false;
            System.out.print("Enter your input data file: ");
            SimpleReader sysIn = new SimpleReader1L();
            String fileName = sysIn.nextLine();
            if (fileName.equals("executer.exit")) {
                break;
            } else {
                if (fileName.length() != 0) {
                    fileIn = new SimpleReader1L(fileName);
                }
                execProgram(p);
                if (fileName.length() != 0) {
                    fileIn.close();
                }
            }
        }
    }

    public static void execProgram(ParseTree p) throws IOException {
        getDeclSeq(p.children.entry(0));
        headercheck = true;
        execStmtSeq(p.children.entry(1));
    }

    public static void getDeclSeq(ParseTree p) throws IOException {
        for (int i = 0; i < p.children.length(); i++) {
            getDecl(p.children.entry(i));
        }
    }

    public static void getDecl(ParseTree p) throws IOException {
        getIdList(p.children.entry(0));
    }

    public static void getIdList(ParseTree p) throws IOException {
        for (int i = 0; i < p.children.length(); i++) {
            getId(p.children.entry(i));
        }
    }

    public static String getId(ParseTree p) throws IOException {
        String idName = p.attributes;
        if (headercheck) {
            if (!ids.hasKey(idName)) {
                System.out.println("ERROR: " + idName + " is not declared.");
                System.exit(0);
            }
        } else {
            ids.add(idName, null);
        }
        return idName;
    }

    public static void execStmtSeq(ParseTree p) throws IOException {
        for (int i = 0; i < p.children.length(); i++) {
            execStmt(p.children.entry(i));
        }
    }

    public static void execStmt(ParseTree p) throws IOException {
        String nodeKind = p.kind;
        if (nodeKind.equals("stmtAssign")) {
            execAssign(p.children.entry(0));
        } else if (nodeKind.equals("stmtIf")) {
            execIf(p.children.entry(0));
        } else if (nodeKind.equals("stmtLoop")) {
            execLoop(p.children.entry(0));
        } else if (nodeKind.equals("stmtIn")) {
            execIn(p.children.entry(0));
        } else if (nodeKind.equals("stmtOut")) {
            execOut(p.children.entry(0));
        }

    }

    public static void execAssign(ParseTree p) throws IOException {
        String idName = getId(p.children.entry(0));
        int result = execExp(p.children.entry(1));
        ids.remove(idName);
        ids.add(idName, result);
    }

    public static void execIf(ParseTree p) throws IOException {
        String nodeKind = p.kind;
        boolean conti = execCond(p.children.entry(0));
        if (conti) {
            execStmtSeq(p.children.entry(1));
        } else {
            if (nodeKind.equals("ifElse")) {
                execStmtSeq(p.children.entry(2));
            }
        }
    }

    public static void execLoop(ParseTree p) throws IOException {
        while (execCond(p.children.entry(0))) {
            execStmtSeq(p.children.entry(1));
        }
    }

    public static void execIn(ParseTree p) throws IOException {
        try {
            readIdList(p.children.entry(0));
        } catch (NullPointerException e) {
            System.out.println("No input data is given");
            System.exit(0);
        }
    }

    public static void readIdList(ParseTree p) throws IOException {
        for (int i = 0; i < p.children.length(); i++) {
            readId(p.children.entry(i));
        }
    }

    public static void readId(ParseTree p) throws IOException {
        String idName = p.attributes;
        if (!ids.hasKey(idName)) {
            System.out.println("ERROR: " + idName + " is not declared.");
            System.exit(0);
        } else {
            ids.remove(idName);
            if (!fileIn.atEOS()) {
                try {
                    int value = Integer.parseInt(fileIn.nextLine());
                    ids.add(idName, value);
                } catch (NumberFormatException e) {
                    System.out.println("Wrong type of input data");
                    System.exit(0);
                }
            } else {
                System.out
                        .println("Input data is not enough for read statement");
                System.exit(0);
            }
        }
    }

    public static void execOut(ParseTree p) throws IOException {
        writeIdList(p.children.entry(0));
    }

    public static void writeIdList(ParseTree p) throws IOException {
        for (int i = 0; i < p.children.length(); i++) {
            writeId(p.children.entry(i));
        }
    }

    public static void writeId(ParseTree p) throws IOException {
        String idName = p.attributes;
        System.out.println(idName + "=" + ids.value(idName));
    }

    public static boolean execCond(ParseTree p) throws IOException {
        boolean result = true;
        String nodeKind = p.kind;
        if (nodeKind.equals("cond")) {
            result = execComp(p.children.entry(0));
        } else if (nodeKind.equals("cond!")) {
            result = !execCond(p.children.entry(0));
        } else if (nodeKind.equals("cond&&")) {
            result = execCond(p.children.entry(0))
                    && execCond(p.children.entry(1));
        } else if (nodeKind.equals("cond||")) {
            result = execCond(p.children.entry(0))
                    || execCond(p.children.entry(1));
        }
        return result;
    }

    public static boolean execComp(ParseTree p) throws IOException {
        int a = execOp(p.children.entry(0));
        int b = execOp(p.children.entry(2));
        boolean result = execCompOp(p.children.entry(1), a, b);
        return result;
    }

    public static int execExp(ParseTree p) throws IOException {
        int result = execTrm(p.children.entry(0));
        if (p.kind.equals("exp+")) {
            result += execExp(p.children.entry(1));
        } else if (p.kind.equals("exp-")) {
            result -= execExp(p.children.entry(1));
        }
        return result;
    }

    public static int execTrm(ParseTree p) throws IOException {
        int result = execOp(p.children.entry(0));
        if (p.kind.equals("trm*")) {
            result *= execExp(p.children.entry(1));
        }
        return result;
    }

    public static int execOp(ParseTree p) throws IOException {
        int result = 0;
        String nodeKind = p.kind;
        if (nodeKind.equals("opNo")) {
            result = Integer.parseInt(p.attributes);
        } else if (nodeKind.equals("opId")) {
            String idName = getId(p.children.entry(0));
            if (ids.value(idName) != null) {
                result = ids.value(idName);
            } else {
                System.out.println("Error: " + idName + " is not initialized");
                System.exit(0);
            }
        } else if (nodeKind.equals("opExp")) {
            result = execExp(p.children.entry(0));
        }
        return result;
    }

    public static boolean execCompOp(ParseTree p, int a, int b)
            throws IOException {
        String nodeKind = p.kind;
        if (nodeKind.equals("compOp!=")) {
            return a != b;
        } else if (nodeKind.equals("compOp==")) {
            return a == b;
        } else if (nodeKind.equals("compOp<")) {
            return a < b;
        } else if (nodeKind.equals("compOp>")) {
            return a > b;
        } else if (nodeKind.equals("compOp<=")) {
            return a <= b;
        } else if (nodeKind.equals("compOp>=")) {
            return a >= b;
        }
        return true;
    }
}
