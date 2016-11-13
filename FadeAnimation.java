/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package matchinggame;

import java.awt.Point;

/**
 *
 * @author tanza
 */
public class FadeAnimation {
    private Point coord;
    private double alpha;
    
    public FadeAnimation(int x, int y) {
        alpha = 1.0;
        coord = new Point(x,y);
    }
    
    public double getAlpha() {
        return alpha;
    }
    public Point getCoord() {
        return coord;
    }
    public void setAlpha(double a) {
        alpha = a;
    }
}
