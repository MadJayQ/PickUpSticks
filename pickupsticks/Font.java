/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pickupsticks;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author jakei_000
 * 
 */
public class Font  {
    
    private final BufferedImage[][] font;
   
    public final String characters =  "ABCDEFGHIJKLMNOPQRSTUVWXYZ    0123456789-.!?/%$\\=*+,;:()&#\"'abcdefghijklmnopqrstuvwxyz    ";
    
    public Font() {
        font = createFontFromPNG("res/font.png", 16, 16);
    }
    
    public BufferedImage getCharacter(int x, int y) {
        return font[x][y];
    }
    
    public void RenderText(String text, int x, int y, Graphics g) {
        int strLength = text.length();
        Vector2 pos = new Vector2(x, y);
        for(int i = 0; i < strLength; i++) {
            int c = characters.indexOf(text.charAt(i));
            if (c >= 0) {
                pos.x = (i * 13);
                g.drawImage(getCharacter((c % 30), (c / 30)), pos.x, pos.y, null);
            }
        }
    }
    
    public void RenderText(String text, int x, int y, int col, Graphics g) {
        RenderText(text, x, y, col, 0x0, g);
    }
    
    public void RenderText(String text, int x, int y, int col, int borderColor, Graphics g) {
        int strLength = text.length();
        Vector2 pos = new Vector2(x, y);
        for(int i = 0; i < strLength; i++) {
            int c = characters.indexOf(text.charAt(i));
            if(c >= 0) {
                pos.x =  x + (i * 13);
                //The defaulted buffered image type is a 4 byte array containing each of the images pixels
                //To me it would make more sense if it stored it as an array of arrays with each subarray containing the byte information of each pixel
                //But that's not how it is stored, this makes the loop logic really cumbersome so I convert it to store each pixel in a 32-bit integer
                BufferedImage tmp = getCharacter((c % 30), (c / 30));
                BufferedImage tmpConverted = new BufferedImage(tmp.getWidth(), tmp.getHeight(), BufferedImage.TYPE_INT_ARGB);
                tmpConverted.getGraphics().drawImage(tmp, 0, 0, null);
                DataBufferInt dataBuffer = (DataBufferInt)tmpConverted.getRaster().getDataBuffer();
                int data[] = dataBuffer.getData();
                for(int p = 0; p < data.length; p ++) {
                    int color = data[p];
                    int alpha = (0xFF & (color >> 24));
                    int red = (0xFF & (color >> 16));
                    int green = (0xFF & (color >> 8));
                    int blue = (0xFF & (color >> 0));
                    
                    if(alpha > 0 && red == 0 && green == 0 && blue == 0) {
                        alpha = 255;
                        red = (0xFF & (borderColor >> 16));
                        green = (0xFF & (borderColor >> 8));
                        blue = (0xFF & (borderColor >> 0));
                    }
                    
                    if(alpha > 0 && red > 0 && green > 0 && blue > 0) {
                        alpha = 255;
                        red = (0xFF & (col >> 16));
                        green = (0xFF & (col >> 8));
                        blue = (0xFF & (col >> 0));
                    }
                    
                    int newColor = (alpha << 24) | (red << 16) | (green << 8) | (blue << 0);
                    data[p] = newColor;
                }
                g.drawImage(tmpConverted, pos.x, pos.y, null);
            }
        }
    }
    
    private BufferedImage[][] createFontFromPNG(String fileName, int w, int h) {
        try {
            BufferedImage i = ImageIO.read(getClass().getClassLoader().getResourceAsStream(fileName));
            Vector2 tileSize = new Vector2(i.getWidth() / w, i.getHeight() / h);
            
            BufferedImage[][] ret = new BufferedImage[tileSize.x][tileSize.y];
            for(int x = 0; x < tileSize.x; x++) {
                for(int y = 0; y < tileSize.y; y++) {
                    ret[x][y] = i.getSubimage(x * w, y * h, w, h);
                }
            }
            return ret;
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return (BufferedImage[][])null;  //RIP
    }
    
}
