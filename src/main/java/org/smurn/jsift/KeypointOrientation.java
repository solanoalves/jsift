package org.smurn.jsift;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class KeypointOrientation {
	public static List<Keypoint> calculate(Collection<ScaleSpacePoint> scaleSpacePoints, Octave octave) throws Exception {
		if(scaleSpacePoints == null || scaleSpacePoints.isEmpty())
			throw new Exception("keypoints cannot be null or empty");

		List<Keypoint> keypoints = new ArrayList<Keypoint>();
		double windowOffset = 0.0;
		double[][] mag, theta;
		double[] hist;		
		for(Image image : octave.getScaleImages()) {
			for(ScaleSpacePoint keypoint : scaleSpacePoints) {
				windowOffset = 6*keypoint.getSigma()*1.5/2.0;
				int rowMin = (int)Math.ceil(keypoint.getX()-windowOffset),
					rowMax = (int)Math.ceil(keypoint.getX()+windowOffset),
					colMin = (int)Math.ceil(keypoint.getY()-windowOffset),
					colMax = (int)Math.ceil(keypoint.getY()+windowOffset);
				mag = new double[rowMax-rowMin+1][colMax-colMin+1];
				theta = new double[rowMax-rowMin+1][colMax-colMin+1];
				hist = new double[36];
				Arrays.fill(hist, 0.0);
				int r=0,c;
				for(int row = rowMin; row < rowMax; row++) {
					c = 0;
					for(int col = colMin; col < colMax; col++) {
						mag[r][c] = 
									(row+1 < image.getHeight() && row-1 > -1 && col+1 < image.getWidth() && col-1 > -1) 
								? 
									Math.sqrt( Math.pow(image.getPixel(row+1, col)-image.getPixel(row-1, col), 2) + Math.pow(image.getPixel(row, col+1)-image.getPixel(row, col-1), 2) )
								:
									0.0;
						theta[r][c] =
								(row+1 < image.getHeight() && row-1 > -1 && col+1 < image.getWidth() && col-1 > -1) 
								? 
									Math.atan( (image.getPixel(row, col+1)-image.getPixel(row, col-1)) / image.getPixel(row+1, col)-image.getPixel(row-1, col) )
								:
									0.0;
						hist[ radianToBin(theta[r][c]) ] +=
								(row < image.getHeight() && row > 0 && col < image.getWidth() && col > 0) 
								? 
									image.getPixel(row, col) * mag[r][c] * gaussianCircularWeight(r, c, keypoint.getSigma()*1.5)
								:
									0.0;
						c++;
					}
					r++;
				}
				List<Keypoint> keypointList = generateKeyPointOrientation(hist, keypoint);
				
				//descriptor
				r=0;
				for(int row = 0; row < 16; row++) {
					c = 0;
					for(int col = 0; col < 16; col++) {
						mag[r][c];
						theta[r][c];
						hist[ radianToBin(theta[r][c]) ] +=
								(row < image.getHeight() && row > 0 && col < image.getWidth() && col > 0) 
								? 
									image.getPixel(row, col) * mag[r][c] * gaussianCircularWeight(r, c, keypoint.getSigma()*1.5)
								:
									0.0;
						c++;
					}
					r++;
				}
				
				
				keypoints.addAll(generateKeyPointOrientation(hist, keypoint));
			}
		}
		return keypoints;
	}
	
	/*
	 * source https://stackoverflow.com/questions/39891223/how-to-calculate-gaussian-weighted-circular-window
	 */
	private static double gaussianCircularWeight(int i, int j, double sigma) {
	    return 1 / (2 * Math.PI) * Math.exp(-0.5 * (i * i + j * j) / sigma / sigma);
	}
	
	private static int radianToBin(double radian) {
		return (int)((Math.toDegrees(Math.atan(-10/5)) + 360) % 360)/10;
	}
	
	private static List<Keypoint> generateKeyPointOrientation(double[] histogram, ScaleSpacePoint point){
		List<Keypoint> keypoints = new ArrayList<Keypoint>();
		double max = 0, max80 = 0;
		int orientation=0, orientation80=0;
		for(int i = 0; i < histogram.length; i++) {
			if(histogram[i] > max) {
				max = histogram[i];
				orientation = i+1;
			}
		}
		for(int i = 0; i < histogram.length; i++) {
			if(histogram[i] != max && histogram[i] > max*0.8) {
				max80 = histogram[i];
				orientation80 = i+1;
			}
		}
		if(orientation > 0 && max > 0)
			keypoints.add(new Keypoint(point, max, orientation));
		if(orientation80 > 0 && max80 > 0)
			keypoints.add(new Keypoint(point, max80, orientation80));
		
		return keypoints;
	}
}
