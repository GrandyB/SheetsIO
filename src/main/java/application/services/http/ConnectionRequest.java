/**
 * ConnectionRequest.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2021.
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

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Model class for an incoming connection request to the {@link HttpService}.
 * Handles validation of the URL and figuring out what it wants.
 *
 * @author Mark "Grandy" Bishop
 */
@ToString
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ConnectionRequest {
	private static final Pattern URL_PATTERN = Pattern.compile("^/([^/]+)/([^?&/]+).*$");

	/** e.g. /project/asset */
	@Getter
	private final String fullRequest;

	// Parts
	@Getter
	private final String project;
	@Getter
	private final String asset;

	@Getter
	private List<String> parameters = new ArrayList<>();

	@Getter
	private final ConnectionRequestType type;

	/**
	 * @param url
	 *            The request URL, e.g. '/project/asset.png' or '/project/thisText'
	 * @return Optional<ConnectionRequest> With a split out request, or empty if the
	 *         URL is invalid
	 */
	public static Optional<ConnectionRequest> from(URI uri) {
		String path = uri.getPath();
		String paramString = uri.getQuery();

		// Validate what type of asset this is by attempting to retrieve it
		File file = new File(System.getProperty("user.dir") + "/files" + path);
		ConnectionRequestType type = file.exists() ? ConnectionRequestType.FILE : ConnectionRequestType.HTML;

		Matcher urlMatcher = URL_PATTERN.matcher(path);
		String proj = "";
		String asset = "";
		if (urlMatcher.matches()) {
			proj = urlMatcher.group(1);
			asset = urlMatcher.group(2);
		} else if ("/favicon.ico".equals(path)) {
			type = ConnectionRequestType.FAVICON;
		} else {
			// Invalid URL, just use HTML
			type = ConnectionRequestType.HTML;
		}

		// TODO: Value toggling parameters? e.g. ?value=thing, instead of just ?thing
		List<String> params = (paramString == null) ? new ArrayList<>()
				: Arrays.asList(paramString.replace("?", "").split("&"));

		return Optional.of(new ConnectionRequest(path, proj, asset, params, type));
	}

	/** @return whether the request is for a valid resource. */
	public boolean isValid() {
		return asset != null && !asset.isEmpty();
	}

	/**
	 * @return whether the query's parameters contains an entry for the given
	 *         parameter.
	 */
	public boolean hasParam(String param) {
		return this.parameters.contains(param);
	}

	enum ConnectionRequestType {
		FILE, HTML, FAVICON;
	}
}
