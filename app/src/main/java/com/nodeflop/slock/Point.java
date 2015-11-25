package com.nodeflop.slock;

import java.io.Serializable;

public class Point implements Serializable{
    private double x;
    private double y;

    public Point(double x, double y){
        set_x(x);
        set_y(y);
    }

    public void set_x(double x){
        this.x = x;
    }

    public void set_y(double y){
        this.y = y;
    }

    public double get_x(){
        return this.x;
    }

    public double get_y(){
        return this.y;
    }

    @Override
    public int hashCode() {
        return 31 * (int)x + (int)y;
    }

    @Override
    public String toString(){
        return "("+x+","+y+")";
    }
}
