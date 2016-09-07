/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pickupsticks;

import java.awt.Rectangle;

/**
 *
 * @author jakei_000
 */
public abstract class ScreenObject {
    
    public static enum ScreenObjectAction {
        ACTION_NONE,
        ACTION_PICK_UP_STICK,
        ACTION_CANVAS_BUTTON;
    }
    
    public ScreenObject(int x, int y) {
        this.vecPosition = new Vector2(x, y);
    }
    public abstract void Draw(Bitmap bm);
    public abstract void Update(); 
    public abstract void MouseClick();
    
    public final boolean isMouseInSelectionBox(Vector2 p) {
        return selectionBoundingBox.contains(p.toPoint());
    }
    public ScreenObjectAction clickAction;
    public Vector2 vecPosition;
    protected Rectangle selectionBoundingBox;
}
