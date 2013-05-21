package com.drawpad.util;

import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.drawpad.view.SketchPadView;

public class SketchPadDeco implements ISketchPadTool {

	private boolean m_hasDrawn = false;
	private Bitmap bitmap;
	private Paint m_penPaint = new Paint();
	private float x1 = 0f;
	private float y1 = 0f;
	private float x2 = 0f;
	private float y2 = 0f;
	private int w;
	private int h;
	public static boolean ONEORMORE = false;
	private Float rotation = null;
	private Float deflation = null;
	private Float Px;
	private Float Py;
	private Matrix matrix;
	private long startTime;
	private long endTime;
//	private boolean type = false;
	private Float dx= null;
	private Float dy= null;
	private String picPath;
	
	
	public SketchPadDeco(String path) {
		m_penPaint.setAntiAlias(true);
		m_penPaint.setDither(true);
		m_penPaint.setStyle(Paint.Style.STROKE);
		m_penPaint.setStrokeJoin(Paint.Join.ROUND);
		m_penPaint.setStrokeCap(Paint.Cap.ROUND);
		this.picPath = path;
		decoBitmap();
//		this.type = type;
		dx= null;
		dy= null;
	}
	private void decoBitmap(){
		Matrix matrix = new Matrix();
		matrix.reset();
		matrix.postScale(0.5f, 0.5f);
		InputStream stream = null;
		try {
			stream = SketchPadView.am.open(picPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		BitmapFactory.Options  options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_4444;
		
		bitmap = BitmapFactory.decodeStream(stream, null, options);
		
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,bitmap.getWidth(), bitmap.getHeight(), matrix, true); 
		bitmap.recycle();
		bitmap = null;
		bitmap = resizedBitmap;
		w = bitmap.getWidth() / 2;
		h = bitmap.getHeight() / 2;
		try {
			stream.close();
			stream = null;
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void setRotation(float rotation) {
		this.rotation = rotation;

	};

	public void draw(Canvas canvas) {
		if(bitmap==null){
			decoBitmap();
		}
		if (null != canvas && bitmap != null) {
			canvas.save();
			if (rotation != null || deflation != null||dx!=null) {
				matrix = new Matrix();
				matrix.reset();
				if (rotation != null) {
					matrix.postRotate(rotation, Px, Py);
				}
				if (deflation != null) {
					matrix.postScale(deflation, deflation, Px, Py);
				}
				if(dx!=null){
					matrix.postTranslate(dx, dy);
				}
				canvas.setMatrix(matrix);
			}
//			if(x1!=null){
			canvas.drawBitmap(bitmap, x1,y1, m_penPaint);
//			}
			bitmap.recycle();
			bitmap = null;

			canvas.restore();
		}
	}

	public boolean hasDraw() {
		return m_hasDrawn;
	}

	public void touchDown(float x, float y) {
//		if(type){
			x1 = x - w;
			y1 = y - h;
			x2 = x + w;
			y2 = y + h;
			startTime = System.currentTimeMillis();
			endTime = startTime;
			m_hasDrawn = true;
//		}
	}

	public void touchMove(float x, float y) {
	}

	public void touchUp(float x, float y) {
	}

	public float getX1() {
		return x1;
	}

	public float getY1() {
		return y1;
	}

	public float getX2() {
		return x2;
	}

	public float getY2() {
		return y2;
	}

	@Override
	public void setPx(float Px) {
		
		this.Px = Px;

	}

	@Override
	public float getRotation() {
		return rotation;
	}

	@Override
	public void setPy(float Py) {
		this.Py = Py;

	}

	@Override
	public void setDeflation(float deflation) {
		this.deflation = deflation;

	}

	@Override
	public long getStartTime() {
		return startTime;
	}

	@Override
	public long getEndTime() {
		return endTime;

	}

	@Override
	public void setHasDraw(boolean x) {
		m_hasDrawn = x;
	}
	
	public void setDx(Float dx) {
		this.dx = dx;
	}

	public void setDy(Float dy) {
		this.dy = dy;
	}
}
