package edu.c3341;

import static edu.c3341.TokenKind.AND_OPERATOR;
import static edu.c3341.TokenKind.ASSIGNMENT_OPERATOR;
import static edu.c3341.TokenKind.BRACKET_LEFT;
import static edu.c3341.TokenKind.BRACKET_RIGHT;
import static edu.c3341.TokenKind.COMMA;
import static edu.c3341.TokenKind.EOF;
import static edu.c3341.TokenKind.EQUALITY_TEST;
import static edu.c3341.TokenKind.ERROR;
import static edu.c3341.TokenKind.EXCLAMATION;
import static edu.c3341.TokenKind.IDENTIFIER;
import static edu.c3341.TokenKind.INTEGER_CONSTANT;
import static edu.c3341.TokenKind.LARGER;
import static edu.c3341.TokenKind.LARGER_EQUAL;
import static edu.c3341.TokenKind.LOWER_CASE_WORD;
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

import components.simplereader.SimpleReader;

public class Tokenizer1 implements Tokenizer {

    private String front;
    private String reminder;
    private TokenKind frontKind;

    public Tokenizer1(SimpleReader programIn) {
        this.front = "";
        this.reminder = "";
        while (!programIn.atEOS()) {
            this.reminder += programIn.nextLine();
            this.reminder += " ";
        }
    }

    @Override
    public TokenKind getToken() {
        return this.frontKind;
    }

    @Override
    public void skipToken() {
        if (this.reminder.length() > 0) {
            char firstChar = this.reminder.charAt(0);
            if (firstChar == ' ' || firstChar == '\t' || firstChar == '\n') {
                // handle space/tab/line separator
                this.reminder = this.reminder.substring(1);
                this.skipToken();
            } else if (firstChar == ';') {
                // handle semicolon
                this.front = ";";
                this.frontKind = SEMICOLON;
                this.reminder = this.reminder.substring(1);
            } else if (firstChar == ',') {
                // handle comma
                this.front = ",";
                this.frontKind = COMMA;
                this.reminder = this.reminder.substring(1);
            } else if (firstChar == '=') {
                // handle equality test and assignment operator
                this.reminder = this.reminder.substring(1);
                if (this.reminder.length() > 0) {
                    if (this.reminder.charAt(0) == '=') {
                        this.front = "==";
                        this.frontKind = EQUALITY_TEST;
                        this.reminder = this.reminder.substring(1);
                    } else {
                        this.front = "=";
                        this.frontKind = ASSIGNMENT_OPERATOR;
                    }
                }
            } else if (firstChar == '!') {
                // handle exclamation and not equal test
                this.reminder = this.reminder.substring(1);
                if (this.reminder.length() > 0) {
                    if (this.reminder.charAt(0) == '=') {
                        this.front = "!=";
                        this.frontKind = NOTEQUAL_TEST;
                        this.reminder = this.reminder.substring(1);
                    } else {
                        this.front = "!";
                        this.frontKind = EXCLAMATION;
                    }
                }
            } else if (firstChar == '[') {
                // handle square bracket left
                this.front = "[";
                this.frontKind = SQUAREBRACKET_LEFT;
                this.reminder = this.reminder.substring(1);
            } else if (firstChar == ']') {
                // handle square bracket right
                this.front = "]";
                this.frontKind = SQUAREBRACKET_RIGHT;
                this.reminder = this.reminder.substring(1);
            } else if (firstChar == '&') {
                // handle and operator
                this.reminder = this.reminder.substring(1);
                if (this.reminder.length() > 0
                        && this.reminder.charAt(0) == '&') {
                    this.front = "&&";
                    this.frontKind = AND_OPERATOR;
                    this.reminder = this.reminder.substring(1);
                } else {
                    this.front = "ERROR";
                    this.frontKind = ERROR;
                }
            } else if (firstChar == '|') {
                // handle or operator
                this.reminder = this.reminder.substring(1);
                if (this.reminder.length() > 0
                        && this.reminder.charAt(0) == '|') {
                    this.front = "||";
                    this.frontKind = OR_OPERATOR;
                    this.reminder = this.reminder.substring(1);
                } else {
                    this.front = "ERROR";
                    this.frontKind = ERROR;
                }
            } else if (firstChar == '(') {
                // handle bracket left
                this.front = "(";
                this.frontKind = BRACKET_LEFT;
                this.reminder = this.reminder.substring(1);
            } else if (firstChar == ')') {
                // handle bracket right
                this.front = ")";
                this.frontKind = BRACKET_RIGHT;
                this.reminder = this.reminder.substring(1);
            } else if (firstChar == '+') {
                // handle plus
                this.front = "+";
                this.frontKind = PLUS;
                this.reminder = this.reminder.substring(1);
            } else if (firstChar == '-') {
                // handle minus
                this.front = "-";
                this.frontKind = MINUS;
                this.reminder = this.reminder.substring(1);
            } else if (firstChar == '*') {
                // handle multiply
                this.front = "*";
                this.frontKind = MULTIPLY;
                this.reminder = this.reminder.substring(1);
            } else if (firstChar == '<') {
                // handle smaller and smaller equal
                this.reminder = this.reminder.substring(1);
                if (this.reminder.length() > 0
                        && this.reminder.charAt(0) == '=') {
                    this.front = "<=";
                    this.frontKind = SMALLER_EQUAL;
                    this.reminder = this.reminder.substring(1);
                } else {
                    this.front = "<";
                    this.frontKind = SMALLER;
                }
            } else if (firstChar == '>') {
                // handle larger and larger equal
                this.reminder = this.reminder.substring(1);
                if (this.reminder.length() > 0
                        && this.reminder.charAt(0) == '=') {
                    this.front = ">=";
                    this.frontKind = LARGER_EQUAL;
                    this.reminder = this.reminder.substring(1);
                } else {
                    this.front = ">";
                    this.frontKind = LARGER;
                }
            } else if (firstChar >= 65 && firstChar <= 90) {
                // handle identifier
                this.front = "";
                this.front += this.reminder.charAt(0);
                this.reminder = this.reminder.substring(1);
                this.frontKind = IDENTIFIER;
                boolean flag = false;
                while (this.reminder.length() > 0) {
                    if (this.reminder.charAt(0) >= 65
                            && this.reminder.charAt(0) <= 90) {
                        if (flag) {
                            this.front = "ERROR";
                            this.frontKind = ERROR;
                            break;
                        }
                        this.front += this.reminder.charAt(0);
                        this.reminder = this.reminder.substring(1);
                    } else if (this.reminder.charAt(0) >= 97
                            && this.reminder.charAt(0) <= 122) {
                        this.front = "ERROR";
                        this.frontKind = ERROR;
                        break;
                    } else if (this.reminder.charAt(0) >= 48
                            && this.reminder.charAt(0) <= 57) {
                        flag = true;
                        this.front += this.reminder.charAt(0);
                        this.reminder = this.reminder.substring(1);
                    } else {
                        break;
                    }
                }
            } else if (this.reminder.charAt(0) >= 48
                    && this.reminder.charAt(0) <= 57) {
                // handle integer constant
                this.front = "";
                this.front += this.reminder.charAt(0);
                this.reminder = this.reminder.substring(1);
                this.frontKind = INTEGER_CONSTANT;
                while (this.reminder.length() > 0) {
                    if (this.reminder.charAt(0) >= 65
                            && this.reminder.charAt(0) <= 90) {
                        this.front = "ERROR";
                        this.frontKind = ERROR;
                        break;
                    } else if (this.reminder.charAt(0) >= 97
                            && this.reminder.charAt(0) <= 122) {
                        this.front = "ERROR";
                        this.frontKind = ERROR;
                        break;
                    } else if (this.reminder.charAt(0) >= 48
                            && this.reminder.charAt(0) <= 57) {
                        this.front += this.reminder.charAt(0);
                        this.reminder = this.reminder.substring(1);
                    } else {
                        break;
                    }
                }
            } else if (this.reminder.charAt(0) >= 97
                    && this.reminder.charAt(0) <= 122) {
                // handle lower case word
                this.front = "";
                this.front += this.reminder.charAt(0);
                this.reminder = this.reminder.substring(1);
                this.frontKind = LOWER_CASE_WORD;
                while (this.reminder.length() > 0) {
                    if (this.reminder.charAt(0) >= 65
                            && this.reminder.charAt(0) <= 90) {
                        this.front = "ERROR";
                        this.frontKind = ERROR;
                        break;
                    } else if (this.reminder.charAt(0) >= 97
                            && this.reminder.charAt(0) <= 122) {
                        this.front += this.reminder.charAt(0);
                        this.reminder = this.reminder.substring(1);
                    } else if (this.reminder.charAt(0) >= 48
                            && this.reminder.charAt(0) <= 57) {
                        this.front = "ERROR";
                        this.frontKind = ERROR;
                        break;
                    } else {
                        break;
                    }
                }
                // change LOWER_CASE_WORD to exact type
                if (this.frontKind.equals(LOWER_CASE_WORD)) {
                    switch (this.front) {
                        case "program":
                            this.front = "program";
                            this.frontKind = WORD_PROGRAM;
                            break;
                        case "begin":
                            this.front = "program";
                            this.frontKind = WORD_BEGIN;
                            break;
                        case "end":
                            this.front = "end";
                            this.frontKind = WORD_END;
                            break;
                        case "int":
                            this.front = "int";
                            this.frontKind = WORD_INT;
                            break;
                        case "if":
                            this.front = "if";
                            this.frontKind = WORD_IF;
                            break;
                        case "then":
                            this.front = "then";
                            this.frontKind = WORD_THEN;
                            break;
                        case "else":
                            this.front = "else";
                            this.frontKind = WORD_ELSE;
                            break;
                        case "while":
                            this.front = "while";
                            this.frontKind = WORD_WHILE;
                            break;
                        case "loop":
                            this.front = "loop";
                            this.frontKind = WORD_LOOP;
                            break;
                        case "read":
                            this.front = "read";
                            this.frontKind = WORD_READ;
                            break;
                        case "write":
                            this.front = "write";
                            this.frontKind = WORD_WRITE;
                            break;
                        default:
                            this.front = "ERROR";
                            this.frontKind = ERROR;
                            break;
                    }
                }
            } else {
                this.front = "ERROR";
                this.frontKind = ERROR;
            }
        } else {
            this.front = "EOF";
            this.frontKind = EOF;
        }
    }

    /*
     * For Part 1 of the Core Interpreter project, the following two methods
     * need not be implemented.
     */
    @Override
    public int intVal() {
        return Integer.parseInt(this.front);
    }

    @Override
    public String idName() {
        return this.front;
    }

}
