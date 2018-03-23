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

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.junit.Test;

/**
 * Unit tests for {@link Image}.
 */
public class ImageTest {

	@Test
	public void comparar() {
		try {
			BufferedImage base = ImageIO.read(new File("cnh.png"));
			Image imgBase = TransformImage.binarizeSkeletonizeImage(new Image(base));
	
			int[][] regions = new int[imgBase.getHeight()][imgBase.getWidth()];
			for (int i = 0; i < regions.length; i++) {
				for (int j = 0; j < regions[0].length; j++) {
					regions[i][j] = -1;
				}
			}
			
			int regioes = TransformImage.countRegions(imgBase, regions);
			int[] hBins = new int[8];
			int[] vBins = new int[8];
			TransformImage.segmentRegionCount(regions, hBins, vBins);
			System.out.println("Regioes "+regioes);
			for (int i = 0; i < vBins.length; i++) {
				System.out.println(i+"\t"+vBins[i]);
			}
			for (int i = 0; i < hBins.length; i++) {
				System.out.println(i+"\t"+hBins[i]);
			}			
			//xxxxxxxxxxxxxxxxxxxxxx
			System.out.println("-------------------------");
			//xxxxxxxxxxxxxxxxxxxxxx
			
			BufferedImage query = ImageIO.read(new File("fran.png"));
			Image imgQuery = TransformImage.binarizeSkeletonizeImage(new Image(query));
	
			regions = new int[imgQuery.getHeight()][imgQuery.getWidth()];
			for (int i = 0; i < regions.length; i++) {
				for (int j = 0; j < regions[0].length; j++) {
					regions[i][j] = -1;
				}
			}
			regioes = TransformImage.countRegions(imgQuery, regions);
			TransformImage.segmentRegionCount(regions, hBins, vBins);
			System.out.println("Regioes "+regioes);
			for (int i = 0; i < vBins.length; i++) {
				System.out.println(i+"\t"+vBins[i]);
			}
			for (int i = 0; i < hBins.length; i++) {
				System.out.println(i+"\t"+hBins[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
