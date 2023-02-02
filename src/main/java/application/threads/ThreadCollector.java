/**
 * ThreadCollector.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020.
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
package application.threads;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import application.services.old.http.HttpService;

/**
 * Central place to keep track of all threads running in the app; to be shut
 * down and cleansed upon exiting the application.
 *
 * @author Mark "Grandy" Bishop
 */
public final class ThreadCollector {

	private static UpdateRunnable updateLoop;
	private static List<IntervalRunnable> runnables = new ArrayList<>();

	private static HttpService httpServiceInstance;

	public static void setHttpService(HttpService httpService) {
		if (httpServiceInstance == null) {
			httpServiceInstance = httpService;
		} else {
			throw new IllegalArgumentException("Unexpected new HttpService");
		}
	}

	public static void stopAllThreads() {
		updateLoop.doStop();
		runnables.forEach(l -> l.doStop());
		if (httpServiceInstance != null) {
			httpServiceInstance.stop();
		}
	}

	public static <L extends IntervalRunnable> L registerRunnable(L loop) {
		runnables.add(loop);
		return loop;
	}

	public static UpdateRunnable registerUpdateLoop(UpdateRunnable loop) {
		ThreadCollector.updateLoop = loop;
		return loop;
	}

	public static Optional<UpdateRunnable> getUpdateLoop() {
		return Optional.of(updateLoop);
	}
}
