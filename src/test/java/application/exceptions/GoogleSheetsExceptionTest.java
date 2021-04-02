/**
 * GoogleSheetsExceptionTest.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2021.
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
package application.exceptions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class GoogleSheetsExceptionTest {

	String validError = new StringBuilder() //
			.append("{") //
			.append("\"error\": {") //
			.append("\"code\": 200,") //
			.append("\"message\": \"error\",") //
			.append("\"status\": \"not good\"") //
			.append("}") //
			.append("}") //
			.toString();

	String invalidError = "<html><body>Sorry mate</body></html>";

	String url = "http://test.com";

	@Test
	void test_validError() {
		GoogleSheetsException ex = GoogleSheetsException.fromJsonString(url, validError);

		Assertions.assertEquals(ex.getUrl(), "http://test.com");
		Assertions.assertEquals(ex.getCode(), 200);
		Assertions.assertEquals(ex.getMessage(), url + "\n\nerror");
		Assertions.assertEquals(ex.getStatus(), "not good");
	}

	@Test
	void test_invalidError() {
		GoogleSheetsException ex = GoogleSheetsException.fromJsonString(url, invalidError);

		Assertions.assertEquals(ex.getUrl(), "http://test.com");
		Assertions.assertEquals(ex.getCode(), -1);
		Assertions.assertEquals(ex.getMessage(), url + "\n\n" + invalidError);
		Assertions.assertEquals(ex.getStatus(),
				"Unable to parse response from Google - expected json but received the following");
	}
}
