package org.smurn.jsift;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Based on github.com/robwhess
 * @author saguiar
 *
 */
public class KeypointsGenerator {
	private static final double SIGMA_FACTOR = 1.5;
	private static final double ORIENTATION_RADIUS = 3.0 * SIGMA_FACTOR;	
	
	public static List<Keypoint> calculate(Collection<ScaleSpacePoint> scaleSpacePoints, List<Octave> octaves) throws Exception {
		if(scaleSpacePoints == null || scaleSpacePoints.isEmpty())
			throw new Exception("keypoints cannot be null or empty");

		List<Keypoint> keypoints = new ArrayList<Keypoint>();
		double windowOffset = 0.0;
		double[][] mag, theta;
		double[] hist;
		Point2D point;
		double scaleFactor;
		for(ScaleSpacePoint keypoint : scaleSpacePoints) {
			Image image = octaves.get(keypoint.getOctave()).getScaleImages().get(keypoint.getScale());
			scaleFactor = ScaleSpaceFactoryImpl.LOWE_INITIAL_SIGMA * Math.pow(2, keypoint.getScale() / octaves.get(keypoint.getOctave()).getDifferenceOfGaussians().size());
			point = image.fromOriginal(new Point2D.Double(keypoint.getX(), keypoint.getY()));
			windowOffset = ORIENTATION_RADIUS * scaleFactor * 1.1;
			int rowMin = (int)Math.round(point.getY()-windowOffset),
				rowMax = (int)Math.round(point.getY()+windowOffset),
				colMin = (int)Math.round(point.getX()-windowOffset),
				colMax = (int)Math.round(point.getX()+windowOffset);
			mag = new double[rowMax-rowMin][colMax-colMin];
			theta = new double[rowMax-rowMin][colMax-colMin];
			hist = new double[36];
			Arrays.fill(hist, 0.0);
			int r=0,c=0;
			
//			if(keypoint.getX() != 402 && keypoint.getY() != 402) continue;
			
//			System.out.println(keypoint.getX()+","+keypoint.getY());
			
			for(int row = rowMin; row < rowMax; row++) {
				c = 0;
				for(int col = colMin; col < colMax; col++) {
					mag[r][c] = 
								(row+1 < image.getHeight() && row-1 > -1 && col+1 < image.getWidth() && col-1 > -1) 
							? 
								Math.sqrt( Math.pow(image.getPixel(row, col+1)-image.getPixel(row, col-1), 2) + Math.pow(image.getPixel(row+1, col)-image.getPixel(row-1, col), 2) )
							:
								0.0;
					theta[r][c] =
							(row+1 < image.getHeight() && row-1 > -1 && col+1 < image.getWidth() && col-1 > -1) 
							? 
								Math.atan( (image.getPixel(row-1, col)-image.getPixel(row+1, col))/(image.getPixel(row, col+1)-image.getPixel(row, col-1)) )
							:
								0.0;
					hist[ radianToBin(theta[r][c]) ] +=
							(row < image.getHeight() && row > 0 && col < image.getWidth() && col > 0) 
							? 
								mag[r][c] * gaussianCircularWeight(r-mag.length/2, c-mag[0].length/2, ORIENTATION_RADIUS * scaleFactor)
							:
								0.0;
					c++;
				}
				r++;
			}
			List<Keypoint> kps = generateKeyPointDescriptor(hist, keypoint, image, ORIENTATION_RADIUS * scaleFactor);
			keypoints.addAll(kps);
		}
		return keypoints;
	}
	
	public static double gaussianCircularWeight(int i, int j, double sigma) {
	    return Math.exp(-0.5 * (i * i + j * j) / sigma / sigma);
	}
	
	private static int radianToBin(double radian) {
		radian = radian % (2*Math.PI);
		radian = ((radian < 0.0 ? (2.0*Math.PI+radian) : radian));
		int bin = (int) (radian / (Math.PI/18.0));		
		bin = bin < 36 ? bin : 0;
		return bin;
	}
	
	private static List<Keypoint> generateKeyPointDescriptor(double[] histogram, ScaleSpacePoint point, Image image, double sigma){
		List<Keypoint> keypoints = new ArrayList<Keypoint>();
		double max = 0, max80 = 0;
		int orientation=0, orientation80=0;
		
		smooth_ori_hist(histogram);
		
		for(int i = 0; i < histogram.length; i++) {
			if(histogram[i] > max) {
				max = histogram[i];
				orientation = i;
			}
		}

//		for(int i = 0; i < histogram.length; i++) {
//			if(histogram[i] != max && histogram[i] >= max*0.8 && histogram[i] > max80) {
//				max80 = histogram[i];
//				orientation80 = i;
//			}
//		}
		
		if(orientation > 0 && max > 0) {
			double[] desc = DescriptorGenerator.generate(orientation, image, (int)point.getX(), (int)point.getY(), sigma);
			keypoints.add(new Keypoint(point, max, orientation, desc));
		}
//		if(orientation80 > 0 && max80 > 0) {
//			double[] desc80 = DescriptorGenerator.generate(orientation80, image, (int)point.getX(), (int)point.getY(), sigma);
//			keypoints.add(new Keypoint(point, max80, orientation80, desc80));
//		}
		
		return keypoints;
	}
	
	//Adapted from github.com/robwhess
	public static void smooth_ori_hist( double[] hist) {
	  double prev, tmp, h0 = hist[0];
	  int i;

	  prev = hist[hist.length-1];
	  for( i = 0; i < hist.length; i++ )
	    {
	      tmp = hist[i];
	      hist[i] = 0.25 * prev + 0.5 * hist[i] + 
		0.25 * ( ( i+1 == hist.length )? h0 : hist[i+1] );
	      prev = tmp;
	    }
	}
}
