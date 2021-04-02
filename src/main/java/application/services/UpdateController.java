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
package application.services;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import application.AppUtil;
import application.exceptions.GoogleSheetsException;
import application.exceptions.IllegalFileExtensionException;
import application.models.CellUpdate;
import application.models.CellWrapper;
import application.models.ConfigHolder;
import application.models.PropertiesHolder;
import application.models.json.GoogleSheetsResponse;
import application.services.http.HttpService;
import application.threads.UpdateRunnable;
import lombok.RequiredArgsConstructor;

/**
 * Makes the calls to update the sheet. May be used within threads, e.g.
 * {@link UpdateRunnable}.
 *
 * @author Mark "Grandy" Bishop
 */
@RequiredArgsConstructor
public class UpdateController {
	private static final Logger LOGGER = LogManager.getLogger(UpdateController.class);

	private final SheetCache cache = new SheetCache();
	private final FileUpdater fileUpdater = new FileUpdater(new FileIO());

	private String urlString;
	private URL url;

	/**
	 * Set a new config, thus needing to reset state and start anew.
	 * 
	 * @throws IllegalFileExtensionException
	 */
	public synchronized void setConfig(boolean fromScratch) throws IOException, IllegalFileExtensionException {

		this.urlString = String.format(AppUtil.SPREADSHEET_URL_FORMAT, //
				ConfigHolder.get().getSpreadsheetId(), //
				AppUtil.encodeUrlContent(ConfigHolder.get().getWorksheetName()),
				PropertiesHolder.get().getProperty(PropertiesHolder.API_KEY));

		LOGGER.debug("URL: {}", AppUtil.get().sanitiseApiKey(this.urlString));

		if (fromScratch) {
			this.cache.setup(ConfigHolder.get().getCells());
			this.url = new URL(this.urlString);
			this.fileUpdater.setup();
		}

		HttpService.getInstance().start(this.cache);

	}

	/**
	 * Perform an update loop, based on the given config.
	 * 
	 * @throws IOException
	 *             should the {@link FileUpdater} fail
	 */
	public void update() throws Exception {
		if (!ConfigHolder.get().isLoaded()) {
			LOGGER.error("No config provided");
			return;
		}

		List<CellUpdate> updatedCells = updateCache(getLatestState());
		if (updatedCells.isEmpty()) {
			LOGGER.debug("Not performing file update(s) - no values to update.");
			return;
		}

		// Update applicable files
		LOGGER.debug("Performing file update(s)");
		fileUpdater.updateFiles(updatedCells.stream() //
				.filter(cu -> cu.getCellWrapper().getFileExtension().isForFile()) //
				.collect(Collectors.toList()));
	}

	/**
	 * Makes a request outwards to our prepared Google Sheets API url.
	 *
	 * @return {@link GoogleSheetsResponse} from our request to the API
	 * @throws IOException
	 *             should the connection or input stream fail
	 * @throws GoogleSheetsException
	 */
	private GoogleSheetsResponse getLatestState() throws IOException, GoogleSheetsException {
		return AppUtil.get().getGoogleSheetsData(this.urlString);
	}

	private List<CellUpdate> updateCache(GoogleSheetsResponse data) {
		Map<CellWrapper, String> fullValueMap = data.getMutatedRowColumnData();

		// Update the cache
		return this.cache.update(fullValueMap);
	}
}
