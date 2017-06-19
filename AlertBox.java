/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mp3player;

import com.jfoenix.controls.*;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author removevirus
 */
public class AlertBox {
   // Stage window;
   // Button btn;4
    static boolean answer;
    public static boolean display(String title, String message){
        GridPane root=new GridPane();
        
        Stage window=new Stage();
        window.initStyle(StageStyle.TRANSPARENT);
        Scene scene=new Scene(root,300,100);
        window.setScene(scene);
        window.setTitle(title);
        
        ColumnConstraints col1=new ColumnConstraints();
        ColumnConstraints col2=new ColumnConstraints();
        ColumnConstraints col3=new ColumnConstraints();
              col1.setPercentWidth(50);
              col2.setPercentWidth(50);
              col3.setPercentWidth(50);
         root.getColumnConstraints().addAll(col1,col2,col3);
         
         RowConstraints row1=new RowConstraints();
         RowConstraints row2=new RowConstraints();
              row1.setPercentHeight(50);
              row2.setPercentHeight(50);
          root.getRowConstraints().addAll(row1,row2);
              
        Label lbl=new Label(message);
        lbl.setWrapText(true);
        lbl.setTextAlignment(TextAlignment.CENTER);
        lbl.setAlignment(Pos.CENTER);
        lbl.setFont(Font.font("Dialog",FontPosture.ITALIC,15));
        GridPane.setHalignment(lbl, HPos.CENTER);
        root.add(lbl, 0, 0,3,1);
        
        JFXButton btn=new JFXButton("Return To Player");
        GridPane.setHalignment(btn, HPos.CENTER);
        root.add(btn,0,1,3,1);
        
        window.show();
        btn.setOnAction(event -> {event.consume();
        answer=true;
            window.close();
         });
       
        return answer;
       
    }    
}
