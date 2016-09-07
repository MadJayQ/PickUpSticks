/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pickupsticks;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.DataBufferInt;

/**
 *
 * @author jakei_000
 */
public class Button extends ScreenObject {
    
    public static enum ButtonAction {
        BUTTON_NONE,
        BUTTON_PASS,
        BUTTON_NEW_GAME
    }
    
    private final int width;
    private final int height;
    private String text = "";
    private Vector2 textPos;
    private int pixelData[];
    
    private int borderColor = 0x000000;
    private int backgroundColor = 0xFFFFFF;
    private int backgroundColorSaved = 0x0;
    private int backgroundColorPressed = Color.yellow.getRGB();
    public ButtonAction buttonAction = ButtonAction.BUTTON_NONE;
    
    public boolean pressed = false;
    
    private int buttonAnimFrameCounter = 0;

    public Button(int x, int y) {
        super(x, y);
        width = 1;
        height = 1;
    }
    
    public Button(int x, int y, int w, int h) {
        super(x, y);
        width = w;
        height = h;
        
        this.clickAction = ScreenObjectAction.ACTION_CANVAS_BUTTON;
        textPos = new Vector2(x, y);
        
        pixelData = new int[w * h];
        for(int xx = 0; xx < w; xx++) {
            for(int yy = 0; yy < h; yy++) {
                if(xx == 0 || xx == (w - 1) || yy == 0 || yy == (h - 1)) {
                    pixelData[xx + (yy * w)] = borderColor;
                }
                else {
                    pixelData[xx + (yy * w)] = backgroundColor;
                }
            }
        }
    }
    private void updateButtonPixels() {
        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                if(x == 0 || x == (width - 1) || y == 0 || y == (height - 1)) {
                    pixelData[x + (y * width)] = borderColor;
                } else {
                    pixelData[x + (y * width)] = backgroundColor;
                }
            }
        }
    }
    
    public void setButtonAction(ButtonAction a) {
        this.buttonAction = a;
    }
    
    public int getButtonAction() {
        return (int)this.buttonAction.ordinal();
    }
    
    public void setBorderColor(int col) {
        if(borderColor != col) {
            borderColor = col;
            updateButtonPixels();
        }
    }
    
    public void setBackgroundColor(int col) {
        if(backgroundColor != col) {
            backgroundColorSaved = backgroundColor;
            backgroundColor = col;
            updateButtonPixels();
        }
    }
    
    public void setPressedColor(int col) {
        backgroundColorPressed = col;
    }
    
    public void attachText(String text, int xo, int yo) {
        this.text = text;
        this.textPos.x = vecPosition.x + xo;
        this.textPos.y = vecPosition.y + yo;
    }
    
    public boolean hasText() {
        return !"".equals(this.text);
    }
    
    public String getText() {
        return text;
    }
    
    public Vector2 getTextPosition() {
        return textPos;
    }

    @Override
    public void Draw(Bitmap backBuffer) {
        DataBufferInt dataBuffer = (DataBufferInt)backBuffer.GetBufferedImage().getRaster().getDataBuffer();
        int data[] = dataBuffer.getData();
        int bufferStartIndex = (this.vecPosition.x + (this.vecPosition.y * backBuffer.width)); //Find the starting place in our pixel buffer
        int bufferMaxStride = bufferStartIndex + width; //Find the maximum stide in which our program is going to attempt to place pixels
        //Clamp max stride to the stride of our backbuffer to prevent objects from wrapping
        if(bufferMaxStride > backBuffer.width + (this.vecPosition.y * backBuffer.width)) { 
            bufferMaxStride = backBuffer.width + (this.vecPosition.y * backBuffer.width);
        }
        for(int xindex = bufferStartIndex; xindex < bufferMaxStride; xindex++) {
            for(int yOffset = 0; yOffset < height; yOffset++) {
                int index = xindex + (yOffset * backBuffer.width);
                if(index <= data.length) { //Prevent the renderer from emplacing pixels in locations that are out of bounds
                    data[index] = pixelData[(xindex - bufferStartIndex) + (yOffset * width)];
                }
            }
        }
    }

    @Override
    public void Update() {
        this.selectionBoundingBox = new Rectangle(vecPosition.x * 5, vecPosition.y * 5, width * 5, height * 5);
        if(pressed) {
            if(backgroundColor != backgroundColorPressed) {
                setBackgroundColor(Color.yellow.getRGB());
                buttonAnimFrameCounter = 0;
            }
            else {
                if(buttonAnimFrameCounter >= 100) {
                    pressed = false;
                } else {
                    buttonAnimFrameCounter++;
                }
            }
        } else {
            if(backgroundColor == backgroundColorPressed) {
                setBackgroundColor(backgroundColorSaved);
                buttonAnimFrameCounter = 0;
            }
        }
    }

    @Override
    public void MouseClick() {
        pressed = true;
        //this.setBackgroundColor(Color.yellow.getRGB());
    }
    
}
