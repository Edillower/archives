package edu.c3341;

import static edu.c3341.TokenKind.AND_OPERATOR;
import static edu.c3341.TokenKind.ASSIGNMENT_OPERATOR;
import static edu.c3341.TokenKind.BRACKET_LEFT;
import static edu.c3341.TokenKind.BRACKET_RIGHT;
import static edu.c3341.TokenKind.COMMA;
import static edu.c3341.TokenKind.EQUALITY_TEST;
import static edu.c3341.TokenKind.ERROR;
import static edu.c3341.TokenKind.EXCLAMATION;
import static edu.c3341.TokenKind.IDENTIFIER;
import static edu.c3341.TokenKind.INTEGER_CONSTANT;
import static edu.c3341.TokenKind.LARGER;
import static edu.c3341.TokenKind.LARGER_EQUAL;
import static edu.c3341.TokenKind.MINUS;
import static edu.c3341.TokenKind.MULTIPLY;
import static edu.c3341.TokenKind.NOTEQUAL_TEST;
import static edu.c3341.TokenKind.OR_OPERATOR;
import static edu.c3341.TokenKind.PLUS;
import static edu.c3341.TokenKind.SEMICOLON;
import static edu.c3341.TokenKind.SMALLER;
import static edu.c3341.TokenKind.SMALLER_EQUAL;
import static edu.c3341.TokenKind.SQUAREBRACKET_LEFT;
import static edu.c3341.TokenKind.SQUAREBRACKET_RIGHT;
import static edu.c3341.TokenKind.WORD_BEGIN;
import static edu.c3341.TokenKind.WORD_ELSE;
import static edu.c3341.TokenKind.WORD_END;
import static edu.c3341.TokenKind.WORD_IF;
import static edu.c3341.TokenKind.WORD_INT;
import static edu.c3341.TokenKind.WORD_LOOP;
import static edu.c3341.TokenKind.WORD_PROGRAM;
import static edu.c3341.TokenKind.WORD_READ;
import static edu.c3341.TokenKind.WORD_THEN;
import static edu.c3341.TokenKind.WORD_WHILE;
import static edu.c3341.TokenKind.WORD_WRITE;

import java.io.IOException;

import components.set.Set;
import components.set.Set2;

public class Parser {

    static Set<String> idList = new Set2<String>();

    static boolean headercheck = false;

    public static ParseTree parseProgram(Tokenizer in) throws IOException {
        ParseTree p = new ParseTree("program", "");
        if (in.getToken() != WORD_PROGRAM || in.getToken() == ERROR) {
            System.out.println("key word is expected: program");
            System.exit(0);
        }
        in.skipToken();
        p.addChild(parseDeclSeq(in));
        headercheck = true;
        if (in.getToken() != WORD_BEGIN || in.getToken() == ERROR) {
            System.out.println("key word is expected: begin");
            System.exit(0);
        }
        in.skipToken();
        p.addChild(parseStmtSeq(in));
        if (in.getToken() != WORD_END || in.getToken() == ERROR) {
            System.out.println("key word is expected: end");
            System.exit(0);
        }
        in.skipToken();
        return p;
    }

    public static ParseTree parseDeclSeq(Tokenizer in) throws IOException {
        ParseTree p = new ParseTree("declSeq", "");
        p.addChild(parseDecl(in));
        if (in.getToken() != WORD_BEGIN) {
            if (in.getToken() == ERROR) {
                System.out.println("ERROR: Check your grammar!");
                System.exit(0);
            }
            p.addChild(parseDecl(in));
        }
        return p;
    }

    public static ParseTree parseDecl(Tokenizer in) throws IOException {
        ParseTree p = new ParseTree("decl", "");
        if (in.getToken() != WORD_INT || in.getToken() == ERROR) {
            System.out.println("key word is expected: int");
            System.exit(0);
        }
        in.skipToken();
        p.addChild(parseIdList(in));
        if (in.getToken() != SEMICOLON || in.getToken() == ERROR) {
            System.out.println("key word is expected: ;");
            System.exit(0);
        }
        in.skipToken();
        return p;
    }

    public static ParseTree parseIdList(Tokenizer in) throws IOException {
        ParseTree p = new ParseTree("idList", "");
        ParseTree c = parseId(in);
        p.addChild(c);
        idList.add(c.attributes);
        while (in.getToken() != SEMICOLON) {
            if (in.getToken() == ERROR) {
                System.out.println("ERROR: Check your grammar!");
                System.exit(0);
            }
            if (in.getToken() != COMMA || in.getToken() == ERROR) {
                System.out.println("key word is expected: ,");
                System.exit(0);
            }
            in.skipToken();

            c = parseId(in);
            String idName = c.attributes;
            if (idList.contains(idName)) {
                if (!headercheck) {
                    System.out.println("an id is declared more than once: "
                            + idName);
                    System.exit(0);
                }
            } else {
                idList.add(idName);
            }
            p.addChild(c);

        }
        return p;
    }

    public static ParseTree parseId(Tokenizer in) throws IOException {

        if (in.getToken() != IDENTIFIER) {
            if (in.getToken() == ERROR) {
                System.out.println("ERROR: Check your grammar!");
                System.exit(0);
            }
            System.out.println("an identifier is expected");
            System.exit(0);
        }
        String idName = in.idName();
        in.skipToken();
        ParseTree p = new ParseTree("id", idName);
        return p;
    }

    public static ParseTree parseStmtSeq(Tokenizer in) throws IOException {
        ParseTree p = new ParseTree("stmtSeq", "");
        p.addChild(parseStmt(in));
        while (in.getToken() != WORD_END) {
            if (in.getToken() == ERROR) {
                System.out.println("ERROR: Check your grammar!");
                System.exit(0);
            }
            p.addChild(parseStmt(in));
        }
        return p;
    }

    public static ParseTree parseIfElseStmtSeq(Tokenizer in) throws IOException {
        ParseTree p = new ParseTree("stmtSeq", "");
        p.addChild(parseStmt(in));
        while (!(in.getToken() == WORD_END || in.getToken() == WORD_ELSE)) {
            if (in.getToken() == ERROR) {
                System.out.println("ERROR: Check your grammar!");
                System.exit(0);
            }
            p.addChild(parseStmt(in));
        }
        return p;
    }

    public static ParseTree parseStmt(Tokenizer in) throws IOException {
        ParseTree p = new ParseTree("stmt", "");
        TokenKind front = in.getToken();
        if (front == ERROR) {
            System.out.println("ERROR: Check your grammar!");
            System.exit(0);
        } else {
            if (front == IDENTIFIER) {
                p.kind = "stmtAssign";
                p.addChild(parseAssign(in));
            } else if (front == WORD_IF) {
                p.kind = "stmtIf";
                p.addChild(parseIf(in));
            } else if (front == WORD_WHILE) {
                p.kind = "stmtLoop";
                p.addChild(parseLoop(in));
            } else if (front == WORD_READ) {
                p.kind = "stmtIn";
                p.addChild(parseIn(in));
            } else if (front == WORD_WRITE) {
                p.kind = "stmtOut";
                p.addChild(parseOut(in));
            } else {
                System.out.println(front);
                System.out.println("a correct statement is expected");
                System.exit(0);
            }
        }
        return p;
    }

    public static ParseTree parseAssign(Tokenizer in) throws IOException {
        ParseTree p = new ParseTree("assign", "");
        p.addChild(parseId(in));
        if (in.getToken() != ASSIGNMENT_OPERATOR || in.getToken() == ERROR) {
            System.out.println("key word is expected: =");
            System.exit(0);
        }
        in.skipToken();
        p.addChild(parseExp(in));
        if (in.getToken() != SEMICOLON || in.getToken() == ERROR) {
            System.out.println("key word is expected: ;");
            System.exit(0);
        }
        in.skipToken();
        return p;
    }

    public static ParseTree parseIf(Tokenizer in) throws IOException {
        in.skipToken();
        ParseTree p = new ParseTree("if", "");
        p.addChild(parseCond(in));

        if (in.getToken() != WORD_THEN || in.getToken() == ERROR) {
            System.out.println("key word is expected: then");
            System.exit(0);
        }
        in.skipToken();

        p.addChild(parseIfElseStmtSeq(in));

        if (in.getToken() == WORD_END) {
            in.skipToken();
            if (in.getToken() != SEMICOLON || in.getToken() == ERROR) {
                System.out.println("key word is expected: ;");
                System.exit(0);
            }
            in.skipToken();
        } else if (in.getToken() == WORD_ELSE) {
            p.kind = "ifElse";
            in.skipToken();
            p.addChild(parseStmtSeq(in));
            if (in.getToken() != WORD_END || in.getToken() == ERROR) {
                System.out.println("key word is expected: end");
                System.exit(0);
            }
            in.skipToken();
            if (in.getToken() != SEMICOLON || in.getToken() == ERROR) {
                System.out.println("key word is expected: ;");
                System.exit(0);
            }
            in.skipToken();
        } else {
            System.out.println("key word is expected: end or else");
            System.exit(0);
        }
        return p;
    }

    public static ParseTree parseLoop(Tokenizer in) throws IOException {
        in.skipToken();
        ParseTree p = new ParseTree("while", "");

        p.addChild(parseCond(in));

        if (in.getToken() != WORD_LOOP || in.getToken() == ERROR) {
            System.out.println("key word is expected: loop");
            System.exit(0);
        }
        in.skipToken();

        p.addChild(parseStmtSeq(in));

        if (in.getToken() != WORD_END || in.getToken() == ERROR) {
            System.out.println("key word is expected: end");
            System.exit(0);
        }
        in.skipToken();
        if (in.getToken() != SEMICOLON || in.getToken() == ERROR) {
            System.out.println("key word is expected: ;");
            System.exit(0);
        }
        in.skipToken();

        return p;
    }

    public static ParseTree parseIn(Tokenizer in) throws IOException {
        ParseTree p = new ParseTree("read", "");
        in.skipToken();

        p.addChild(parseIdList(in));

        if (in.getToken() != SEMICOLON || in.getToken() == ERROR) {
            System.out.println("key word is expected: ;");
            System.exit(0);
        }
        in.skipToken();
        return p;
    }

    public static ParseTree parseOut(Tokenizer in) throws IOException {
        ParseTree p = new ParseTree("write", "");
        in.skipToken();

        p.addChild(parseIdList(in));

        if (in.getToken() != SEMICOLON || in.getToken() == ERROR) {
            System.out.println("key word is expected: ;");
            System.exit(0);
        }
        in.skipToken();
        return p;
    }

    public static ParseTree parseCond(Tokenizer in) throws IOException {
        ParseTree p = new ParseTree("cond", "");
        TokenKind front = in.getToken();
        if (front == BRACKET_LEFT) {
            p.addChild(parseComp(in));
        } else if (front == EXCLAMATION) {
            in.skipToken();
            p.kind = "cond!";
            p.addChild(parseCond(in));
        } else if (front == SQUAREBRACKET_LEFT) {
            in.skipToken();
            p.addChild(parseCond(in));
            TokenKind nextFront = in.getToken();
            in.skipToken();
            if (nextFront == AND_OPERATOR) {
                p.kind = "cond&&";
                p.addChild(parseCond(in));
                if (in.getToken() != SQUAREBRACKET_RIGHT
                        || in.getToken() == ERROR) {
                    System.out.println("key word is expected: ]");
                    System.exit(0);
                }
                in.skipToken();
            } else if (nextFront == OR_OPERATOR) {
                p.kind = "cond||";
                p.addChild(parseCond(in));
                if (in.getToken() != SQUAREBRACKET_RIGHT
                        || in.getToken() == ERROR) {
                    System.out.println("key word is expected: ]");
                    System.exit(0);
                }
                in.skipToken();
            } else {
                System.out.println("key word is expected: && or ||");
                System.exit(0);
            }
        } else {
            System.out.println("ERROR: please check your grammar!");
            System.exit(0);
        }
        return p;
    }

    public static ParseTree parseComp(Tokenizer in) throws IOException {
        in.skipToken();
        ParseTree p = new ParseTree("comp", "");

        p.addChild(parseOp(in));

        p.addChild(parseCompOp(in));

        p.addChild(parseOp(in));

        if (in.getToken() != BRACKET_RIGHT || in.getToken() == ERROR) {
            System.out.println("key word is expected: )");
            System.exit(0);
        }
        in.skipToken();

        return p;
    }

    public static ParseTree parseExp(Tokenizer in) throws IOException {
        ParseTree p = new ParseTree("exp", "");

        p.addChild(parseTrm(in));

        TokenKind front = in.getToken();

        if (front == PLUS) {
            p.kind = "exp+";
            in.skipToken();
            p.addChild(parseExp(in));
        } else if (front == MINUS) {
            p.kind = "exp-";
            in.skipToken();
            p.addChild(parseExp(in));
        }

        return p;
    }

    public static ParseTree parseTrm(Tokenizer in) throws IOException {
        ParseTree p = new ParseTree("trm", "");

        p.addChild(parseOp(in));

        if (in.getToken() == MULTIPLY) {
            p.kind = "trm*";
            in.skipToken();
            p.addChild(parseTrm(in));
        }

        return p;
    }

    public static ParseTree parseOp(Tokenizer in) throws IOException {
        ParseTree p = new ParseTree("op", "");
        TokenKind front = in.getToken();
        if (front == INTEGER_CONSTANT) {
            p.kind = "opNo";
            p.attributes = in.intVal() + "";
            in.skipToken();
        } else if (front == IDENTIFIER) {
            p.kind = "opId";
            p.addChild(parseId(in));
        } else if (front == BRACKET_LEFT) {
            p.kind = "opExp";
            in.skipToken();
            p.addChild(parseExp(in));
            if (in.getToken() != BRACKET_RIGHT || in.getToken() == ERROR) {
                System.out.println("key word is expected: )");
                System.exit(0);
            }
            in.skipToken();
        } else {
            System.out.println("ERROR: please check your grammar!");
            System.exit(0);
        }

        return p;
    }

    public static ParseTree parseCompOp(Tokenizer in) throws IOException {
        ParseTree p = new ParseTree("compOp", "");
        TokenKind front = in.getToken();
        if (front == NOTEQUAL_TEST) {
            p.kind = "compOp!=";
            in.skipToken();
        } else if (front == EQUALITY_TEST) {
            p.kind = "compOp==";
            in.skipToken();
        } else if (front == SMALLER) {
            p.kind = "compOp<";
            in.skipToken();
        } else if (front == LARGER) {
            p.kind = "compOp>";
            in.skipToken();
        } else if (front == SMALLER_EQUAL) {
            p.kind = "compOp<=";
            in.skipToken();
        } else if (front == LARGER_EQUAL) {
            p.kind = "compOp>=";
            in.skipToken();
        } else {
            System.out
                    .println("key word is expected: != or == or < or > or <= or >=");
            System.exit(0);
        }
        return p;
    }

}
