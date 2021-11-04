package com.github.mc1arke.sonarqube.plugin.ce.pullrequest.markup;

public final class Line extends Node {

    public Line(Node... children) {
        super(children);
    }

    @Override
    boolean isValidChild(Node child) {
        return child instanceof Image || child instanceof Text || child instanceof Link;
    }
}
