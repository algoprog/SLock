package com.nodeflop.slock;

import java.util.ArrayList;

/**
 * Created by Chris on 17/10/2015.
 */
public class GestureChecker {

    private static ArrayList<Point> normalize(ArrayList<Point> data){
        ArrayList<Point> points = new ArrayList<Point>();
        points.addAll(data);

        int frame_width = 1920;
        int frame_height = 1080;

        Point first = points.get(0);
        double dx = first.get_x();
        double dy = first.get_y();

        double max_x = -1;
        double min_x = frame_width+1;
        double max_y = -1;
        double min_y = frame_height+1;

        for (Point p : points) {
            p.set_x(p.get_x() - dx);
            p.set_y(p.get_y() - dy);

            double x = p.get_x();
            double y = p.get_y();

            if(x>max_x){
                max_x = x;
            }else if(x<min_x){
                min_x = x;
            }
            if(y>max_y){
                max_y = y;
            }else if(y<min_y){
                min_y = y;
            }
        }

        points.remove(0);

        double gesture_width = Math.abs(max_x-min_x);
        double gesture_height = Math.abs(max_y-min_y);
        double width_ratio = 1;
        double height_ratio = 1;
        if(gesture_width>0){
            width_ratio = frame_width/gesture_width;
        }
        if(gesture_height>0){
            height_ratio = frame_height/gesture_height;
        }
        double resize_ratio = Math.min(width_ratio,height_ratio);

        ArrayList<Point> normalized_points = new ArrayList<Point>();

        normalized_points.add(new Point(0,0));
        for(Point p : points){
            normalized_points.add(new Point(resize_ratio*p.get_x(),resize_ratio*p.get_y()));
        }

        return normalized_points;
    }

    public static boolean check(ArrayList<Point> gesture1, ArrayList<Point> gesture2){
        ArrayList<Point> saved_gesture = normalize(gesture1);
        ArrayList<Point> current_gesture = normalize(gesture2);

        double a_dists = 0;
        double b_dists = 0;

        for(Point a : saved_gesture){
            double min_dist = 9999;
            for(Point b: current_gesture){
                double dist = distance(a,b);
                if(dist<min_dist){
                    min_dist = dist;
                }
            }
            a_dists += min_dist;
        }
        for(Point b : current_gesture){
            double min_dist = 9999;
            for(Point a: saved_gesture){
                double dist = distance(a,b);
                if(dist<min_dist){
                    min_dist = dist;
                }
            }
            b_dists += min_dist;
        }
        double avg_dist = (a_dists+b_dists)/(saved_gesture.size()+current_gesture.size());
        return avg_dist<180;
    }

    private static double distance(Point a, Point b){
        return Math.sqrt(Math.pow(a.get_x()-b.get_x(),2)+Math.pow(a.get_y()-b.get_y(),2));
    }
}
