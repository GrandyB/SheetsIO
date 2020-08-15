/**
 * MainView.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020.
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

import application.IApplicationOps;
import application.panels.MainPanel;
import javafx.scene.layout.VBox;

/**
 * GUI for main bulk of the app, comprised of a {@link ConfigGui} and a
 * {@link TimerGui}.
 *
 * @author Mark "Grandy" Bishop
 */
public class MainGui extends BaseGui<MainPanel, MainPanel.Gui, VBox> implements MainPanel.Gui {

	private VBox mainLayout = new VBox();

	public MainGui(IApplicationOps app) {
		super(app, new MainPanel(), new VBox(3));
		getPanel().initialise();
	}

	@Override
	protected void doLayout() {
		getRoot().getStyleClass().add("root");
		getRoot().setSpacing(10);

		getLayout().add(new ApiKeyGui(this.getApp()));

		ConfigGui configGui = new ConfigGui(getApp());
		mainLayout.getChildren().add(configGui);

		TimerGui timer = new TimerGui(getApp());
		mainLayout.getChildren().add(timer);
		getLayout().add(mainLayout);
	}

	@Override
	public void enableMainLayout(boolean enable) {
		mainLayout.setDisable(!enable);
	}
}
