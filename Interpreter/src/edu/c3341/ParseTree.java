package edu.c3341;

import components.sequence.Sequence;
import components.sequence.Sequence1L;

public class ParseTree {
    public String kind;
    public String attributes;
    public Sequence<ParseTree> children = new Sequence1L<ParseTree>();

    public ParseTree(String nodeKind, String attr) {
        this.kind = nodeKind;
        this.attributes = attr;
        this.children = new Sequence1L<ParseTree>();
    }

    public void addChild(ParseTree child) {
        this.children.add(this.children.length(), child);
    }

}
