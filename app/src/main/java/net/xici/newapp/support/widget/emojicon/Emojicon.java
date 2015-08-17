package net.xici.newapp.support.widget.emojicon;

import java.io.Serializable;

public class Emojicon implements Serializable {
    private static final long serialVersionUID = 1L;
    public int icon;
    public char value;
    public String emoji;

    private Emojicon() {
    }
    
    public Emojicon(int res,String emoji){
    	this.icon = res;
    	this.emoji = emoji;
    }

}
