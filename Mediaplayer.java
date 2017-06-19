/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mp3player;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import java.io.File;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
/**
 *
 * @author removevirus
 */
public class Mediaplayer extends Application {

private MediaPlayer mediaPlayer;
private Point2D anchorPt;
private Point2D previousLocation;
private ChangeListener<Duration> progressListener;
private static Stage PRIMARY_STAGE;
private static final String STOP_BUTTON_ID = "stop-button";
private static final String PLAY_BUTTON_ID = "play-button";
private static final String PAUSE_BUTTON_ID = "pause-button";
private static final String CLOSE_BUTTON_ID = "close-button";
private static final String VIS_CONTAINER_ID = "viz-container";
private static final String SEEK_POS_SLIDER_ID = "seek-position-slider";
private static final String INFO_ID="info-label";
private static Text infoLabel;

    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
   public void start(Stage primaryStage) throws MalformedURLException {
    PRIMARY_STAGE = primaryStage;
    PRIMARY_STAGE.initStyle(StageStyle.DECORATED);
    PRIMARY_STAGE.centerOnScreen();
    Group root = new Group();
    Scene scene = new Scene(root, 600, 270, Color.rgb(0, 0, 0, 0));

    // load JavaFX CSS style
    scene.getStylesheets().add(getClass().getResource("playing-audio.css").toExternalForm());
    PRIMARY_STAGE.setScene(scene);
    
    // Initialize stage to be movable via mouse
    initMovablePlayer();

    // application area
    Node applicationArea = createApplicationArea();
    
    // Container for random circles bouncing about
    Node vizContainer = new Group();
    vizContainer.setId(VIS_CONTAINER_ID);
    
   // Create the button panel
    Node buttonPanel = createButtonPanel();
   
    // Progress and seek position slider
    Slider progressSlider = createSlider();
    
    // Update slider as video is progressing
    progressListener = (observable, oldValue, newValue) ->
    progressSlider.setValue(newValue.toSeconds());
    // Initializing to accept files
    // dragged over surface to load media
    initFileDragNDrop();
    // Create the close button
    Node fileChooserButton = createFileChooserButton();
 
    infoLabel=createTrackInfoText();
    
    Slider volumeSlider=createVolumeSlider();
    
    ImageView imageView=(ImageView)createOnlineResourceBtn();
    PRIMARY_STAGE.getIcons().add(new Image(getClass().getResourceAsStream("play.png")));
    root.getChildren().addAll(applicationArea,vizContainer,buttonPanel,progressSlider,fileChooserButton,
            infoLabel,volumeSlider,imageView);
       primaryStage.show();
    }
   
   private Text createTrackInfoText(){
    Text text=new Text("");
    text.setId(INFO_ID);
    text.setFont(Font.font("Dialog", FontPosture.ITALIC, 15));
    return text;
   }

private void initMovablePlayer() {
    Scene scene = PRIMARY_STAGE.getScene();
    // starting initial anchor point
    scene.setOnMousePressed(mouseEvent-> anchorPt = new Point2D(
            mouseEvent.getScreenX(),
            mouseEvent.getScreenY())
    );
       // dragging the entire stage
     scene.setOnMouseDragged(mouseEvent -> {
        if (anchorPt != null && previousLocation != null) {
            PRIMARY_STAGE.setX(previousLocation.getX()+ mouseEvent.getScreenX()- anchorPt.getX());
            PRIMARY_STAGE.setY(previousLocation.getY()+ mouseEvent.getScreenY()- anchorPt.getY());
   }
   });
    // set the current location
    scene.setOnMouseReleased(mouseEvent-> previousLocation = new Point2D(
            PRIMARY_STAGE.getX(),
            PRIMARY_STAGE.getY())
    );
    // Initialize previousLocation after Stage is shown
    PRIMARY_STAGE.addEventHandler(WindowEvent.WINDOW_SHOWN,
        (WindowEvent t) -> {
        previousLocation = new Point2D(PRIMARY_STAGE.getX(),
        PRIMARY_STAGE.getY());
    });
    }

    private Node createApplicationArea() {
       Scene scene = PRIMARY_STAGE.getScene();
          Rectangle applicationArea = new Rectangle();
       
      // add selector to style app-area
       applicationArea.setId("app-area");

    // make the app area rectangle the size of the scene.
       applicationArea.widthProperty().bind(scene.widthProperty());
       applicationArea.heightProperty().bind(scene.heightProperty());
       return applicationArea;
    }
/**
* Initialize the Drag and Drop ability for media files.
*
*/
    private void initFileDragNDrop() {
        Scene scene = PRIMARY_STAGE.getScene();
        scene.setOnDragOver(dragEvent -> {
          Dragboard db = dragEvent.getDragboard();
          if (db.hasFiles() || db.hasUrl()) {
             dragEvent.acceptTransferModes(TransferMode.LINK);
           } else {
                 dragEvent.consume();
           }
      });
    
     // Dropping over surface
      scene.setOnDragDropped(dragEvent -> {
          Dragboard db = dragEvent.getDragboard();
          boolean success = false;
          String filePath = null;
      if (db.hasFiles()) {
          success = true;
      if (db.getFiles().size() > 0) {
          try {
           filePath = db.getFiles().get(0).toURI().toURL().toString();
           playMedia(filePath);
           } catch (MalformedURLException ex) {ex.printStackTrace();}
       }
         } else {
    // audio file from some host or jar
         playMedia(db.getUrl());
          success = true;
      }
       dragEvent.setDropCompleted(success);
       dragEvent.consume();
    }); // end of setOnDragDropped
    }

    private Node createButtonPanel() {
      Scene scene = PRIMARY_STAGE.getScene();
    
     // create button control panel
        Group buttonGroup = new Group();
    
    // Button area
       Rectangle buttonArea = new Rectangle(100,30);
       buttonArea.setId("button-area");
       buttonGroup.getChildren().add(buttonArea);// this will give the button group the shape of the  rectangle
     
       //repeat button
       
       
    // stop button control
        Node stopButton = new Rectangle(10, 10);
        stopButton.setId(STOP_BUTTON_ID);
        
         stopButton.setOnMousePressed(mouseEvent -> {
            if (mediaPlayer != null) {
               updatePlayAndPauseButtons(true);
                  if (mediaPlayer.getStatus() == Status.PLAYING) {
                      mediaPlayer.stop();
            }
            }
       }); // setOnMousePressed()
    
    // play button
        Arc playButton = new Arc(20, // center x
        16, // center y
        15, // radius x
        15, // radius y
        150, // start angle
        60); // length
        
        playButton.setId(PLAY_BUTTON_ID);
        playButton.setType(ArcType.ROUND);
        playButton.setOnMousePressed(mouseEvent -> mediaPlayer.play());
      
    // pause control
        Group pauseButton = new Group();
        pauseButton.setId(PAUSE_BUTTON_ID);
        Node pauseBackground = new Circle(12, 16, 20);
        pauseBackground.getStyleClass().add("pause-circle");
                
        Node firstLine = new Line(6, // start x
        6, // start y
        6, // end x
        14); // end y
        
        firstLine.getStyleClass().add("pause-line");
        firstLine.setStyle("-fx-translate-x: 65;");
    
        Node secondLine = new Line(6, // start x
        6, // start y
        6, // end x
        14); // end y
    
        secondLine.getStyleClass().add("pause-line");
        secondLine.setStyle("-fx-translate-x: 70;");
        
        pauseButton.getChildren().addAll(pauseBackground, firstLine, secondLine);
        
        pauseButton.setOnMousePressed(mouseEvent -> {
             if (mediaPlayer!=null) {
                 updatePlayAndPauseButtons(true);
                    if (mediaPlayer.getStatus() == Status.PLAYING) {
                         mediaPlayer.pause();}
         }
           }); // setOnMousePressed()
    
        playButton.setOnMousePressed(mouseEvent -> {
             if (mediaPlayer != null) {
               updatePlayAndPauseButtons(false);
              mediaPlayer.play();}
    }); // setOnMousePressed()
        
        //repeatButton
        Group repeatGroup=new Group();
        
        Ellipse repeatButton=new Ellipse(2,16,10,10);
        repeatButton.setTranslateX(15);
        repeatButton.setStroke(Color.WHITE);
        repeatButton.setId("repeat-btn");
        
        repeatButton.setOnMousePressed(mouseEvent->{
            updateRepeatButton(false);
        });
        
        Line noRepeat = new Line(6,2,6,20);
        noRepeat.setFill(Color.WHITE);
        noRepeat.setId("no-repeat-lines");
        
        noRepeat.setOnMousePressed(mouseEvent->{
            updateRepeatButton(true);
        });
        
        repeatGroup.getChildren().addAll(repeatButton,noRepeat);
        buttonGroup.getChildren().addAll(repeatGroup,stopButton,playButton,pauseButton);
    // move button group when scene is resized
        
    buttonGroup.translateXProperty().bind(scene.widthProperty().divide(2).subtract(34));
    buttonGroup.translateYProperty().bind(scene.heightProperty().subtract(buttonArea.getHeight() + 6));
    return buttonGroup;
    }

    
    private Node createFileChooserButton() {
       Scene scene = PRIMARY_STAGE.getScene();
       Group fileChooserButton = new Group();
       fileChooserButton.setId(CLOSE_BUTTON_ID);
    
        
        Node firstLine = new Line(30,0,14,0);
        Node secondLine = new Line(30,5,14,5);
        Node thirdLine = new Line(30, // start x
        10, // start y
        14, // end x
        10);
        
        firstLine.getStyleClass().add("pause-line");
        secondLine.getStyleClass().add("pause-line");
        thirdLine.getStyleClass().add("pause-line");
        
         fileChooserButton.translateXProperty().bind(scene.widthProperty().subtract(60));
          fileChooserButton.setTranslateY(10);
        fileChooserButton.getChildren().addAll(firstLine,secondLine,thirdLine);

      // exit app
      fileChooserButton.setOnMouseClicked(mouseEvent -> {try{
        initFileChooser();
    }catch(Exception e){
        //
    }});
          return fileChooserButton;
     }
    
    private void playMedia(String url) {
        Scene scene = PRIMARY_STAGE.getScene();
        
      // settng the mediaplayer parameter and properties
        if (mediaPlayer != null) {
         mediaPlayer.pause();
         mediaPlayer.setOnPaused(null);
         mediaPlayer.setOnPlaying(null);
         mediaPlayer.setOnReady(null);
         mediaPlayer.currentTimeProperty().removeListener(progressListener);
         mediaPlayer.setAudioSpectrumListener(null);
    }
        if(isValidFile(url)){
    Media media = new Media(url);
    mediaPlayer = new MediaPlayer(media);

    // as the media is playing move the slider for progress
    mediaPlayer.currentTimeProperty().addListener(progressListener);
    
    Text text=(Text)scene.lookup("#"+INFO_ID);

    mediaPlayer.setOnReady(() -> {
        updatePlayAndPauseButtons(false);
        updateRepeatButton(true);
        Slider progressSlider =(Slider) scene.lookup("#" + SEEK_POS_SLIDER_ID);
        
        
        progressSlider.setValue(0);
        progressSlider.setMax(mediaPlayer.getMedia().getDuration().toSeconds());
        mediaPlayer.play();
      
        text.setText((String)media.getMetadata().get("title"));
        
    }); // setOnReady()
  
    
// setup visualization (circle container)
    Group vizContainer =(Group) PRIMARY_STAGE.getScene().lookup("#" + VIS_CONTAINER_ID);
    mediaPlayer.setAudioSpectrumListener((double timestamp,double duration,float[] magnitudes,float[] phases)
            -> {vizContainer.getChildren().clear();
        int i = 0;
        int x = 10;
        double y = PRIMARY_STAGE.getScene().getHeight() / 2;
        Random rand = new Random(System.currentTimeMillis());
        
// Build random colored circles
        for (float phase : phases) {
            int red = rand.nextInt(255);
            int green = rand.nextInt(255);
            int blue = rand.nextInt(255);
            Circle circle = new Circle(50);
            circle.setCenterX(x + i);
            circle.setCenterY(y + (phase * 100));
            circle.setFill(Color.rgb(red, green, blue, .70));
            vizContainer.getChildren().add(circle);
            i +=scene.getWidth()/20;
        }
        }); // setAudioSpectrumListener()
        }// if extension matches
        else{
            AlertBox.display("Warning", "File Format Not Supported");
        }
    }

    private void updatePlayAndPauseButtons(boolean playVisible) {
      Scene scene = PRIMARY_STAGE.getScene();
      Node playButton = scene.lookup("#" + PLAY_BUTTON_ID);
      Node pauseButton = scene.lookup("#" + PAUSE_BUTTON_ID);
    
  // hide or show buttons
    playButton.setVisible(playVisible);
    pauseButton.setVisible(!playVisible);
    if (playVisible) {
        // show play button
    playButton.toFront();
    pauseButton.toBack();
    } else { 
   // show pause button
    pauseButton.toFront();
    playButton.toBack();
    } }
    
    public void updateRepeatButton(boolean repeat){
        Scene scene= PRIMARY_STAGE.getScene();
        Node node=scene.lookup("#"+"repeat-btn");
        Node nodeX=scene.lookup("#"+"no-repeat-lines");
        
         node.setVisible(repeat);
         nodeX.setVisible(!repeat);
         
         if(repeat){
               
// back to the beginning
    mediaPlayer.setOnEndOfMedia( ()-> {
    // change buttons to play and rewind
    mediaPlayer.stop();
    mediaPlayer.play();
    
    
    node.toBack();
    nodeX.toFront();
    }); // setOnEndOfMedia()
         }else{
               
// back to the beginning
    mediaPlayer.setOnEndOfMedia( ()-> {
    // change buttons to play and rewind
    mediaPlayer.stop();
    updatePlayAndPauseButtons(true);
    node.toFront();
    nodeX.toBack();
    }); // setOnEndOfMedia()
         }
    }
    
    private Slider createSlider() {
    JFXSlider slider = new JFXSlider(0, 100, 1);
    slider.setId(SEEK_POS_SLIDER_ID);
         
   // slider.valueProperty().bindBidirectional(mediaPlayer.);
    slider.valueProperty().addListener(((ObservableValue <?extends Number>value, Number oldVal, Number newVal) -> {    
         if (slider.isValueChanging()) {
          // must check if media is paused before seeking
         if (mediaPlayer != null &&(mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED ||
                 mediaPlayer.getStatus()==MediaPlayer.Status.PLAYING)) {
                  
    // convert seconds to millis
      double dur = newVal.doubleValue()*1000;
      mediaPlayer.seek(Duration.millis(dur)); 
       }
       }
         })); //addListener()

        Scene scene = PRIMARY_STAGE.getScene();
        slider.setPrefWidth(scene.getWidth());
        slider.setTranslateX(10);
        slider.translateYProperty().bind(scene.heightProperty().subtract(70));
        scene.widthProperty().addListener(((ObservableValue <?extends Number>value, Number oldVal, Number newVal) ->{
           slider.setPrefWidth(newVal.doubleValue()-15);
        }));
        
        return slider;
        }
    
    public Slider createVolumeSlider(){
        Scene scene=PRIMARY_STAGE.getScene();
        Group sliderGroup=new Group();
        JFXSlider slider=new JFXSlider(0,100,1);
        Polygon triangle=new Polygon(20,20,30,20,30,10,20,20);
        
        slider.setPrefWidth(100);
        slider.translateYProperty().bind(scene.heightProperty().subtract(30));
        slider.translateXProperty().bind(scene.widthProperty().divide(2).add(100));
        scene.widthProperty().addListener((ObservableValue <?extends Number>value, Number oldVal, Number newVal)->{
            slider.setPrefWidth(newVal.doubleValue()/5);
        });
        
        slider.valueProperty().addListener((ObservableValue <?extends Number> ov,Number oldVal, Number newVal)->{
        mediaPlayer.setVolume(newVal.doubleValue()/100);
    });
                
        return slider;
    }
    
    public boolean isValidFile(String extension){
    List <String> mediaFiles=Arrays.asList(".mp3",".flac");
      return  mediaFiles.stream().anyMatch(val-> extension.endsWith(val));
        
}
    public  void initFileChooser() throws MalformedURLException{
        FileChooser fileChooser=new FileChooser();
        fileChooser.setTitle("Select Music File");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter(".MP3","*.mp3"),
                                                 new ExtensionFilter(".FLAC","*.flac"));
        
        File selectedFile= fileChooser.showOpenDialog(PRIMARY_STAGE);
        if(selectedFile!=null)
            playMedia(selectedFile.toURI().toURL().toExternalForm());
    }
    public void initOnlineSource() throws Exception{
        
        Stage stage=new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        GridPane grid=new GridPane();
        grid.setPadding(new Insets(5,5,5,5));
        Scene scene=new Scene(grid,300,100);
       
        ColumnConstraints col1=new ColumnConstraints();
        ColumnConstraints col2=new ColumnConstraints();
        ColumnConstraints col3=new ColumnConstraints();
                         col1.setPercentWidth(50);
                         col2.setPercentWidth(50);
                         col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1,col2,col3);
        
        RowConstraints row1=new RowConstraints();
        RowConstraints row2=new RowConstraints();
        RowConstraints row3=new RowConstraints();
                         row1.setPercentHeight(50);
                         row2.setPercentHeight(50);
                         row3.setPercentHeight(50);
         grid.getRowConstraints().addAll(row1,row2,row3);
       
         Text header=new Text("Enter Media URL");
         header.setFont(Font.font("Dialog",FontPosture.ITALIC,15));
         GridPane.setHalignment(header, HPos.CENTER);
         
         JFXTextField textField=new JFXTextField();
         textField.setPromptText("Enter URL here");
         GridPane.setHalignment(textField, HPos.LEFT);
         
         JFXButton playButton=new JFXButton("Play Music");
         GridPane.setHalignment(playButton, HPos.RIGHT);
         playButton.setOnAction(event->{ 
             String url=textField.getText();
           if(url.equals("")){
               AlertBox.display("Enter File  URL", "Enter A Valid UrL Or Click The Close Button To Exit");
           }else{   
               stage.close();
               playMedia(url);
                }
           
            event.consume();
         });
        
        JFXButton closeButton=new JFXButton("Close");
        GridPane.setHalignment(closeButton, HPos.CENTER);
        GridPane.setHalignment(closeButton, HPos.LEFT);
        closeButton.setOnAction(event->{
            stage.close();
        });
        grid.add(header,0,0,3,1);
        grid.add(textField, 0, 1,1,1);
        grid.add(closeButton, 1,2,2,1);
        grid.add(playButton, 1,1);
        
        stage.setScene(scene);
        stage.show();
    }
    public Node createOnlineResourceBtn() throws MalformedURLException{
        Scene scene=PRIMARY_STAGE.getScene();
                
        File file=new File("C:\\Users\\PC\\NetBeansWorkspace\\Miscellaneous\\src\\miscellaneous\\earth.png");
        Image image=new Image(file.toURI().toURL().toString());
        ImageView imageView=new ImageView(image);
        
        
         imageView.translateXProperty().bind(scene.widthProperty().subtract(100));
          imageView.setTranslateY(10);
          
          imageView.setOnMouseClicked(mouseEvent->{
            try {
                initOnlineSource();
            } catch (Exception ex) {
            }
          });
          return imageView;
    }
}