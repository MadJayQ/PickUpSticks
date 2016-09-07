/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pickupsticks;

import java.awt.Point;

/**
 *
 * @author jakei_000
 */
public class Vector2 {
    public int x;
    public int y;
    public Vector2(int _x, int _y) {
        x = _x;
        y = _y;
    }
    public double getDistance(Vector2 dst) {
        return Math.sqrt(Math.pow((x - dst.x), 2)+ Math.pow((y - dst.y), 2));
    }
    
    public Vector2 scaleVector(int scalar) {
        return new Vector2(this.x * scalar, this.y * scalar);
    }
    
    public Point toPoint() {
        return new Point(x, y);
    }
    
}
