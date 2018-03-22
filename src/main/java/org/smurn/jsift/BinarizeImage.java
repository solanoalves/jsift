package org.smurn.jsift;

public class BinarizeImage {
	public static Image binarizeImage(Image image) {		
		Image ret = null;
		if(image != null) {
			ret = new Image(image.getHeight(), image.getWidth());
			for(int r=0; r<image.getHeight(); r++) {
				for(int c=0; c<image.getWidth(); c++) {
					if(image.getPixel(r, c) < 0.85) {
						ret.setPixel(r, c, 0);
					}else {
						ret.setPixel(r, c, 1);
					}
				}
			}
		}
		return ret;
	}
}
