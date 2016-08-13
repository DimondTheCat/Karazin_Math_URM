package urm.Views;

import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import urm.Controllers.RegisterListCellController;
import urm.Main;
import urm.Utilities.Register;

import java.io.IOException;

/**
 * Created by Дом on 23.06.2016.
 */
public class RegisterListCell extends ListCell<Register> {


    private Pane graphics;
    public RegisterListCellController controller;

    public RegisterListCell(){

        super();

        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("FXMLs/RegisterListCell.fxml"));
            loader.setBuilderFactory(new JavaFXBuilderFactory());
            AnchorPane questionsOverview = (AnchorPane) loader.load();

            controller = loader.getController();

            graphics = questionsOverview;

            // Set person overview into the center of root layout.
            //rootLayout.setCenter(personOverview);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void updateItem(Register register, boolean empty) {
        super.updateItem(register, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(null);

            controller.register = register;
            controller.updateContent();
            setGraphic(graphics);


        }
    }

}
