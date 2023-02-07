/**
 * GoogleSheetsService.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2023.
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
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import application.data.GoogleSheetsRepository;
import application.exceptions.GoogleSheetsException;
import application.models.CellUpdate;
import application.models.CellWrapper;
import application.models.ConfigurationFile;
import application.models.SheetCache;
import application.models.json.GoogleSheetsResponse;

/**
 * Service responsible for calls to the {@link GoogleSheetsRepository} and
 * translating it into something we can use.
 *
 * @author Mark "Grandy" Bishop
 */
public class GoogleSheetsService extends AbstractService {

	@Autowired
	private GoogleSheetsRepository googleSheetsRepository;
	@Autowired
	private ConfigurationFile configurationFile;

	@Autowired
	private SheetCache cache;

	/**
	 * Update the cache of cells from the Google Sheet.
	 * 
	 * @return List<{@link CellUpdate}> a list of changed elements that need
	 *         attention
	 * @throws IOException
	 *             when opening a connection
	 * @throws GoogleSheetsException
	 *             any custom exceptions
	 */
	public List<CellUpdate> updateCache() throws IOException, GoogleSheetsException {
		String sheetUrl = googleSheetsRepository.getGoogleRequestUrl(configurationFile.getSpreadsheetId(),
				configurationFile.getWorksheetName(), getAppProps().getApiKey());
		GoogleSheetsResponse data = googleSheetsRepository.getGoogleSheetsData(sheetUrl);
		Map<CellWrapper, String> fullValueMap = data.getMutatedRowColumnData();

		// Update the cache
		return this.cache.update(fullValueMap);
	}
}
