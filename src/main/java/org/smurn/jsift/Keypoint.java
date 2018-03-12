package org.smurn.jsift;

public class Keypoint {
	ScaleSpacePoint point;
	double magnitude;
	int direction;
	
	public Keypoint(ScaleSpacePoint point, double magnitude, int direction) {
		this.point = point;
		this.magnitude = magnitude;
		this.direction = direction;
	}

	public ScaleSpacePoint getPoint() {
		return point;
	}

	public void setPoint(ScaleSpacePoint point) {
		this.point = point;
	}

	public double getMagnitude() {
		return magnitude;
	}

	public void setMagnitude(double magnitude) {
		this.magnitude = magnitude;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}
}
