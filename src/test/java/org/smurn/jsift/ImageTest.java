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
import java.util.Collection;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.Test;

/**
 * Unit tests for {@link Image}.
 */
public class ImageTest {

	@Test
	public void comparar() {
		try {
			BufferedImage base = ImageIO.read(new File("lena.jpg"));
			 BufferedImage target = ImageIO.read(new File("lena90.jpg"));

			Image imgBase = new Image(base);
			Image imgTarget = new Image(target);
			BufferedImage result = new BufferedImage(imgBase.getWidth()+imgTarget.getWidth(), imgBase.getHeight()+imgTarget.getHeight(), imgBase.toBufferedImage().getType());

			
			ScaleSpaceFactoryImpl spaceFac = new ScaleSpaceFactoryImpl();
			
			// Cria o scalespace com as octaves (4 octaves com 5 imgs cada)
			ScaleSpace ssBase = spaceFac.create(imgBase);
			ScaleSpace ssTarget = spaceFac.create(imgTarget);
			
			
			ExtremaDetector extremaDetector = new ExtremaDetector();
			
			
			Collection<ScaleSpacePoint> pointsBase = extremaDetector.detectKeypoints(ssBase);
			Collection<ScaleSpacePoint> pointsTarget = extremaDetector.detectKeypoints(ssTarget);
			
			
			List<Keypoint> keypointsBase = KeypointsGenerator.calculate(pointsBase, ssBase.getOctaves().get(1));
			List<Keypoint> keypointsTarget = KeypointsGenerator.calculate(pointsTarget, ssTarget.getOctaves().get(1));
			
			System.out.println(keypointsBase.size()+" x "+keypointsTarget.size());
			
			//Desenha
//			Graphics g = result.getGraphics();
//			Graphics2D drawer = result.createGraphics() ;
//			drawer.setBackground(Color.WHITE);
//			drawer.clearRect(0,0,result.getWidth(),result.getHeight());
//			g.drawImage(imgBase.toBufferedImage(), 0, 0, null);
//			g.drawImage(imgTarget.toBufferedImage(), imgBase.getWidth(), 0, null);			
//			for(Keypoint kb : keypointsBase) {
//				for(Keypoint kt : keypointsTarget) {
//					if(EuclideanDistance.calculate(kb.getDescriptor(), kt.getDescriptor()) < 1) {
//						g.setColor(new Color(255, 0, 0));
//						g.drawOval((int)kb.getPoint().getY(), (int)kb.getPoint().getX(), 10, 10);
//						g.drawOval(imgBase.getWidth()+(int)kt.getPoint().getY(), (int)kt.getPoint().getX(), 10, 10);
////						g.drawLine((int)kb.getPoint().getY(), (int)kb.getPoint().getX(), ((int)kt.getPoint().getY())+imgBase.toBufferedImage().getWidth(), (int)kt.getPoint().getX());
//						break;
//					}
//				}
//			}
			
			BufferedImage bi = imgTarget.toBufferedImage();
			Graphics g = bi.getGraphics();
			for(Keypoint kb : keypointsTarget) {
				g.setColor(new Color(255, 255, 255));
				g.drawOval((int)kb.getPoint().getY(), (int)kb.getPoint().getX(), 10, 10);
			}
			
			File outputfile = new File("saved.png");
			ImageIO.write(bi, "png", outputfile);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
