/**
 * GoogleSheetsException.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020.
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

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Exception from a request to Google Sheets v4 API.
 *
 * @author Mark "Grandy" Bishop
 */
@RequiredArgsConstructor
public class GoogleSheetsException extends Exception {
	private static final long serialVersionUID = 1L;

	@Getter
	private final int code;

	@Getter
	private final String message;

	@Getter
	private final String status;

	/** Create a {@link GoogleSheetsException} from the given response json. */
	public static GoogleSheetsException fromJsonString(String json) {
		JsonElement elem = JsonParser.parseString(json);
		JsonElement error = elem.getAsJsonObject().get("error");

		int code = error.getAsJsonObject().get("code").getAsInt();
		String message = error.getAsJsonObject().get("message").getAsString();
		String status = error.getAsJsonObject().get("status").getAsString();

		return new GoogleSheetsException(code, message, status);
	}

	/** @return formatted string of 'code - ERROR'. */
	public String getHeader() {
		return String.format("%d - %s", code, status);
	}
}
