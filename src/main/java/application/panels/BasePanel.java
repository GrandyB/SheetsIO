/**
 * BasePanel.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020.
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
package application.panels;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.greenrobot.eventbus.Subscribe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import application.IApplicationOps;
import application.configuration.ApplicationProperties;
import application.configuration.TransientProperties;
import application.events.ConfigReloadedEvent;
import application.services.ExceptionHandlerService;
import application.utils.Prototype;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * BasePanel.
 *
 * @author Mark "Grandy" Bishop
 */
@Prototype
@RequiredArgsConstructor
public abstract class BasePanel<G extends BasePanel.Gui> implements IPanel<G> {
	private static final Logger LOGGER = LogManager.getLogger(BasePanel.class);

	@Getter
	@Setter
	private G gui;

	@Getter
	@Setter
	@Autowired
	private IApplicationOps app;

	@Autowired
	@Getter
	private ApplicationContext applicationContext;

	@Autowired
	@Getter
	private ApplicationProperties appProps;

	@Autowired
	@Getter
	private ExceptionHandlerService exceptionHandler;

	@Autowired
	@Getter
	private TransientProperties transientProperties;

	public interface Gui {
		/** Perform initialisation of the Gui. */
		void init();
	}

	/**
	 * Perform any Gui-related initialisation. Use {@link #preInitialise()} for
	 * any non-Gui related initialisation.
	 */
	public void initialise() {
		getApp().getEventBus().register(this);
	}

	/** Peform any post-layouting actions. */
	public void postLayout() {
		// Do nothing by default
	}

	@Subscribe
	public void handleConfigReloadEvent(ConfigReloadedEvent event) {
		// Do nothing
	}

	/** Open a browser window with the given url. */
	public void openBrowser(String url) {
		getApp().openBrowser(url);
	}

	/** Handle an exception. */
	public void handleException(Exception e) {
		exceptionHandler.handle(e);
	}
}
