package org.smurn.jsift;

import java.util.Arrays;

public class DescriptorGenerator {
	public static double[] generate(int keypointDirection, double[][] mag, double[][] theta, int centerX, int centerY) {
		//descriptor
		double[] desc = new double[128];
		Arrays.fill(desc, 0.0);
		int offset = 0, c=0, r=-1;
		for(int row = centerY - 8; row < centerY+8; row++) {
			r++;
			c=-1;
			if(! (row > 0 && row < mag.length)) {
				continue;
			}
			for(int col = centerX-8; col < centerX+8; col++) {
				c++;
				if(! (col > 0 && col < mag[0].length) || mag[row][col] == 0) {
					continue;
				}
				if( !(c > 0 && c < 16 && r > 0 && r < 16) ) {
					continue;
				}
				offset = (4*(r/4))*8 + (c/4)*8;
				desc[offset + radianToBin(theta[row][col], keypointDirection)] += mag[row][col] * KeypointsGenerator.gaussianCircularWeight(r, c, 4.0);
				if(desc[offset + radianToBin(theta[row][col], keypointDirection)] > 0.2)
					desc[offset + radianToBin(theta[row][col], keypointDirection)] = 0.2;					
			}
		}
		normalize(desc);		
		return desc;
	}
	
	private static int radianToBin(double radian, int keypointDirection) {
		int dif = (int) (((Math.toDegrees(radian) + 360) % 360) - keypointDirection*10);
		if(dif < 0)
			dif = 360 + dif;
		return dif/45;
	}
	
	private static void normalize(double[] toNormalize) {
		double[] _toNormalize = new double[toNormalize.length];
		System.arraycopy( toNormalize, 0, _toNormalize, 0, toNormalize.length );
		
		Arrays.sort(_toNormalize);
		
		for(int d = 0; d<toNormalize.length; d++) {
			toNormalize[d] = (toNormalize[d]-_toNormalize[0])/(_toNormalize[_toNormalize.length-1]-_toNormalize[0]);
		}
	}
}
