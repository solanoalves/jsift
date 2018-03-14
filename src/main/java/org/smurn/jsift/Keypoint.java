package org.smurn.jsift;

public class Keypoint {
	ScaleSpacePoint point;
	double magnitude;
	int direction;
	double dominant;
	double[] descriptor;
	
	public Keypoint(ScaleSpacePoint point, double magnitude, int direction, double dominant, double[] descriptor) {
		this.point = point;
		this.magnitude = magnitude;
		this.direction = direction;
		this.dominant = dominant;
		this.descriptor = descriptor;
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

	public double getDominant() {
		return dominant;
	}

	public void setDominant(double dominant) {
		this.dominant = dominant;
	}

	public double[] getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(double[] descriptor) {
		this.descriptor = descriptor;
	}
}
