package com.drawpad.util;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;

public class SketchPadPen implements ISketchPadTool {
	private static final float TOUCH_TOLERANCE = 4.0f;

	private float m_curX = 0.0f;
	private float m_curY = 0.0f;
	private boolean m_hasDrawn = false;
	private Path m_penPath = new Path();
	private Paint m_penPaint = new Paint();
	private float x1 = 0f;
	private float y1 = 0f;
	private float x2 = 0f;
	private float y2 = 0f;
	private int penSize;
	private Float rotation = null;
	private Float deflation = null;
	private Matrix matrix = new Matrix();
	private float Px;
	private float Py;
	private long startTime;
	private long endTime;
	private Float dx = null;
	private Float dy = null;

	public SketchPadPen(int penSize, int penColor) {
		m_penPaint.setAntiAlias(true);
		m_penPaint.setDither(true);
		m_penPaint.setColor(penColor);
		m_penPaint.setStrokeWidth(penSize);
		m_penPaint.setStyle(Paint.Style.STROKE);
		m_penPaint.setStrokeJoin(Paint.Join.ROUND);
		m_penPaint.setStrokeCap(Paint.Cap.ROUND);
		this.penSize = penSize;
		dx = null;
		dy = null;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;

	};

	public void draw(Canvas canvas) {
		if (null != canvas) {
			canvas.save();
			if (rotation != null || deflation != null || dx != null) {
				matrix.reset();
				if (rotation != null) {
					matrix.postRotate(rotation, Px, Py);
				}
				if (deflation != null) {
					matrix.postScale(deflation, deflation, Px, Py);

				}
				if (dx != null) {
					matrix.postTranslate(dx, dy);
				}
				canvas.setMatrix(matrix);
			}
			canvas.drawPath(m_penPath, m_penPaint);
			canvas.restore();
		}
	}

	public boolean hasDraw() {
		return m_hasDrawn;
	}

	public void touchDown(float x, float y) {
		startTime = System.currentTimeMillis();
		m_penPath.reset();
		m_penPath.moveTo(x, y);
		m_curX = x;
		m_curY = y;
		x1 = x;
		x2 = x;
		y1 = y;
		y2 = y;
	}

	private void getxy(float x, float y) {
		if (x1 > x) {
			x1 = x;
		}
		if (x2 < x) {
			x2 = x;
		}
		if (y1 > y) {
			y1 = y;
		}
		if (y2 < y) {
			y2 = y;
		}

	}

	public void touchMove(float x, float y) {
		float dx = Math.abs(x - m_curX);
		float dy = Math.abs(y - m_curY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			m_penPath.quadTo(m_curX, m_curY, (x + m_curX) / 2, (y + m_curY) / 2);
			m_hasDrawn = true;
			m_curX = x;
			m_curY = y;
			getxy(x, y);
		}
	}

	public void touchUp(float x, float y) {
		endTime = System.currentTimeMillis();
		m_penPath.lineTo(x, y);
		x1 -= penSize / 2f;
		y1 -= penSize / 2f;
		x2 += penSize / 2f;
		y2 += penSize / 2f;

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
	public void setPy(float Py) {
		this.Py = Py;

	}

	@Override
	public float getRotation() {
		return rotation;
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
