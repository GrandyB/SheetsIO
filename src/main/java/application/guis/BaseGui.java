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

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import application.panels.BasePanel;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * Base class for all Guis.
 *
 * @author Mark "Grandy" Bishop
 */
@Component
public abstract class BaseGui<P extends BasePanel<G>, G extends BasePanel.Gui, L extends Pane> extends Pane
		implements BasePanel.Gui {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger(BaseGui.class);
	private static final long DISABLE_CONTROL_TIME = 1000L;

	@Autowired
	@Getter
	private ApplicationContext appContext;

	@Getter(AccessLevel.PROTECTED)
	private P panel;
	@Getter(AccessLevel.PROTECTED)
	private L root;

	@SuppressWarnings("unchecked")
	@Autowired
	public BaseGui(P panel) {
		this.panel = panel;
		this.panel.setGui((G) this);
		this.root = (L) new VBox();
		super.getChildren().add(this.root);
	}

	/** Sets up the Gui/Panel, to be called at end of child constructor. */
	@Override
	@PostConstruct
	public void init() {
		getPanel().initialise();
		setUp();
		doLayout();
	}

	/** Setup any listeners/actions on components. */
	protected void setUp() {
		// Do nothing by default
	}

	/** Populate the layout. */
	protected abstract void doLayout();

	/**
	 * Disable the given {@link Control} for {@link #DISABLE_CONTROL_TIME}
	 * millis.
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
