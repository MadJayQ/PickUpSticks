/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pickupsticks;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import pickupsticks.ScreenObject.ScreenObjectAction;

/**
 *
 * @author jakei_000
 */
public class Viewport extends Bitmap {
    
    private final CopyOnWriteArrayList<ScreenObject> screenObjects;
    private final CopyOnWriteArrayList<ScreenObject> deadObjects;
    private int SCALE = 1;
    private int WIDTH = 0;
    private int HEIGHT = 0;
    private Font font;
    private final PickUpSticks gameComponent;
    private Graphics graphics;
    
    
    private ScreenObject lastSelectedObject;
    
    public Viewport(int w, int h, int scale, PickUpSticks game) { //Pass our screen the Graphics object so it can manage its own draw calls, and allow ScreenObjects to manage their own draw calls
        super(w, h, scale); //Call Bitmaps constructor 
        WIDTH = w;
        HEIGHT = h;
        SCALE = scale;
        gameComponent = game;
        screenObjects = new CopyOnWriteArrayList<>(); //I hope this functions similarly to std::vector<type> from C++
        deadObjects = new CopyOnWriteArrayList<>();
        font = new Font();
    }
    
    public void Update()
    {
        screenObjects.stream().forEach((object) -> { //Lambdas in Java, cool
            object.Update();
            if(object instanceof Stick) {
                if(((Stick) object).isSelected()){
                    UnRegisterScreenObject(object);
                }
            }
        
        });
        
        deadObjects.stream().forEach((object) -> {
            screenObjects.remove(object);
        });
        
        deadObjects.clear();
    }
    
    public ScreenObject getLastSelectedObject() {
        return lastSelectedObject;
    }
    
    public int executeMouseClick(Vector2 mousePosition) { 
        
        for(ScreenObject obj : screenObjects) {
            if(obj.isMouseInSelectionBox(mousePosition)) {
                obj.MouseClick();
                lastSelectedObject = obj;
                return (int)obj.clickAction.ordinal();
            }
        }
        return ScreenObject.ScreenObjectAction.ACTION_NONE.ordinal();
    }
    
    public void ClearViewport(int color) {
        this.clearBitmap(color);
    }
    
    public int getNumberOfType(Class c) {
        int ret = 0;
        for(ScreenObject obj : screenObjects) {
            if(c.isInstance(obj)) {
                ret++;
            }
        }
        return ret;
    }
    
    public ArrayList<ScreenObject> getAllObjectsOfType(ScreenObject.ScreenObjectAction action) {
        ArrayList<ScreenObject> list = new ArrayList<>();
        screenObjects.stream().forEach((object) -> {
            if(object.clickAction == action) {
                list.add(object);
            }
        });
        
        return list;
    }
    
    public void clearAllObjects() {
        screenObjects.clear();
    }
    
    public void BeginDraw(Graphics g) {
        graphics = g;
    }
    
    public void clearAllOfType(ScreenObjectAction a) {
        for(ScreenObject s : screenObjects) {
            if(s.clickAction == a) {
                this.UnRegisterScreenObject(s);
            }
        }
    }
    
    public void EndDraw() {
        graphics.dispose();
    }
    public void Draw() {
        screenObjects.stream().forEach((object) -> {
            object.Draw(this);
        });
        
        if(graphics == null) {
            throw new RuntimeException("FAILED TO DRAW!");
        }
               
        graphics.drawImage(this.GetBufferedImage(), 0, 0, WIDTH * SCALE, HEIGHT * SCALE, gameComponent);
        screenObjects.stream().forEach((object) -> { //Draw debug bounding boxes for mouse collision detection
            if(object instanceof Button && object != null) {
                Button b = (Button)(object);
                if(b.hasText()) {
                    Vector2 textPosition = b.getTextPosition();
                    font.RenderText(b.getText(), textPosition.x * SCALE, textPosition.y * SCALE, 0xFF0000, graphics);
                }
            }
        });
        //font.RenderText("PASS", 350, 700, Color.gray.getRGB(), graphics); //Since I can't get the alpha channel working in the back buffer, i'll have to settle with debug text
        
        //graphics.drawImage(font.getCharacter(0, 2), 0, 0, gameComponent);
    }
    
    public void RenderText(String text, int x, int y, int col) {
        if(font == null) {
            font = new Font();
        }
        font.RenderText(text, x, y, graphics);
    }
    
    public Dimension getScreenDimensions() {
        return new Dimension(WIDTH, HEIGHT);
    }
    
    public synchronized void RegisterScreenObject(ScreenObject obj) {
        screenObjects.add(obj);
    }
    
    public synchronized void UnRegisterScreenObject(ScreenObject obj) {
        deadObjects.add(obj);
    }
    
    
    
    
}
    