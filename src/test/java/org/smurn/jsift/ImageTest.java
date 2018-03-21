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
			ScaleSpaceFactoryImpl spaceFac = new ScaleSpaceFactoryImpl();
			ExtremaDetector extremaDetector = new ExtremaDetector();
			
			BufferedImage base = ImageIO.read(new File("lena.jpg"));
			Image imgBase = new Image(base);
			ScaleSpace ssBase = spaceFac.create(imgBase);
			Collection<ScaleSpacePoint> pointsBase = extremaDetector.detectKeypoints(ssBase);
			List<Keypoint> keypointsBase = KeypointsGenerator.calculate(pointsBase, ssBase.getOctaves());
			System.out.println("-----");
			BufferedImage target = ImageIO.read(new File("lena90.jpg"));
			Image imgTarget = new Image(target);
			ScaleSpace ssTarget = spaceFac.create(imgTarget);
			Collection<ScaleSpacePoint> pointsTarget = extremaDetector.detectKeypoints(ssTarget);
			List<Keypoint> keypointsTarget = KeypointsGenerator.calculate(pointsTarget, ssTarget.getOctaves());
//
			BufferedImage result = new BufferedImage(imgBase.getWidth()+imgTarget.getWidth(), imgBase.getHeight()+imgTarget.getHeight(), imgBase.toBufferedImage().getType());
			
//			//Pontos
//			BufferedImage bio1 = imgBase.toBufferedImage();
//			Graphics go1 = bio1.getGraphics();
//			go1.setColor(new Color(255, 255, 255));
//			for(ScaleSpacePoint point : pointsBase) {
//				go1.drawOval((int)point.getX()-7, (int)point.getY()-7, 14, 14);
//			}
//			File outputfileo1 = new File("saved_1.png");
//			ImageIO.write(bio1, "png", outputfileo1);
//			BufferedImage bio2 = imgTarget.toBufferedImage();
//			Graphics go2 = bio2.getGraphics();
//			go2.setColor(new Color(255, 255, 255));
//			for(ScaleSpacePoint point : pointsTarget) {
//				go2.drawOval((int)point.getX()-7, (int)point.getY()-7, 14, 14);
//			}
//			File outputfileo2 = new File("saved_2.png");
//			ImageIO.write(bio2, "png", outputfileo2);
			
			
			//Desenha
			Graphics g = result.getGraphics();
			Graphics2D drawer = result.createGraphics() ;
			drawer.setBackground(Color.WHITE);
			drawer.clearRect(0,0,result.getWidth(),result.getHeight());
			g.drawImage(imgBase.toBufferedImage(), 0, 0, null);
			g.drawImage(imgTarget.toBufferedImage(), imgBase.getWidth(), 0, null);
			g.setColor(new Color(255, 255, 255));
			double dist = 0;
			DecimalFormat df = new DecimalFormat("0.0000");
			for(Keypoint kb : keypointsBase) {
				for(Keypoint kt : keypointsTarget) {
					dist = EuclideanDistance.calculate(kb.getDescriptor(), kt.getDescriptor());
					if(dist < 0.01) {
						System.out.print ("("+kb.getPoint().getX()+","+kb.getPoint().getY()+") "+"("+kt.getPoint().getX()+","+kt.getPoint().getY()+")");
						System.out.println(" Distancia "+dist);						
						
						g.drawString(df.format(dist), (int)kb.getPoint().getX()-5, (int)kb.getPoint().getY()-5);
						g.drawOval((int)kb.getPoint().getX()-5, (int)kb.getPoint().getY()-5, 10, 10);
						g.drawOval(imgBase.getWidth()+(int)kt.getPoint().getX()-5, (int)kt.getPoint().getY()-5, 10, 10);
						g.drawLine((int)kb.getPoint().getX(), (int)kb.getPoint().getY(), ((int)kt.getPoint().getX())+imgBase.toBufferedImage().getWidth(), (int)kt.getPoint().getY());
					}
				}
			}
			File o = new File("saved.png");
			ImageIO.write(result, "png", o);
			
//			System.out.println("--------");
			BufferedImage bi = imgBase.toBufferedImage();
			Graphics g1 = bi.getGraphics();
			g1.setColor(new Color(255, 255, 255));
			int r = 0;
			for(Keypoint kb : keypointsBase) {
				r = (int)(20*kb.getMagnitude());
				g1.drawOval((int)kb.getPoint().getX()-r/2, (int)kb.getPoint().getY()-r/2, r, r);
				g1.drawLine((int)kb.getPoint().getX(), (int)kb.getPoint().getY(), (int)(kb.getPoint().getX() + (r/2)*Math.cos(kb.getDirection()*(Math.PI/18.0))), (int)(kb.getPoint().getY() + (r/2)*Math.sin(kb.getDirection()*(Math.PI/18.0))));
			}
			File outputfile = new File("saved1.png");
			ImageIO.write(bi, "png", outputfile);
			System.out.println("--------");
			BufferedImage bi2 = imgTarget.toBufferedImage();
			Graphics g2 = bi2.getGraphics();
			g2.setColor(new Color(255, 255, 255));
			for(Keypoint kb : keypointsTarget) {
				r = (int)(20*kb.getMagnitude());
				g2.drawOval((int)kb.getPoint().getX()-r/2, (int)kb.getPoint().getY()-r/2, r, r);
				g2.drawLine((int)kb.getPoint().getX(), (int)kb.getPoint().getY(), (int)(kb.getPoint().getX() + (r/2)*Math.cos(kb.getDirection()*(Math.PI/18.0))), (int)(kb.getPoint().getY() + (r/2)*Math.sin(kb.getDirection()*(Math.PI/18.0))));
			}
			File outputfile2 = new File("saved2.png");
			ImageIO.write(bi2, "png", outputfile2);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
