package urm.Controllers;

import javafx.fxml.FXML;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

public class Controller {

    @FXML
    CodeArea codeArea;

    @FXML
    public void initialize(){

        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
    }

}
