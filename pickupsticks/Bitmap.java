/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pickupsticks;


import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 *
 * @author jakei_000
 */

public class Bitmap {
    
    public int width;
    public int height;
    public int scale = 1;
    
    private final BufferedImage bufferedImage;
    
    public Bitmap(int w, int h) {
        bufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        width = w;
        height = h;
        
        clearBitmap(0xFFFFFF);
        
    }
    
    public Bitmap(int w, int h, int scalar) {
        bufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        width = w;
        height = h;
        scale = scalar;
        
        clearBitmap(0xFFFFFF);
    }
    
    public int getWidth() {
        return width * scale;
    }
    
    public int getHeight() {
        return height * scale;
    }
    
    public BufferedImage GetBufferedImage() {
        return bufferedImage;
    }
    public final void clearBitmap(int color)
    {
        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                setPixel(x, y, color);
            }
        }
    }
    public int[] getPixels() {
        DataBufferInt buffer = (DataBufferInt)bufferedImage.getRaster().getDataBuffer();
        return buffer.getData();
    }
    public void setPixels(int pixels[]) {
        DataBufferInt buffer = (DataBufferInt)bufferedImage.getRaster().getDataBuffer();
        int data[] = buffer.getData();
        data = pixels;
    }
    public void setPixel(int x, int y, int color) {
        int index = x + (y * width);
        setPixel(index, color);
    }
    public void setPixel(int index, int color) {
        DataBufferInt buffer = (DataBufferInt)bufferedImage.getRaster().getDataBuffer();
        int temp[] = buffer.getData();
        temp[index] = color;
    }
}
