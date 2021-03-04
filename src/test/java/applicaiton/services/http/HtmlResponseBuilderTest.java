/**
 * HtmlResponseBuilderTest.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2021.
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
package applicaiton.services.http;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import application.services.http.HtmlResponseBuilder;

public class HtmlResponseBuilderTest {

	@Test
	public void test_emptyTemplate() {
		Assertions.assertEquals("<html>" //
				+ "	<head>" //
				+ "		<script type=\"text/javascript\" src=\"http://livejs.com/live.js\"></script>" //
				+ "		<style>body, iframe { margin: 0; padding: 0; background-color: rgba(0, 0, 0, 0); overflow: hidden; } #content { width: 100%; height: 100%; } </style>" //
				+ "	</head>" //
				+ "	<body>" //
				+ "		" //
				+ " </body>" //
				+ "</html>", new HtmlResponseBuilder().empty().build());
	}

	@Test
	public void test_scale() {
		Assertions.assertEquals("<html>" //
				+ "	<head>" //
				+ "		<script type=\"text/javascript\" src=\"http://livejs.com/live.js\"></script>" //
				+ "		<style>body, iframe { margin: 0; padding: 0; background-color: rgba(0, 0, 0, 0); overflow: hidden; }  </style>" //
				+ "	</head>" //
				+ "	<body>" //
				+ "		<div id=\"content\"><span id=\"text\">Test</span></div>" //
				+ " </body>" //
				+ "</html>", new HtmlResponseBuilder().buildDivTemplate("Test").scale(false).build());

	}

	@Test
	public void test_text() {
		Assertions.assertEquals("<html>" //
				+ "	<head>" //
				+ "		<script type=\"text/javascript\" src=\"http://livejs.com/live.js\"></script>" //
				+ "		<style>body, iframe { margin: 0; padding: 0; background-color: rgba(0, 0, 0, 0); overflow: hidden; } #content { width: 100%; height: 100%; } </style>" //
				+ "	</head>" //
				+ "	<body>" //
				+ "		<div id=\"content\"><span id=\"text\">Test</span></div>" //
				+ " </body>" //
				+ "</html>", new HtmlResponseBuilder().buildDivTemplate("Test").build());
	}
}
