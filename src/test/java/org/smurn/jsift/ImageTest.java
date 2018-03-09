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
import java.io.IOException;
import java.util.Collection;

import javax.imageio.ImageIO;

import org.junit.Test;

/**
 * Unit tests for {@link Image}.
 */
public class ImageTest {

	@Test
	public void comparar() {
		try {
			BufferedImage base = ImageIO.read(new File("lena.bmp"));
//			BufferedImage target = ImageIO.read(new File("olho.bmp"));

			Image imgBase = new Image(base);
//			Image imgTarget = new Image(target);

			// Cria o scalespace com as octaves (5 octaves com 5 imgs cada)
			ScaleSpaceFactoryImpl spaceFac = new ScaleSpaceFactoryImpl();
			ScaleSpace ssBase = spaceFac.create(imgBase);
//			ScaleSpace ssTarget = spaceFac.create(imgTarget);
//
			ExtremaDetector extremaDetector = new ExtremaDetector();
			Collection<ScaleSpacePoint> pointsBase = extremaDetector.detectKeypoints(ssBase);
//			for (ScaleSpacePoint pointBase : pointsBase) {
//			System.out.println("("+pointBase.getX()+","+pointBase.getY()+","+pointBase.getSigma()+")");
//			}
			System.out.println("total "+pointsBase.size());
//			Collection<ScaleSpacePoint> pointsTarget = extremaDetector.detectKeypoints(ssTarget);
//
//			Color myWhite = new Color(255, 255, 0); // Color white
//			for (ScaleSpacePoint pointBase : pointsBase) {
//				for (ScaleSpacePoint pointTarget : pointsTarget) {
//					if (pointBase.equals(pointTarget)) {
//						base.setRGB((int) pointBase.getX(), (int) pointBase.getY(), myWhite.getRGB());
//					}
//				}
//			}
//
//			File outputfile = new File("saved.bmp");
//			ImageIO.write(base, "bmp", outputfile);

		} catch (IOException e) {
		}
	}
}
