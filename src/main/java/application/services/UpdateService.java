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
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import application.models.CellUpdate;
import application.models.ConfigurationFile;
import application.services.old.FileUpdater;

/**
 * Service responsible for the thread that updates the files, using data from
 * the {@link GoogleSheetsService}.
 *
 * @author Mark "Grandy" Bishop
 */
@Service
public class UpdateService extends AbstractService {
	private static final Logger LOGGER = LogManager.getLogger(UpdateService.class);

	@Autowired
	private GoogleSheetsService googleSheetsService;

	private final FileUpdater fileUpdater = new FileUpdater(new FileIOService()); // FileUpdateRepository

	@Autowired
	private ConfigurationFile configurationFile;
	private Timer timer = new Timer();

	@PostConstruct
	public void start() {
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (configurationFile.isAutoUpdate()) {
					update();
				}
			}
		}, 0, getAppProps().getUpdateInterval());
	}

	public void updateInterval(int newInterval) {
		timer.cancel();
		start();
	}

	/**
	 * Perform an update loop, based on the given config.
	 * 
	 * @throws IOException
	 *             should the {@link FileUpdater} fail
	 */
	public void update() throws Exception {
		List<CellUpdate> updatedCells = googleSheetsService.updateCache();

		// Update applicable files
		LOGGER.debug("Performing file update(s)");
		fileUpdater.updateFiles(updatedCells.stream() //
				.filter(cu -> cu.getCellWrapper().getFileExtension().isForFile()) //
				.collect(Collectors.toList()));
	}
}
