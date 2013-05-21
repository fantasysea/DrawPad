package com.drawpad.util;

import android.graphics.Canvas;

public interface ISketchPadTool {
	public void draw(Canvas canvas);

	public boolean hasDraw();

	public void setHasDraw(boolean x);
	
	public void touchDown(float x, float y);

	public void touchMove(float x, float y);

	public void touchUp(float x, float y);
	
	public float getX1() ;
	
	public float getY1() ;
	
	public float getX2() ;
	
	public float getY2() ;
	
	
	public void setRotation(float rotation);
	
	public void setDeflation(float deflation);
	
	public float getRotation();
	
	public void setPx(float Px);
	
	public void setPy(float Py);
	
	public long getStartTime();
	
	public long getEndTime();
	
	public void setDx(Float dx);

	public void setDy(Float dy);
}
