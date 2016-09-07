/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pickupsticks;

import java.awt.Rectangle;
import java.awt.image.DataBufferInt;
import java.util.Random;

/**
 *
 * @author jakei_000
 */
public class Stick extends ScreenObject{
    
    public final int STRIDE = 4;
    
    //In hindsight, this was not the best way to store this information...but it'll do for now
    public int pixelData[] = {0x000000, 0x000000, 0x000000, 0x000000,
                              0x000000, 0xFFFFFF, 0xFFFFFF, 0x000000,
                              0x000000, 0xFFFFFF, 0xFFFFFF, 0x000000,
                              0x000000, 0xFFFFFF, 0xFFFFFF, 0x000000,
                              0x000000, 0xFFFFFF, 0xFFFFFF, 0x000000,
                              0x000000, 0xFFFFFF, 0xFFFFFF, 0x000000,
                              0x000000, 0xFFFFFF, 0xFFFFFF, 0x000000,
                              0x000000, 0xFFFFFF, 0xFFFFFF, 0x000000,
                              0x000000, 0xFFFFFF, 0xFFFFFF, 0x000000,
                              0x000000, 0xFFFFFF, 0xFFFFFF, 0x000000,
                              0x000000, 0xFFFFFF, 0xFFFFFF, 0x000000,
                              0x000000, 0xFFFFFF, 0xFFFFFF, 0x000000,
                              0x000000, 0xFFFFFF, 0xFFFFFF, 0x000000,
                              0x000000, 0xFFFFFF, 0xFFFFFF, 0x000000,
                              0x000000, 0xFFFFFF, 0xFFFFFF, 0x000000,
                              0x000000, 0xFFFFFF, 0xFFFFFF, 0x000000,
                              0x000000, 0xFFFFFF, 0xFFFFFF, 0x000000,
                              0x000000, 0xFFFFFF, 0xFFFFFF, 0x000000,
                              0x000000, 0xFFFFFF, 0xFFFFFF, 0x000000,
                              0x000000, 0xFFFFFF, 0xFFFFFF, 0x000000,
                              0x000000, 0xFFFFFF, 0xFFFFFF, 0x000000,
                              0x000000, 0xFFFFFF, 0xFFFFFF, 0x000000,
                              0x000000, 0xFFFFFF, 0xFFFFFF, 0x000000,
                              0x000000, 0xFFFFFF, 0xFFFFFF, 0x000000,
                              0x000000, 0xFFFFFF, 0xFFFFFF, 0x000000,
                              0x000000, 0xFFFFFF, 0xFFFFFF, 0x000000,
                              0x000000, 0xFFFFFF, 0xFFFFFF, 0x000000,
                              0x000000, 0xFFFFFF, 0xFFFFFF, 0x000000,
                              0x000000, 0xFFFFFF, 0xFFFFFF, 0x000000,
                              0x000000, 0xFFFFFF, 0xFFFFFF, 0x000000,
                              0x000000, 0xFFFFFF, 0xFFFFFF, 0x000000,
                              0x000000, 0xFFFFFF, 0xFFFFFF, 0x000000,
                              0x000000, 0xFFFFFF, 0xFFFFFF, 0x000000,
                              0x000000, 0xFFFFFF, 0xFFFFFF, 0x000000,
                              0x000000, 0xFFFFFF, 0xFFFFFF, 0x000000,
                              0x000000, 0xFFFFFF, 0xFFFFFF, 0x000000,
                              0x000000, 0x000000, 0x000000, 0x000000};
    
    private boolean bSelected = false;
    
    public Rectangle GetBoundingBox() {
        int x0 = this.vecPosition.x * 5;
        int y0 = this.vecPosition.y * 5;
        int x1 = STRIDE * 5;
        int y1 = getHeight() * 5;
        
        //Bad programming practice, should not have to hardcode scale into here, should be able to grab it
        return new Rectangle(x0, y0, x1, y1); //Shift coordinates of bounding box from scale 1 to scale 5
    }
    
    public Stick(int color, int x, int y) {
        super(x, y);
        this.clickAction = ScreenObjectAction.ACTION_CANVAS_BUTTON;
        setColor(color, 0x0000FF00);
    }
    
    public Stick(int color, int bandColor, int x, int y) {
        super(x, y); 
        this.clickAction = ScreenObjectAction.ACTION_PICK_UP_STICK;
        setColor(color, bandColor);
    }
    
    public Stick(int x, int y) {
        super(x, y);
        this.clickAction = ScreenObjectAction.ACTION_PICK_UP_STICK;
        Random rn = new Random();
        byte[] rgb = {0, 0, 0, 0, 0, 0}; //First 3 bytes are stick color, next three are band color
        rn.nextBytes(rgb);
        //Left shift each byte over into their correct position 
        //Memory map -- 0x00000000
        //              0xXXRRGGBB -> X = unknown, should probably zero it? --> EDIT: Based on backbuffer image format it should be alpha? But it seems to have no effect whatsoever...
        //              0xFF000000 -> Black, proof first byte has no effect
        //              0x00FF0000 -> 255 RED
        //              0x0000FF00 -> 255 GREEN
        //              0x000000FF -> 255 BLUE
        //Note to self: Zero first byte for safety, also AND every byte against 0xFF for safety
        
        int stickColor = ((rgb[0] & 0xFF) << 16) + ((rgb[1] & 0xFF) << 8) + ((rgb[2] & 0xFF));
        int bandColor  = ((rgb[3] & 0xFF) << 16) + ((rgb[4] & 0xFF) << 8) + ((rgb[5] & 0xFF));

        setColor(stickColor, bandColor);
    }
    
    public final void setColor(int color) {
        for(int i = 0; i < pixelData.length; i++) {
            if(pixelData[i] == 0xFFFFFF) {
                pixelData[i] = color;
            }
        }
    }
    
    public final void setColor(int color, int bandColor) {
        for(int i = 0; i < pixelData.length; i++) {
            if(pixelData[i] == 0xFFFFFF) {
                int localY = (int)(i / STRIDE); //The current row is the integer division of the index over the stride
                boolean b = (localY == (getHeight() / 2) - 1);
                pixelData[i] = (b) ? bandColor : color;
            }
        }
    }
    
    public int getHeight() {
        return (pixelData.length / STRIDE);
    }

    @Override
    public void Draw(Bitmap backBuffer) {
        //The place in the pixel buffer where I want to merge my pixels in is calculated by taking the x coordinate and adding it to the
        //y coordinate times the stride/pitch of the backbuffer
        DataBufferInt dataBuffer = (DataBufferInt)backBuffer.GetBufferedImage().getRaster().getDataBuffer();
        int data[] = dataBuffer.getData();
        int bufferStartIndex = (this.vecPosition.x + (this.vecPosition.y * backBuffer.width)); //Find the starting place in our pixel buffer
        int bufferMaxStride = bufferStartIndex + STRIDE; //Find the maximum stide in which our program is going to attempt to place pixels
        //Clamp max stride to the stride of our backbuffer to prevent objects from wrapping
        if(bufferMaxStride > backBuffer.width + (this.vecPosition.y * backBuffer.width)) { 
            bufferMaxStride = backBuffer.width + (this.vecPosition.y * backBuffer.width);
        }
        for(int xindex = bufferStartIndex; xindex < bufferMaxStride; xindex++) {
            for(int yOffset = 0; yOffset < getHeight(); yOffset++) {
                int index = xindex + (yOffset * backBuffer.width);
                if(index <= data.length) { //Prevent the renderer from emplacing pixels in locations that are out of bounds
                    data[index] = pixelData[(xindex - bufferStartIndex) + (yOffset * STRIDE)];
                }
            }
        }
    }
    
    public boolean isSelected() {
        return bSelected;
    }

    @Override
    public void Update() {
        selectionBoundingBox = GetBoundingBox();
    }

    @Override
    public void MouseClick() {
        bSelected = true;
    }   
}