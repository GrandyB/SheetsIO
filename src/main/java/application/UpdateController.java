/**
 * UpdateController.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020.
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.GsonBuilder;

import application.exceptions.IllegalFileExtensionException;
import application.models.CellWrapper;
import application.models.json.GoogleSheetsResponse;

/**
 * Makes the calls to update the sheet. May be used within threads, e.g.
 * {@link UpdateRunnable}.
 *
 * @author Mark "Grandy" Bishop
 */
public class UpdateController {
	private static final Logger LOGGER = LogManager.getLogger(UpdateController.class);

	private final SheetCache cache = new SheetCache();
	private final FileUpdater fileUpdater = new FileUpdater(new FileIO());

	private ConfigHolder config;
	private String urlString;
	private URL url;

	/**
	 * Set a new config, thus needing to reset state and start anew.
	 * 
	 * @throws IllegalFileExtensionException
	 */
	public synchronized void setConfig(ConfigHolder config, boolean fromScratch)
			throws IOException, IllegalFileExtensionException {
		this.config = config;

		this.urlString = "https://sheets.googleapis.com/v4/spreadsheets/" + config.getSpreadsheetId() + "/values/"
				+ config.getWorksheetName() + /* "!rangeHere" + */ "?key=" + config.getApiKey()
				+ "&majorDimension=COLUMNS&valueRenderOption=FORMATTED_VALUE";
		LOGGER.debug("URL: {}", this.urlString);

		if (fromScratch) {
			this.cache.setup(config.getCells());
			this.url = new URL(this.urlString);
			this.fileUpdater.setup(config);
		}

	}

	/**
	 * Perform an update loop, based on the given config.
	 * 
	 * @throws IOException
	 *             should the {@link FileUpdater} fail
	 */
	public void update() throws IOException {
		if (config == null) {
			LOGGER.warn("No config provided");
			return;
		}

		Map<CellWrapper, String> updatedCells = updateCache(getLatestState());
		if (!updatedCells.isEmpty()) {
			fileUpdater.updateFiles(updatedCells);
		}
	}

	/**
	 * Makes a request outwards to our prepared Google Sheets API url.
	 *
	 * @return {@link GoogleSheetsResponse} from our request to the API
	 * @throws IOException
	 *             should the connection or input stream fail
	 */
	private GoogleSheetsResponse getLatestState() throws IOException {
		URLConnection request = this.url.openConnection();
		request.connect();

		return new GsonBuilder().create().fromJson(new InputStreamReader((InputStream) request.getContent()),
				GoogleSheetsResponse.class);
	}

	private Map<CellWrapper, String> updateCache(GoogleSheetsResponse data) {
		Map<CellWrapper, String> fullValueMap = data.getMutatedRowColumnData();

		// Update the cache
		return this.cache.update(fullValueMap);
	}
}
