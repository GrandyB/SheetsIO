/**
 * AppUtilTest.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2021.
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
package application;

import static org.junit.jupiter.api.Assertions.fail;

import java.net.MalformedURLException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import application.utils.AppUtil;

public class AppUtilTest {

	@Test
	public void test_encodeForUrl_allowsValidSchemas_http() {
		assertNoEncodeForUrlIssues("http://test.com");
	}

	@Test
	public void test_encodeForUrl_allowsValidSchemas_https() {
		assertNoEncodeForUrlIssues("https://test.com");
	}

	@Test
	public void test_encodeForUrl_allowsValidSchemas_file() {
		assertNoEncodeForUrlIssues("file://test.com");
	}

	@Test
	public void test_encodeUrlContent_spaces() {
		Assertions.assertEquals(AppUtil.encodeUrlContent("This has spaces"), "This%20has%20spaces");
	}

	@Test
	public void test_encodeUrlContent_periods() {
		Assertions.assertEquals(AppUtil.encodeUrlContent("This.has.periods"), "This%2Ehas%2Eperiods");
	}

	@Test
	public void test_encodeUrlContent_hyphens() {
		Assertions.assertEquals(AppUtil.encodeUrlContent("This-has-hyphens"), "This%2Dhas%2Dhyphens");
	}

	@Test
	public void test_encodeUrlContent_mix() {
		Assertions.assertEquals(AppUtil.encodeUrlContent("This.has-a mix"), "This%2Ehas%2Da%20mix");
	}

	private void assertNoEncodeForUrlIssues(String url) {
		try {
			AppUtil.encodeForUrl(url);
		} catch (MalformedURLException e) {
			fail("SchemaException", e);
		} catch (Exception e) {
			fail("Encoding failure", e);
		}
	}
}
