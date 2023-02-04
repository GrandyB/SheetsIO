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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import application.data.GoogleSheetsRepository;
import application.models.CellUpdate;
import application.models.CellWrapper;
import application.models.SheetCache;
import application.models.json.GoogleSheetsResponse;
import application.services.old.FileUpdater;
import application.utils.FileIO;
import lombok.RequiredArgsConstructor;

/**
 * Service responsible for the thread that updates the files, using data from
 * the {@link GoogleSheetsService}.
 *
 * @author Mark "Grandy" Bishop
 */
@RequiredArgsConstructor
public class UpdateService {
	private static final Logger LOGGER = LogManager.getLogger(UpdateService.class);

	@Autowired
	private GoogleSheetsRepository googleSheetsRepository;

	@Autowired
	private SheetCache cache;

	private final FileUpdater fileUpdater = new FileUpdater(new FileIO());

	/**
	 * Perform an update loop, based on the given config.
	 * 
	 * @throws IOException
	 *             should the {@link FileUpdater} fail
	 */
	public void update() throws Exception {
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

	private List<CellUpdate> updateCache(GoogleSheetsResponse data) {
		Map<CellWrapper, String> fullValueMap = data.getMutatedRowColumnData();

		// Update the cache
		return this.cache.update(fullValueMap);
	}
}
