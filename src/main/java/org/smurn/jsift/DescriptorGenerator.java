package org.smurn.jsift;

import java.text.DecimalFormat;
import java.util.Arrays;

public class DescriptorGenerator {
	public static int cont = 0;
	public static double[] generate(double orientation, Image image, int centerX, int centerY, double sigma) {
		//descriptor
		double[][] mag, theta;
		mag = new double[16][16];
		theta = new double[16][16];
		double[] desc = new double[128];
		Arrays.fill(desc, 0.0);
		int offset = 0, c=0, r=-1, cRot=0, rRot=0;
		double rad = orientation * Math.PI/18;
		double cos = Math.cos(-rad);
		double sen = Math.sin(-rad);

		DecimalFormat df = new DecimalFormat("0.00");

		for(int row = centerY - 8; row < centerY+8; row++) {
			r++;
			c=-1;
			for(int col = centerX-8; col < centerX+8; col++) {
				c++;
				if( !(c > 0 && c < 16 && r > 0 && r < 16) ) {
					continue;
				}
				
				if(row < 0 || col < 0) continue;
				
				rRot = (int)(r*cos + c*sen);
				cRot = (int)(c*cos - r*sen);
				
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
				theta[r][c] = theta[r][c] - orientation;

				while(theta[r][c] < 0)
					theta[r][c] += 2*Math.PI;
				while(theta[r][c] >= 2*Math.PI)
					theta[r][c] -= 2*Math.PI;

				offset = (4*(r/4))*8 + (c/4)*8;
				desc[ offset + radianToBin(theta[r][c]) ] += mag[r][c] * KeypointsGenerator.gaussianCircularWeight(r-mag.length/2, c-mag[0].length/2, 8.0);
//				System.out.print("("+r+","+c+")"+"\t");
//				System.out.print(df.format(desc[ offset + radianToBin(theta[r][c]) ])+"\t");
				
//				if(desc[offset + radianToBin(theta[r][c])] > 0.2)
//					desc[offset + radianToBin(theta[r][c])] = 0.2;
			}	
//			System.out.println("");
		}
		
		normalize(desc);
		
//		for(int i=0; i<desc.length; i++) {
//			System.out.println(i+"\t"+df.format(desc[i]));
//		}
		
		return desc;
	}
	
	private static int radianToBin(double radian) {
		radian = radian % (2*Math.PI);
		radian = ((radian < 0.0 ? (2.0*Math.PI+radian) : radian));
		int bin = (int) (radian / (Math.PI/4.0)); 
		bin = bin < 8 ? bin : 0;
		return bin;
	}
	
	private static void normalize(double[] toNormalize) {
		double[] _toNormalize = new double[toNormalize.length];
		System.arraycopy( toNormalize, 0, _toNormalize, 0, toNormalize.length );
		
		Arrays.sort(_toNormalize);
		
		for(int d = 0; d<toNormalize.length; d++) {
			toNormalize[d] = ((_toNormalize[_toNormalize.length-1]-_toNormalize[0]) == 0) ? 0 : (toNormalize[d]-_toNormalize[0])/(_toNormalize[_toNormalize.length-1]-_toNormalize[0]);
		}
	}
}
