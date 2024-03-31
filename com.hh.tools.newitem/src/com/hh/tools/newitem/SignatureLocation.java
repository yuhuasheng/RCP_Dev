package com.hh.tools.newitem;

public class SignatureLocation {

    private int page;
    private String name;
    private float x;
    private float y;
    private float angle;
    private float fontsize;

    public SignatureLocation(int page, String name, float x, float y, float angle, float fontsize) {
        this.page = page;
        this.name = name;
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.fontsize = fontsize;
    }

    public String getName() {
        return this.name;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getAngle() {
        return this.angle;
    }

    public float getFontSize() {
        return this.fontsize;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
