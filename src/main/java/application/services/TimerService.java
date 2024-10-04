/**
 * TimerService.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2023.
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

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import application.Main;
import application.data.FileUpdateRepository;
import application.events.TimerUpdateEvent;
import application.models.TimerDuration;
import lombok.Getter;
import lombok.Setter;

/**
 * Service responsible for access and maintenance of timer update/output.
 *
 * @author Mark "Grandy" Bishop
 */
@Service
public class TimerService extends AbstractService {
	private static final Logger LOGGER = LogManager.getLogger(TimerService.class);

	@Autowired
	private FileUpdateRepository fileUpdateRepository;

	private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	private TimerDuration time = new TimerDuration();

	@Getter
	@Setter
	private boolean running = false;

	@PostConstruct
	public void setUp() {
		executor.scheduleWithFixedDelay(() -> this.updateLoop(), 0, 1, TimeUnit.SECONDS);
	}

	@PreDestroy
	public void tearDown() {
		executor.shutdownNow();
	}

	/** @return a String representation of the timer's current value. */
	public String getDisplay() {
		return time.getDisplay();
	}

	private void updateLoop() {
		try {
			if (running) {
				updateOnce();
			}
		} catch (Exception e) {
			LOGGER.error("Error during TimerService#update", e);
		}
	}

	/** Perform an update regardless of looping state. */
	public void updateOnce() {
		LOGGER.debug("Perform timer update: " + time.getDisplay());
		time.decrease();
		try {
			fileUpdateRepository.writeTextFile(Main.FOLDER_PREFIX + File.separator + "timer.txt", time.getDisplay());
		} catch (IOException e) {
			LOGGER.error("Unable to update the timer file", e);
		}

		if (time.getTotalSeconds() == 0) {
			// Forcibly stop it once it ends
			this.running = false;
		}
		getEventBus().post(new TimerUpdateEvent(time, this.running));
	}

	/** Reset the timer back to 0. */
	public void reset() {
		this.time.setTimeAndFormat(0, 0, 0);
		updateOnce();
	}

	/** Set the time. */
	public void setTimeAndFormat(int hours, int minutes, int seconds) {
		this.time.setTimeAndFormat(hours, minutes, seconds);
	}
}
