/**
 * BaseGui.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020.
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
package application.guis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import application.AppUtil;
import application.IApplicationOps;
import application.panels.BasePanel;
import application.panels.IPanel;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Control;
import javafx.scene.layout.Pane;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * Base class for all Guis.
 *
 * @author Mark "Grandy" Bishop
 */
public abstract class BaseGui<P extends BasePanel<G>, G extends BasePanel.Gui, L extends Pane> extends Pane
		implements BasePanel.Gui {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger(BaseGui.class);
	private static final long DISABLE_CONTROL_TIME = 1000L;

	@Getter(AccessLevel.PROTECTED)
	private P panel;
	@Getter(AccessLevel.PROTECTED)
	private IApplicationOps app;
	@Getter(AccessLevel.PROTECTED)
	private L root;

	@SuppressWarnings("unchecked")
	public BaseGui(IApplicationOps app, IPanel<G> panel, L root) {
		this.app = app;
		panel.setGui((G) this);
		this.panel = (P) panel;
		this.root = root;
		super.getChildren().add(this.root);
	}

	/** Sets up the Gui/Panel, to be called at end of child constructor. */
	@Override
	public void init() {
		setUp();
		doLayout();
	}

	/** Setup any listeners/actions on components. */
	protected void setUp() {
		// Do nothing by default
	}

	/** Populate the layout. */
	protected abstract void doLayout();

	@Override
	public void showErrorDialog(String header, String message) {
		// Remove all instances of the user's API key
		String sanitisedMessage = AppUtil.sanitiseApiKey(header);
		String errorMessage = AppUtil.sanitiseApiKey(message);
		/*
		 * Exceptions can be thrown within our {@link UpdateRunnable} thread and beyond,
		 * which is separate to the JavaFX application thread; Platform.runLater allows
		 * the code to be ran on the proper thread.
		 */
		Platform.runLater(() -> {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("SheetsIO error");
			alert.setHeaderText(sanitisedMessage);
			alert.setContentText(errorMessage);
			alert.showAndWait();
		});
	}

	/**
	 * Disable the given {@link Control} for {@link #DISABLE_CONTROL_TIME} millis.
	 */
	protected void disableThenReenable(Control ctrl) {
		new Thread(() -> {
			try {
				ctrl.setDisable(true);
				Thread.sleep(DISABLE_CONTROL_TIME);
			} catch (InterruptedException e) {
				getPanel().handleException(e);
			} finally {
				ctrl.setDisable(false);
			}
		}).start();
	}

	protected ObservableList<Node> getLayout() {
		return root.getChildren();
	}
}
