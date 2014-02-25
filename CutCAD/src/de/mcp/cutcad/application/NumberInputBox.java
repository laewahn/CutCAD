package de.mcp.cutcad.application;

import processing.core.PApplet;
import processing.event.KeyEvent;
import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ControlP5;
import controlP5.Numberbox;

public class NumberInputBox extends Numberbox
{ 
	private final NumberInputBoxKeyEventHelper nin;
	
    public NumberInputBox(PApplet p, ControlP5 cp5, String theName)
    {
        super(cp5, theName);   
        nin = new NumberInputBoxKeyEventHelper(p, this);
        this.addCallback(new CallbackListener() {
            public void controlEvent(CallbackEvent theEvent) {
                if (theEvent.getAction()==ControlP5.ACTION_RELEASED) { 
                    nin.setActive(true);
                } 
                else if (theEvent.getAction()==ControlP5.ACTION_LEAVE) { 
                    nin.setActive(false); 
                }
            }
        });
    }
    
    public void keyEvent(KeyEvent k)
    {
    	nin.keyEvent(k);
    }

}
