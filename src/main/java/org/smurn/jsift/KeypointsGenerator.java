package org.smurn.jsift;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

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
		int b = (int) (new Date().getTime() & 0xFF);
		double scaleFactor;
//		DecimalFormat df = new DecimalFormat("0.00");
		for(ScaleSpacePoint keypoint : scaleSpacePoints) {
			Image image = octaves.get(keypoint.getOctave()).getScaleImages().get(keypoint.getScale());
			scaleFactor = ScaleSpaceFactoryImpl.LOWE_INITIAL_SIGMA * Math.pow(2, keypoint.getScale() / octaves.get(keypoint.getOctave()).getDifferenceOfGaussians().size());
			point = image.fromOriginal(new Point2D.Double(keypoint.getX(), keypoint.getY()));
			windowOffset = ORIENTATION_RADIUS * scaleFactor;
			int rowMin = (int)Math.round(point.getY()-windowOffset),
				rowMax = (int)Math.round(point.getY()+windowOffset),
				colMin = (int)Math.round(point.getX()-windowOffset),
				colMax = (int)Math.round(point.getX()+windowOffset);
			mag = new double[rowMax-rowMin][colMax-colMin];
			theta = new double[rowMax-rowMin][colMax-colMin];
			hist = new double[36];
			Arrays.fill(hist, 0.0);
			int r=0,c,rC=0,cC=0, bin=0;
//			//remover
//			BufferedImage bi = image.toBufferedImage();
//			Graphics g1 = bi.getGraphics();
//			g1.setColor(new Color(255, 255, 255));
//			//remover
			
			for(int row = rowMin; row < rowMax; row++) {
				c = 0;
				for(int col = colMin; col < colMax; col++) {
					if(row == (int)point.getY() && col == (int)point.getX()) {
						rC = r;
						cC = c;
					}
					mag[r][c] = 
								(row+1 < image.getHeight() && row-1 > -1 && col+1 < image.getWidth() && col-1 > -1) 
							? 
								Math.sqrt( Math.pow(image.getPixel(row, col+1)-image.getPixel(row, col-1), 2) + Math.pow(image.getPixel(row+1, col)-image.getPixel(row-1, col), 2) )
							:
								0.0;
					theta[r][c] =
							(row+1 < image.getHeight() && row-1 > -1 && col+1 < image.getWidth() && col-1 > -1) 
							? 
								Math.atan( (image.getPixel(row+1, col)-image.getPixel(row-1, col))/(image.getPixel(row, col+1)-image.getPixel(row, col-1)) )
							:
								0.0;
					bin = radianToBin((theta[r][c] < 0 ? 2*Math.PI+theta[r][c] : theta[r][c])%(Math.PI*2));
					hist[ bin ] +=
							(row < image.getHeight() && row > 0 && col < image.getWidth() && col > 0) 
							? 
								mag[r][c] * gaussianCircularWeight(r, c, ORIENTATION_RADIUS * scaleFactor)
							:
								0.0;
					c++;
				}
				r++;
			}
//			g1.drawOval((int)point.getX()-10, (int)point.getY()-10, 20, 20);
//			g1.drawLine((int)point.getX(), (int)point.getY(), (int)(point.getX() + Math.cos(theta[rC][cC])*10), (int)(point.getY() + Math.sin(theta[rC][cC])*10));
			List<Keypoint> kps = generateKeyPointDescriptor(hist, keypoint, mag, theta, cC, rC);
			keypoints.addAll(kps);
			//remover
//			g1.setColor(new Color(0,0,0));
//			g1.drawLine((int)point.getX(), (int)point.getY(), (int)point.getX(), (int)point.getY());
//			File outputfile = new File("image"+(b++)+".png");
//			ImageIO.write(bi, "png", outputfile);
			//remover
		}
//		}
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

//		for(int i = 0; i < histogram.length; i++) {
//			if(histogram[i] != max && histogram[i] >= max*0.8 && histogram[i] > max80) {
//				max80 = histogram[i];
//				orientation80 = i;
//			}
//		}
		
		//rotate relative to dominant
		double[][] t = new double[mag.length][];
//		double[][] t80 = new double[mag.length][];
		for(int i = 0; i < theta.length; i++) {
		    t[i] = theta[i].clone();
//		    t80[i] = theta[i].clone();
		    for(int j=0; j<t[0].length; j++) {
		    	t[i][j] = theta[i][j] - (Math.PI/18)*orientation;
		    }
//		    if(max80 > 0) {
//			    for(int j=0; j<t80[0].length; j++) {
//			    	t80[i][j] = t80[i][j] - (Math.PI/18)*orientation80;
//			    }
//		    }
		}
//		for(int i = 0; i < t.length; i++) {
//		    for(int j=0; j<t[0].length; j++) {
//		    	System.out.print(df.format(Math.toDegrees((t[i][j] < 0 ? 2*Math.PI+t[i][j] : t[i][j])%(Math.PI*2)))+"\t");
//		    }
//		    System.out.println("");
//		}
//		System.out.println("Dominante: "+orientation*10+" centro:"+Math.toDegrees(theta[cC][rC])+" corrigido: "+Math.toDegrees(t[cC][rC]));
		
		if(orientation > 0 && max > 0) {
			double[] desc = DescriptorGenerator.generate(orientation, mag, t, cC, rC);
			keypoints.add(new Keypoint(point, max, orientation, desc));
		}
//		if(orientation80 > 0 && max80 > 0) {
//			double[] desc80 = DescriptorGenerator.generate(orientation80, mag, t80, cC, rC);
//			keypoints.add(new Keypoint(point, max80, orientation80, desc80));
//		}
		
		return keypoints;
	}
}
