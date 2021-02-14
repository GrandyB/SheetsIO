/**
 * HttpBinding.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2021.
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
package application.services.http;

import java.net.InetSocketAddress;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * A connection and an asset.
 *
 * @author Mark "Grandy" Bishop
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode
public class HttpBinding {
	@Getter
	private InetSocketAddress socket;
	@Getter
	private ConnectionRequest request;

	private HttpBinding() {
		// Nothing
	}

	public static HttpBinding from(InetSocketAddress socket, ConnectionRequest request) {
		return new HttpBinding(socket, request);
	}
}
