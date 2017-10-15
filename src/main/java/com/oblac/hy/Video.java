package com.oblac.hy;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.opencv.core.Core;

import java.net.URL;

/**
 * The main class for a JavaFX application. It creates and handle the main
 * window with its resources (style, graphics, etc.).
 * <p>
 * This application handles a video stream and can convert its frames in gray
 * scale or color. Moreover, for each frame, it shows the corresponding
 * histogram and it is possible to add a logo in a corner of the video.
 *
 * @author <a href="mailto:luigi.derussis@polito.it">Luigi De Russis</a>
 * @version 2.0 (2017-03-10)
 * @since 1.0 (2013-11-20)
 */
public class Video extends Application {

	@Override
	public void start(Stage primaryStage) {
		try {

			// load the FXML resource
			URL resource = getClass().getResource("Video.fxml");

			FXMLLoader loader = new FXMLLoader(resource);

			// store the root element so that the controllers can use it
			BorderPane rootElement = loader.load();

			// create and style a scene
			Scene scene = new Scene(rootElement, 800, 600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			// create the stage with the given title and the previously created scene
			primaryStage.setTitle("HEY YOU!");
			primaryStage.setScene(scene);

			// show the GUI
			primaryStage.show();

			// set the proper behavior on closing the application
			VideoController controller = loader.getController();
			primaryStage.setOnCloseRequest((we -> controller.setClosed()));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// load the native OpenCV library
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		launch(args);
	}
}