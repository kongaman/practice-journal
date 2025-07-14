package com.ck.practicejournal.view;

import java.util.Map;

import com.ck.practicejournal.model.FocusArea;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class FocusAreaCellFactory implements Callback<ListView<FocusArea>, ListCell<FocusArea>> {

	private final Map<FocusArea, BooleanProperty> selectionMap;

	public FocusAreaCellFactory(Map<FocusArea, BooleanProperty> selectionMap) {
		this.selectionMap = selectionMap;
	}

	@Override
	public ListCell<FocusArea> call(ListView<FocusArea> lv) {
		return new ListCell<>() {
			private final CheckBox checkBox = new CheckBox();
			private BooleanProperty currentSelectedProperty;

			@Override
			protected void updateItem(FocusArea item, boolean empty) {
				super.updateItem(item, empty);

				if (currentSelectedProperty != null) {
					checkBox.selectedProperty().unbindBidirectional(currentSelectedProperty);
					currentSelectedProperty = null;
				}

				if (empty || item == null) {
					setGraphic(null);
					setText(null);
				} else {
					currentSelectedProperty = selectionMap.get(item);
					if (currentSelectedProperty != null) {
						checkBox.selectedProperty().bindBidirectional(currentSelectedProperty);
					}
					checkBox.setText(item.getName());
					setGraphic(checkBox);
				}
			}
		};
	}
}
