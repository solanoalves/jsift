/*
 * Copyright 2011 Stefan C. Mueller.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smurn.jsift;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Strategy that detects local extrema in a scale-space. The strategy searches
 * all pixels in the difference-of-gaussian images and returns the positions of
 * those who's value is either smaller or larger than all 26 neighboring pixels.
 */
public class ExtremaDetector implements KeypointDetector {

	public static final LUSolver3 solver = new LUSolver3();
	
	@Override
	public Collection<ScaleSpacePoint> detectKeypoints(final ScaleSpace scaleSpace) {

		if (scaleSpace == null) {
			throw new NullPointerException("scale space must not be null");
		}

		List<ScaleSpacePoint> points = new ArrayList<ScaleSpacePoint>();
		for (Octave octave : scaleSpace.getOctaves()) {

			int dogCount = octave.getDifferenceOfGaussians().size();
			for (int i = 1; i < dogCount - 1; i++) {
				Collection<ScaleSpacePoint> pointsOnThisScale = detectKeypoints(
						octave.getDifferenceOfGaussians().get(i - 1), 
						octave.getDifferenceOfGaussians().get(i),
						octave.getDifferenceOfGaussians().get(i + 1),
						(i+2 < dogCount-1 ? octave.getDifferenceOfGaussians().get(i + 2) : null),
						(i-2 > 0 ? octave.getDifferenceOfGaussians().get(i - 2) : null));
				points.addAll(pointsOnThisScale);
			}
		}

		return points;
	}

	/**
	 * Detects extrema on one scale.
	 * 
	 * @param low
	 *            The DoG at one scale lower.
	 * @param center
	 *            The DoG on which to detect the extrema.
	 * @param high
	 *            The DoG at one scale higher.
	 * @return The extremas.
	 */
	private Collection<ScaleSpacePoint> detectKeypoints(Image low, Image center, Image high, Image highhigh, Image lowlow) {
		int x,y;
		double partialX, partialY, partialS, offsetX, offsetY, module;
		double[][] hessian = new double[3][3],
				invHessian;
		boolean highContrast;
		
		List<ScaleSpacePoint> points = new LinkedList<ScaleSpacePoint>();
		System.out.println("Total pixels "+center.getHeight()*center.getWidth());
		for (int row = 1; row < center.getHeight() - 1; row++) {
			for (int col = 1; col < center.getWidth() - 1; col++) {
				highContrast = false;
				float value = center.getPixel(row, col);

				// Since all neighbors all need to be on the same 'side' for an
				// extremum we can just take an arbitrary neighbor to determine
				// if we might face a minimum or a maximum.
				float sign = Math.signum(value - center.getPixel(row, col - 1));
				if (sign == 0.0f) {
					break;
				}
				value *= sign;				
				boolean isExtremum = true;
				isExtremum &= low.getPixel(row - 1, col - 1) * sign < value;
				isExtremum &= low.getPixel(row - 1, col) * sign < value;
				isExtremum &= low.getPixel(row - 1, col + 1) * sign < value;
				isExtremum &= low.getPixel(row, col - 1) * sign < value;
				isExtremum &= low.getPixel(row, col) * sign < value;
				isExtremum &= low.getPixel(row, col + 1) * sign < value;
				isExtremum &= low.getPixel(row + 1, col - 1) * sign < value;
				isExtremum &= low.getPixel(row + 1, col) * sign < value;
				isExtremum &= low.getPixel(row + 1, col + 1) * sign < value;

				isExtremum &= center.getPixel(row - 1, col - 1) * sign < value;
				isExtremum &= center.getPixel(row - 1, col) * sign < value;
				isExtremum &= center.getPixel(row - 1, col + 1) * sign < value;
				isExtremum &= center.getPixel(row, col - 1) * sign < value;
				isExtremum &= center.getPixel(row, col + 1) * sign < value;
				isExtremum &= center.getPixel(row + 1, col - 1) * sign < value;
				isExtremum &= center.getPixel(row + 1, col) * sign < value;
				isExtremum &= center.getPixel(row + 1, col + 1) * sign < value;

				isExtremum &= high.getPixel(row - 1, col - 1) * sign < value;
				isExtremum &= high.getPixel(row - 1, col) * sign < value;
				isExtremum &= high.getPixel(row - 1, col + 1) * sign < value;
				isExtremum &= high.getPixel(row, col - 1) * sign < value;
				isExtremum &= high.getPixel(row, col) * sign < value;
				isExtremum &= high.getPixel(row, col + 1) * sign < value;
				isExtremum &= high.getPixel(row + 1, col - 1) * sign < value;
				isExtremum &= high.getPixel(row + 1, col) * sign < value;
				isExtremum &= high.getPixel(row + 1, col + 1) * sign < value;

				if (isExtremum) {					
					Point2D coords = center.toOriginal(new Point2D.Double(row, col));
					
					/*
					// Partial Derivatives
					partialX = (center.getPixel(row, col + 1) - center.getPixel(row, col - 1)) / 2.0f;
					partialY = (center.getPixel(row + 1, col) - center.getPixel(row - 1, col)) / 2.0f;
					partialS = (high.getPixel(row, col) - low.getPixel(row, col)) / 2.0f;
					
					//Hessian
					hessian[0][0] = Fxx(row, col, center);
					hessian[0][1] = Fxy(row, col, center);
					hessian[0][2] = Fxs(row, col, high, low);
					hessian[1][0] = Fyx(row, col, center);
					hessian[1][1] = Fyy(row, col, center);
					hessian[1][2] = Fys(row, col, high, low);
					hessian[2][0] = Fsx(row, col, high, low);
					hessian[2][1] = Fsy(row, col, high, low);
					hessian[2][2] = Fss(row, col, highhigh, center, lowlow);
					
					invHessian = solver.inverse(hessian);
					
					//points h = -invH.(dDog/dX)T
					offsetX = -(invHessian[0][0]*partialX + invHessian[0][1]*partialY + invHessian[0][2]*partialS);
					offsetY = -(invHessian[1][0]*partialX + invHessian[1][1]*partialY + invHessian[1][2]*partialS);
					
					
					ScaleSpacePoint point = new ScaleSpacePoint(coords.getX(), coords.getY(), Math.floor(coords.getX()+offsetX), Math.floor(coords.getY()+offsetY), center.getSigma());
					
					//removing low contrast points
					x = (int)Math.floor(coords.getX()+0.5*partialX*offsetX);
					y = (int)Math.floor(coords.getY()+0.5*partialY*offsetY);
					
					try {
						module = center.getPixel(y < center.getHeight() ? y : center.getHeight()-1, x < center.getWidth() ? x : center.getWidth()-1);
					}catch(ArrayIndexOutOfBoundsException aiob) {
						throw new ArrayIndexOutOfBoundsException();
					}
					if(module >= 0.03) {
						highContrast = true;
					}
					
					
					//remove edge responses
					if(highContrast && (Math.pow(hessian[0][0]+hessian[1][1], 2) / (hessian[0][0]*hessian[1][1]-Math.pow(hessian[0][1]*hessian[1][0],2))) <= 10) {
						points.add(point);						
					}
					*/
					ScaleSpacePoint point = new ScaleSpacePoint(coords.getX(), coords.getY(), coords.getX(), coords.getY(), center.getSigma());
					points.add(point);
				}
			}
		}

		return points;
	}

	// Difference Of Gaussian
	private double dog(int row, int col, Image image) {
		if(row < 0 || col < 0 || row == image.getHeight() || col == image.getWidth()) return 0.0;
		return image.getPixel(row, col);
	}

	// joske @
	// http://www.dreamincode.net/forums/topic/53310-how-to-calculate-hessian-matrix/
	// (20 June 2008 - 03:07 AM)
	// first derivative to x
	private double Fx(int row, int col, Image image) {
		double fr = dog(row, col + 1, image);
		double fl = dog(row, col - 1, image);
		return (fr - fl) / (2.0);
	}

	// first derivative to y
	private double Fy(int row, int col, Image image) {
		double fr = dog(row + 1, col, image);
		double fl = dog(row - 1, col, image);
		return (fr - fl) / (2.0);
	}

	// first derivative to sigma
	private double Fs(int row, int col, Image high, Image low) {
		if(high == null || low == null) return 0.0;
		double fl = dog(row, col, low);
		double fr = dog(row, col, high);
		return (fr - fl) / (2.0);
	}

	// second derivative of Fx to x
	private double Fxx(int row, int col, Image image) {
		double fr = Fx(row, col + 1, image);
		double fl = Fx(row, col - 1, image);
		return (fr - fl) / (2.0);
	}

	// second derivative of Fx to y
	private double Fxy(int row, int col, Image image) {
		double fr = Fx(row + 1, col, image);
		double fl = Fx(row - 1, col, image);
		return (fr - fl) / (2.0);
	}

	// second derivative of Fx to s
	private double Fxs(int row, int col, Image high, Image low) {
		double fl = Fx(row, col, low);
		double fr = Fx(row, col, high);
		return (fr - fl) / (2.0);
	}
	
	// second derivative of Fy to x
	private double Fyx(int row, int col, Image image) {
		double fr = Fy(row, col + 1, image);
		double fl = Fy(row, col - 1, image);
		return (fr - fl) / (2.0);
	}
	
	// second derivative of Fy to y
	private double Fyy(int row, int col, Image image) {
		double fr = Fy(row + 1, col, image);
		double fl = Fy(row - 1, col, image);
		return (fr - fl) / (2.0);
	}


	// second derivative of Fy to s
	private double Fys(int row, int col, Image high, Image low) {
		double fl = Fy(row, col, low);
		double fr = Fy(row, col, high);
		return (fr - fl) / (2.0);
	}
	
	// second derivative of Fs to x
	private double Fsx(int row, int col, Image high, Image low) {
		double fr = Fs(row, col + 1, high, low);
		double fl = Fs(row, col - 1, high, low);
		return (fr - fl) / (2.0);
	}
	
	// second derivative of Fs to y
	private double Fsy(int row, int col, Image high, Image low) {
		double fr = Fs(row + 1, col, high, low);
		double fl = Fs(row - 1, col, high, low);
		return (fr - fl) / (2.0);
	}
	
	// second derivative of Fs to s
	private double Fss(int row, int col, Image highhigh, Image center, Image lowlow) {
		double fl = Fs(row, col, center, lowlow);
		double fr = Fs(row, col, highhigh, center);
		return (fr - fl) / (2.0);
	}


}
