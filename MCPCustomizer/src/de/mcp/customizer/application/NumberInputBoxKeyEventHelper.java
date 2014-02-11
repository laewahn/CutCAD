package de.mcp.customizer.application;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.event.KeyEvent;
import controlP5.Numberbox;

public class NumberInputBoxKeyEventHelper {

    private String text = "";
    private Numberbox n;    
    private PApplet p;
    private boolean active;

    public NumberInputBoxKeyEventHelper(PApplet p, Numberbox theNumberbox) {
    	this.p = p;
        n = theNumberbox;
        p.registerMethod("keyEvent", this );
        text = n.getValueLabel().getText();
    }

    public void keyEvent(KeyEvent k) {
        if (k.getAction() == KeyEvent.PRESS && active) {
            if (k.getKey() == '\n') {
                submit();
                return;
            } 
            else if (k.getKeyCode() == PConstants.BACKSPACE) { 
                text = text.isEmpty() ? "":text.substring(0, text.length()-1);
            }
            else if (k.getKey() < 255) {
                final String regex = "\\d+([.]\\d{0,2})?";
                if (java.util.regex.Pattern.matches(regex, text + k.getKey())) {
                    text += k.getKey();
                }
            }
            n.getValueLabel().setText(this.text);
        }
    }

    public void setActive(boolean b) {
        active = b;
        if (active) {
            n.setColorValueLabel(n.getColor().getActive());
            text = n.getValueLabel().getText();
        }
        else {
            n.setColorValueLabel(p.color(255));
            n.getValueLabel().setText(String.format("%.0f", n.getValue()));
        }
    }
    
    private void submit() {
        if (!text.isEmpty()) {
            n.setValue(Float.parseFloat(text));
        } 
        else {
            n.getValueLabel().setText(""+n.getValue());
        }
        this.setActive(false);
    }
}