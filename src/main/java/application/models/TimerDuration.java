/**
 * CountdownDuration.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020.
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
package application.models;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.Getter;

/**
 * Data holder for timer values.
 *
 * @author Mark "Grandy" Bishop
 */
public class TimerDuration {
	private static final Logger LOGGER = LogManager.getLogger(TimerDuration.class);

	private boolean hasHours;
	private int totalSeconds;

	@Getter
	private int hours;
	@Getter
	private int minutes;
	@Getter
	private int seconds;

	/* Set the starting state. */
	public void setTimeAndFormat(int hours, int minutes, int seconds) {
		LOGGER.debug("Setting up start time");
		update(hours, minutes, seconds);
		this.hasHours = hours > 0;
	}

	/** Update state based on totalSeconds, an internal update. */
	public void update(int totalSeconds) {
		this.totalSeconds = totalSeconds;

		this.hours = totalSeconds / 3600;
		this.minutes = (totalSeconds % 3600) / 60;
		this.seconds = totalSeconds % 60;

		LOGGER.debug("{} set to {}:{}:{} - total {}", TimerDuration.class.getSimpleName(), hours, minutes, seconds,
				totalSeconds);
	}

	/** Update state based on individual hours/minutes/seconds from UI. */
	public void update(int hours, int minutes, int seconds) {
		this.totalSeconds = (hours * 3600) + (minutes * 60) + seconds;

		this.hours = hours;
		this.minutes = minutes;
		this.seconds = seconds;

		LOGGER.debug("{} set to {}:{}:{} - total {}", TimerDuration.class.getSimpleName(), hours, minutes, seconds,
				totalSeconds);
	}

	/** Perform an update tick downwards. */
	public boolean decrease() {
		if (!(this.totalSeconds == 0)) {
			this.update(this.totalSeconds - 1);
			return true;
		}
		return false;
	}

	/** @return the String display of the current timer duration. */
	public String getDisplay() {
		if (hasHours) {
			return String.format("%02d:%02d:%02d", hours, minutes, seconds);
		}
		return String.format("%02d:%02d", minutes, seconds);
	}

}
