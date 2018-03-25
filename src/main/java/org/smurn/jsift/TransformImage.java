package org.smurn.jsift;

import java.awt.Point;
import java.util.LinkedList;
import java.util.Queue;

public class TransformImage {
	
	public static Image binarizeImage(Image image) {		
		Image ret = null;
		if(image != null) {
			ret = new Image(image.getHeight(), image.getWidth());
			for(int r=0; r<image.getHeight(); r++) {
				for(int c=0; c<image.getWidth(); c++) {
					if(image.getPixel(r, c) < 0.85) {
						ret.setPixel(r, c, 1);
					}else {
						ret.setPixel(r, c, 0);
					}
				}
			}
		}
		ret = TransformImage.cutMinMaxPixels(ret);
		ret = dilate(ret);
		return ret;
	}
	
	public static Image dilate(Image image) {
		if(image != null) {
			for(int r=0; r<image.getHeight(); r++) {
				for(int c=0; c<image.getWidth(); c++) {
					if(image.getPixel(r, c) == 1) {
						if(r-1>0 && image.getPixel(r-1, c) == 0) image.setPixel(r-1, c, 2.0f);
						if(c-1>0 && image.getPixel(r, c-1) == 0) image.setPixel(r, c-1, 2.0f);
						if(r+1<image.getHeight() && image.getPixel(r+1, c) == 0) image.setPixel(r+1, c, 2.0f);
						if(c+1<image.getWidth() && image.getPixel(r, c+1) == 0) image.setPixel(r, c+1, 2.0f);
					}
				}
			}
			for(int r=0; r<image.getHeight(); r++) {
				for(int c=0; c<image.getWidth(); c++) {
					if(image.getPixel(r, c) == 2) {
						image.setPixel(r, c, 1.0f);
					}
				}
			}
		}
		return image;
	}
	
	public static Image erode(Image image) {
		if(image != null) {
			boolean erode;
			for(int r=0; r<image.getHeight(); r++) {
				for(int c=0; c<image.getWidth(); c++) {
					if(image.getPixel(r, c) == 1) {
						erode = false;
						for(int i=-1; i<=1; i++) {
							for(int j=-1; j<=1; j++) {
								erode |= r+i > 0 && c+j > 0 && r+i < image.getHeight() && c+j < image.getWidth() && image.getPixel(r+i, c+j) == 0;
								if(erode)
									image.setPixel(r, c, 2);
							}
						}
					}
				}
			}
			for(int r=0; r<image.getHeight(); r++) {
				for(int c=0; c<image.getWidth(); c++) {
					if(image.getPixel(r, c) == 2) {
						image.setPixel(r, c, 0.0f);
					}
				}
			}
		}
		return image;
	}
	
	private static Image skeletonization(Image image, boolean step1) {
		Image ret = null;
		boolean fim = true;
		if(image != null) {
			ret = new Image(image.getHeight(), image.getWidth());
			float window[][] = new float[3][3];
			int sum = 0;
			int NP1, SP1;
			boolean eliminate = true;
			for(int r=0; r<image.getHeight(); r++) {
				for(int c=0; c<image.getWidth(); c++) {
					eliminate = true;
					sum = 0;
					if(image.getPixel(r, c) == 0 || r==0 || c==0 || r==image.getHeight()-1 || c==image.getWidth()-1) continue;
					
					for(int i=-1; i<=1; i++)
						for(int j=-1; j<=1; j++) {
							if(r+i > 0 && r+i < image.getHeight() && c+j > 0 && c+j < image.getWidth()) {								
								window[1+i][1+j] = image.getPixel(r+i, c+j);
								sum += image.getPixel(r+i, c+j);
							}
						}
					NP1 = sum;
					SP1 = (window[0][2]>window[0][1]?1:0)+(window[1][2]>window[0][2]?1:0)+(window[2][2]>window[1][2]?1:0)
						+ (window[2][1]>window[2][2]?1:0)+(window[2][0]>window[2][1]?1:0)+(window[1][0]>window[2][0]?1:0)
						+ (window[0][0]>window[1][0]?1:0)+(window[0][1]>window[0][0]?1:0);
					eliminate &= (NP1 >= 4 && NP1 <= 6); //a
					eliminate &= (SP1 == 1); //b
					if(step1) {
						eliminate &= (window[0][1] * window[1][2] * window[2][1] == 0); //c
						eliminate &= (window[1][2] * window[2][1] * window[0][1] == 0); //d
					}else {
						eliminate &= (window[0][1] * window[1][2] * window[1][0] == 0); //c'
						eliminate &= (window[0][1] * window[2][1] * window[1][0] == 0); //d'
					}
					
					if(!eliminate) {
						ret.setPixel(r, c, 1.0f);
					}
					
					if(image.getPixel(r, c) != ret.getPixel(r, c)) {
						fim = false;
					}
				}
			}
		}
		return fim ? null : ret;
	}
	
	public static int countRegions(Image image, int[][] region) {
		int rc = 0;
		if(image != null) {
			int marker = 0;
			for (int row = 0; row < image.getHeight(); row++) {
                for (int col = 0; col < image.getWidth(); col++) {
                    if (image.getPixel(row, col) == 0 && region[row][col] == -1) {
                        Queue<Point> queue = new LinkedList<Point>();
                        queue.add(new Point(col, row));

                        while (!queue.isEmpty()) {
                            Point p = queue.remove();

                            if ((p.y >= 0) && (p.y < image.getHeight() && (p.x >= 0) && (p.x < image.getWidth()))) {
                                if (region[p.y][p.x] == -1 && image.getPixel(p.y, p.x) == 0) {
                                    region[p.y][p.x] = marker;
                                    queue.add(new Point(p.x + 1, p.y));
                                    queue.add(new Point(p.x - 1, p.y));
                                    queue.add(new Point(p.x, p.y + 1));
                                    queue.add(new Point(p.x, p.y - 1));
                                }
                            }
                        }
                        marker += 10;
                        rc++;
                    }
                }
            }
		}
		return rc;
	}
	
	public static void segmentRegionCount(int[][] markers, int hBins[][], int vBins[][]) {
		if(markers != null) {
			for (int row = 0; row < markers.length; row++) {
                for (int col = 0; col < markers[0].length; col++) {
                	vBins[col / (markers[0].length/2+1)][row / (markers.length/vBins[0].length+1)] += (markers[row][col] > 0 ? 1 : 0);
                	hBins[row / (markers.length/2+1)][col / (markers[0].length/hBins[0].length+1)] += (markers[row][col] > 0 ? 1 : 0);
                }
            }
		}
	}
	
	public static Image cutMinMaxPixels(Image image) {
		if(image != null) {
			int minCol=image.getWidth(),minRow=image.getHeight(), maxCol=0,maxRow=0;
			for (int row = 0; row < image.getHeight(); row++) {
                for (int col = 0; col < image.getWidth(); col++) {
                    if (image.getPixel(row, col) == 1) {
                    	if(col < minCol) minCol = col;
                    	if(col > maxCol) maxCol = col;
                    	if(row < minRow) minRow = row;
                    	if(row > maxRow) maxRow = row;
                    }
                }
            }
			int offset = 10;
			Image img = new Image(maxRow-minRow+offset, maxCol-minCol+offset);
			for(int c = 0; c < img.getWidth(); c++) {
				for(int r = 0; r < img.getHeight(); r++) {
					img.setPixel(r, c, 0);
				}
			}
			for(int c = 0; c < img.getWidth()-offset; c++) {
				for(int r = 0; r < img.getHeight()-offset; r++) {
					img.setPixel(r+offset/2, c+offset/2, image.getPixel(r+minRow, c+minCol));
				}
			}
			image = img;
		}
		return image;
	}
	
	
}
