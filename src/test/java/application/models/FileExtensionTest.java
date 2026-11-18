/**
 * FileExtensionTest.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package application.models;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import application.models.FileExtension.FileExtensionType;

public class FileExtensionTest {

	@Test
	void test_fromRaw_webp() throws Exception {
		FileExtension ext = FileExtension.fromRaw("webp");
		Assertions.assertEquals(FileExtension.WEBP, ext);
		Assertions.assertEquals("webp", ext.getExtension());
		Assertions.assertEquals("image/webp", ext.getContentType());
		Assertions.assertEquals(FileExtensionType.IMAGE, ext.getType());
		Assertions.assertTrue(ext.isForFile());
	}

	@Test
	void test_imageIo_supportsWebpRoundTrip() throws Exception {
		BufferedImage source = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		Assertions.assertTrue(ImageIO.write(source, "webp", baos), "ImageIO should write webp via webp-imageio");
		Assertions.assertTrue(baos.size() > 0);

		BufferedImage roundTrip = ImageIO.read(new ByteArrayInputStream(baos.toByteArray()));
		Assertions.assertNotNull(roundTrip, "ImageIO should read webp via webp-imageio");
		Assertions.assertEquals(2, roundTrip.getWidth());
		Assertions.assertEquals(2, roundTrip.getHeight());
	}
}
