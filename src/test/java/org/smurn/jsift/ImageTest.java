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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;

import org.junit.Test;

/**
 * Unit tests for {@link Image}.
 */
public class ImageTest {

	@Test
	public void comparar() {
		try {
			BufferedImage base = ImageIO.read(new File("assinatura.jpg"));
			Image imgBase = new Image(base);
			DecimalFormat df = new DecimalFormat("0.000");
	
			int[][] regions = new int[imgBase.getHeight()][imgBase.getWidth()];
			for (int i = 0; i < regions.length; i++) {
				for (int j = 0; j < regions[0].length; j++) {
					regions[i][j] = -1;
				}
			}
			
			int regioes = TransformImage.countRegions(imgBase, regions);
			double[][] hBins = new double[2][6];
			double[][] vBins = new double[2][6];
			TransformImage.segmentRegionCount(regions, hBins, vBins);
			System.out.println("Regioes "+regioes);
			System.out.println("Vertical");
			for (int i = 0; i < vBins.length; i++) {
				for (int j = 0; j < vBins[0].length; j++) {
					System.out.print(df.format(vBins[i][j])+"\t");
				}
				System.out.println("");
			}
			System.out.println("Horizontal");
			for (int i = 0; i < hBins.length; i++) {
				for (int j = 0; j < hBins[0].length; j++) {
					System.out.print(df.format(hBins[i][j])+"\t");
				}
				System.out.println("");
			}
			//xxxxxxxxxxxxxxxxxxxxxx
			System.out.println("-------------------------");
			//xxxxxxxxxxxxxxxxxxxxxx
			
			BufferedImage query = ImageIO.read(new File("cnh.png"));
			Image imgQuery = new Image(query);
	
			regions = new int[imgQuery.getHeight()][imgQuery.getWidth()];
			for (int i = 0; i < regions.length; i++) {
				for (int j = 0; j < regions[0].length; j++) {
					regions[i][j] = -1;
				}
			}
			regioes = TransformImage.countRegions(imgQuery, regions);
			TransformImage.segmentRegionCount(regions, hBins, vBins);
			System.out.println("Regioes "+regioes);
			System.out.println("Vertical");
			for (int i = 0; i < vBins.length; i++) {
				for (int j = 0; j < vBins[0].length; j++) {
					System.out.print(df.format(vBins[i][j])+"\t");
				}
				System.out.println("");
			}
			System.out.println("Horizontal");
			for (int i = 0; i < hBins.length; i++) {
				for (int j = 0; j < hBins[0].length; j++) {
					System.out.print(df.format(hBins[i][j])+"\t");
				}
				System.out.println("");
			}
			
			
			BufferedImage result = new BufferedImage(Math.max(imgBase.getWidth(),imgQuery.getWidth()), imgBase.getHeight()+imgQuery.getHeight(), imgBase.toBufferedImage().getType());
			Graphics g = result.getGraphics();
			Graphics2D drawer = result.createGraphics() ;
			drawer.setBackground(Color.WHITE);
			drawer.clearRect(0,0,result.getWidth(),result.getHeight());
			g.drawImage(imgBase.toBufferedImage(), 0, 0, null);
			g.drawImage(imgQuery.toBufferedImage(), 0, imgBase.getHeight(), null);
			File o = new File("saved.png");
			ImageIO.write(result, "png", o);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
