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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

/**
 * Unit test for {@link Octave}.
 */
public class OctaveTest {

    @Test(expected = NullPointerException.class)
    public void ctrNullScales() {
        new Octave(null, new ArrayList<Image>());
    }

    @Test(expected = NullPointerException.class)
    public void ctrNullDoG() {
        new Octave(new ArrayList<Image>(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ctrWrongCount() {
        new Octave(Arrays.asList(
                new Image(20, 20),	
                new Image(20, 20),
                new Image(20, 20),
                new Image(20, 20)),
                Arrays.asList(
                new Image(20, 20),
                new Image(20, 20),
                new Image(20, 20),
                new Image(20, 20)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void ctrToFew() {
        new Octave(Arrays.asList(
                new Image(20, 20),
                new Image(20, 20),
                new Image(20, 20)),
                Arrays.asList(
                new Image(20, 20),
                new Image(20, 20)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void ctrWrongSize() {
        new Octave(Arrays.asList(
                new Image(20, 20),
                new Image(20, 20),
                new Image(20, 20),
                new Image(20, 20)),
                Arrays.asList(
                new Image(20, 20),
                new Image(20, 20),
                new Image(20, 19)));
    }

    @Test
    public void getScalesPerOctave() {
        Octave target = new Octave(Arrays.asList(
                new Image(20, 20),
                new Image(20, 20),
                new Image(20, 20),
                new Image(20, 20)),
                Arrays.asList(
                new Image(20, 20),
                new Image(20, 20),
                new Image(20, 20)));
        assertEquals(1, target.getScalesPerOctave());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void scaleImagesImmutable() {
        Octave target = new Octave(Arrays.asList(
                new Image(new float[][]{{1}}),
                new Image(new float[][]{{2}}),
                new Image(new float[][]{{3}}),
                new Image(new float[][]{{4}})),
                Arrays.asList(
                new Image(new float[][]{{5}}),
                new Image(new float[][]{{6}}),
                new Image(new float[][]{{7}})));
        target.getScaleImages().clear();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void dogImmutable() {
        Octave target = new Octave(Arrays.asList(
                new Image(new float[][]{{1}}),
                new Image(new float[][]{{2}}),
                new Image(new float[][]{{3}}),
                new Image(new float[][]{{4}})),
                Arrays.asList(
                new Image(new float[][]{{5}}),
                new Image(new float[][]{{6}}),
                new Image(new float[][]{{7}})));
        target.getDifferenceOfGaussians().clear();
    }

    @Test
    public void getWidth() {
        Octave target = new Octave(Arrays.asList(
                new Image(20, 21),
                new Image(20, 21),
                new Image(20, 21),
                new Image(20, 21)),
                Arrays.asList(
                new Image(20, 21),
                new Image(20, 21),
                new Image(20, 21)));
        assertEquals(21, target.getWidth());
    }

    @Test
    public void getHeight() {
        Octave target = new Octave(Arrays.asList(
                new Image(20, 21),
                new Image(20, 21),
                new Image(20, 21),
                new Image(20, 21)),
                Arrays.asList(
                new Image(20, 21),
                new Image(20, 21),
                new Image(20, 21)));
        assertEquals(20, target.getHeight());
    }
}
