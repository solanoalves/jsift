package org.smurn.jsift;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class KeypointsGenerator {
	public static List<Keypoint> calculate(Collection<ScaleSpacePoint> scaleSpacePoints, List<Octave> octaves) throws Exception {
		if(scaleSpacePoints == null || scaleSpacePoints.isEmpty())
			throw new Exception("keypoints cannot be null or empty");

		List<Keypoint> keypoints = new ArrayList<Keypoint>();
		double windowOffset = 0.0;
		double[][] mag, theta;
		double[] hist;		
		for(ScaleSpacePoint keypoint : scaleSpacePoints) {
			Image image = octaves.get(keypoint.getOctave()).getDifferenceOfGaussians().get(keypoint.getScale());
			
			windowOffset = 6*keypoint.getSigma()*1.5/2.0;
			int rowMin = (int)Math.ceil(keypoint.getY()-windowOffset),
				rowMax = (int)Math.ceil(keypoint.getY()+windowOffset),
				colMin = (int)Math.ceil(keypoint.getX()-windowOffset),
				colMax = (int)Math.ceil(keypoint.getX()+windowOffset);
			mag = new double[rowMax-rowMin+1][colMax-colMin+1];
			theta = new double[rowMax-rowMin+1][colMax-colMin+1];
			hist = new double[36];
			Arrays.fill(hist, 0.0);
			int r=0,c,rC=0,cC=0;
			System.out.println("Keypoint ("+keypoint.getX()+","+keypoint.getY()+")");
			for(int row = rowMin; row < rowMax; row++) {
				c = 0;
				for(int col = colMin; col < colMax; col++) {
					if(row == (int)keypoint.getY() && col == (int)keypoint.getX()) {
						rC = r;
						cC = c;
					}
					mag[r][c] = 
								(row+1 < image.getHeight() && row-1 > -1 && col+1 < image.getWidth() && col-1 > -1) 
							? 
								Math.sqrt( Math.pow(image.getPixel(row, col+1)-image.getPixel(row, col-1), 2) + Math.pow(image.getPixel(row-1, col)-image.getPixel(row+1, col), 2) )
							:
								0.0;
					theta[r][c] =
							(row+1 < image.getHeight() && row-1 > -1 && col+1 < image.getWidth() && col-1 > -1) 
							? 
								Math.atan2( (image.getPixel(row-1, col)-image.getPixel(row+1, col)) , image.getPixel(row, col+1)-image.getPixel(row, col-1) )
							:
								0.0;
					theta[r][c] = theta[r][c] < 0 ? (2*Math.PI-theta[r][c]) : theta[r][c];
					//System.out.println((k++)+" mag["+r+","+c+"]="+mag[r][c]+" theta["+r+","+c+"]="+theta[r][c]);
					hist[ radianToBin(theta[r][c]) ] +=
							(row < image.getHeight() && row > 0 && col < image.getWidth() && col > 0) 
							? 
								mag[r][c] * gaussianCircularWeight(r, c, keypoint.getSigma()*1.5)
							:
								0.0;
					c++;
				}
				r++;
			}
			List<Keypoint> kps = generateKeyPointDescriptor(hist, keypoint, mag, theta, cC, rC);
			keypoints.addAll(kps);
		}
//		}
		return keypoints;
	}
	
	/*
	 * source https://stackoverflow.com/questions/39891223/how-to-calculate-gaussian-weighted-circular-window
	 */
	public static double gaussianCircularWeight(int i, int j, double sigma) {
	    return 1 / (2 * Math.PI) * Math.exp(-0.5 * (i * i + j * j) / sigma / sigma);
	}
	
	private static int radianToBin(double radian) {
		radian = radian > Math.PI*2 ? radian-2*Math.PI : radian;
		return (int)(radian / (Math.PI/18.0));
	}
	
	private static List<Keypoint> generateKeyPointDescriptor(double[] histogram, ScaleSpacePoint point, double[][] mag, double[][] theta, int cC, int rC){
		List<Keypoint> keypoints = new ArrayList<Keypoint>();
		double max = 0, max80 = 0;
		int orientation=0, orientation80=0;
		for(int i = 0; i < histogram.length; i++) {
			if(histogram[i] > max) {
				max = histogram[i];
				orientation = i;
			}
		}
		for(int i = 0; i < histogram.length; i++) {
			if(histogram[i] != max && histogram[i] > max*0.8) {
				max80 = histogram[i];
				orientation80 = i;
			}
		}
		if(orientation > 0 && max > 0) {
			double[] desc = DescriptorGenerator.generate(orientation, mag, theta, cC, rC);
			keypoints.add(new Keypoint(point, max, orientation, theta[rC][cC], desc));
		}
		if(orientation80 > 0 && max80 > 0) {
			double[] desc80 = DescriptorGenerator.generate(orientation80, mag, theta, cC, rC);
			keypoints.add(new Keypoint(point, max80, orientation80, theta[rC][cC], desc80));
		}
		
		return keypoints;
	}
}
