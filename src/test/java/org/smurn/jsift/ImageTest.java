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
			BufferedImage base = ImageIO.read(new File("dennys.png"));
			// BufferedImage target = ImageIO.read(new File("olho.bmp"));

			Image imgBase = new Image(base);
			// Image imgTarget = new Image(target);

			// Cria o scalespace com as octaves (4 octaves com 5 imgs cada)
			ScaleSpaceFactoryImpl spaceFac = new ScaleSpaceFactoryImpl();
			ScaleSpace ssBase = spaceFac.create(imgBase);
			// ScaleSpace ssTarget = spaceFac.create(imgTarget);
			//
			ExtremaDetector extremaDetector = new ExtremaDetector();
			Collection<ScaleSpacePoint> pointsBase = extremaDetector.detectKeypoints(ssBase);
			List<Keypoint> keypoints = KeypointsGenerator.calculate(pointsBase, ssBase.getOctaves().get(1));
			// for (ScaleSpacePoint pointBase : pointsBase) {
			// System.out.println("("+pointBase.getX()+","+pointBase.getY()+","+pointBase.getSigma()+")");
			// }
			System.out.println("total " + keypoints.size());
			// Collection<ScaleSpacePoint> pointsTarget =
			// extremaDetector.detectKeypoints(ssTarget);
			//
			Graphics g = base.getGraphics();
			int mag = 0;
			for (Keypoint point : keypoints) {
				g.setColor(new Color(255, 0, 0));
				mag = (int)(20*point.getMagnitude());
				g.drawOval((int) point.getPoint().getY(), (int) point.getPoint().getX(), mag, mag);
			}
			//
			File outputfile = new File("saved.png");
			ImageIO.write(base, "png", outputfile);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
