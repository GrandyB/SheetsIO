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
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import application.data.FileUpdateRepository;
import application.models.CellUpdate;
import application.models.CellWrapper;
import application.models.ConfigurationFile;
import application.models.FileExtension;
import application.models.FileExtension.FileExtensionType;
import application.utils.AppUtil;

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
	@Autowired
	private FileAcquisitionService fileAcquisitionService;
	@Autowired
	private FileUpdateRepository fileUpdateRepository;

	@Autowired
	private ConfigurationFile configurationFile;
	private Timer timer = new Timer();

	@PostConstruct
	public void start() {
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (getTransientProperties().isAutoUpdate()) {
					try {
						update();
					} catch (Exception e) {
						getExceptionHandler().handle(e);
					}
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
		updateFiles(updatedCells.stream() //
				.filter(cu -> cu.getCellWrapper().getFileExtension().isForFile()) //
				.collect(Collectors.toList()));
	}

	/** Update all the files from the Map with their new values. */
	private void updateFiles(List<CellUpdate> updatedCells) throws Exception {
		for (CellUpdate entry : updatedCells) {
			if (!entry.getCellWrapper().getFileExtension().isForFile()) {
				continue;
			}
			CellWrapper cellWrapper = entry.getCellWrapper();
			String newValue = entry.getNewValue();

			/**
			 * {@link CellUpdate}s are made up of {@link CellWrapper}s created from changes
			 * in the Google Sheet - "A1 now has value X". The {@link SheetCache} will only
			 * ever store one wrapper to a value (uses a Map to store it), but our config
			 * could theoretically have multiple pieces of cell config all wanting to be
			 * updated when the value changes. Here, we look up these multiple pieces.
			 */
			List<CellWrapper> allWrappersForCell = configurationFile.getCells().stream()
					.filter(cw -> cw.equals(cellWrapper)) //
					.collect(Collectors.toList());
			for (CellWrapper w : allWrappersForCell) {
				updateFile(w, newValue);
			}
		}
	}

	private void updateFile(CellWrapper cellWrapper, String newValue) throws Exception {
		String destFilePath = fileUpdateRepository.createFilePath(configurationFile.getProjectName(), cellWrapper);
		FileExtension ext = cellWrapper.getFileExtension();

		if (FileExtension.TXT.equals(ext)) {
			fileUpdateRepository.writeTextFile(destFilePath, cellWrapper.getPadding().insert(0, newValue).toString());
		}

		InputStream file = acquireFileInputStream(newValue);
		switch (ext.getType()) {
		case TEXT:
			/**
			 * Add padding if applicable (@see {@link CellWrapper#getPadding}), and replace
			 * GSheet errors with blank values.
			 */
			break;
		case IMAGE:
			try {
				fileUpdateRepository.writeImage(file, newValue, destFilePath);
			} catch (Exception e) {
				fileUpdateRepository.saveTransparentImage(destFilePath, ext.getType().name());
			}
			break;
		case VIDEO:
			try {
				fileUpdateRepository.writeVideo(file, newValue, destFilePath);
			} catch (Exception e) {
				// TODO: Throw an actual exception
			}
			break;
		default:
			throw new IllegalStateException(
					"Unable to handle " + FileExtensionType.class.getSimpleName() + ": " + ext.getType());
		}
	}

	/**
	 * @return {@link InputStream} of a file, acquired from a remote or local source
	 *         depending on the URL.
	 */
	private InputStream acquireFileInputStream(String url) throws Exception {
		return isRemote(url) ? fileAcquisitionService.downloadRemoteFile(url)
				: fileAcquisitionService.downloadRemoteFile(url);
	}

	private boolean isRemote(String url) throws Exception {
		URI uri = AppUtil.encodeForUrl(url);
		return uri.getScheme().equals("file");
	}
}
