package application;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			primaryStage.setTitle("Compression & Decompression");
			Huffman h = new Huffman();

			StackPane root = new StackPane();

			// Using a BorderPane as the content pane
			BorderPane contentPane = new BorderPane();
			contentPane.setStyle("-fx-background-color: transparent;");

			HBox hboxHuff = new HBox(500);

			HBox hboxButton = new HBox(90);
			hboxButton.setTranslateX(550);
			hboxButton.setTranslateY(160);

			VBox vboxLeft = new VBox(60);

			Button compress = new Button("Compress a file");
			compress.setStyle("-fx-text-fill: Black");
			compress.setPrefSize(180, 50);
			compress.setFont(new Font(18));

			Button decompress = new Button("Decompress a file");
			decompress.setStyle("-fx-text-fill: RED");
			decompress.setPrefSize(180, 50);
			decompress.setFont(new Font(18));

			hboxButton.getChildren().addAll(compress, decompress);

			TextArea ta = new TextArea();
			ta.setTranslateX(410);
			ta.setTranslateY(150);
			ta.setPrefHeight(400);
			ta.setPrefWidth(700);
			ta.setFont(new Font(15));
			ta.setEditable(false);

			compress.setOnAction(e -> {
				FileChooser chose = new FileChooser();
				File file = chose.showOpenDialog(primaryStage);
				if (file != null) {
					try {
						int[] sizes = h.compress(file);
						ta.appendText("Compression is done");
						Button Statistic = new Button("Statistic");
						Statistic.setOnAction(p->{
							ta.clear();
							//ta.appendText("Number of Distinct Characters: " + Huffman.huffCodeArraySize + "\n");
							ta.appendText("Original File Size: " + sizes[0] + " bytes\n");
							ta.appendText("Compressed File Size: " + sizes[1] + " bytes\n");
							int comr =  (sizes[1] * 100) / sizes[0];
							int com=100-comr;
							ta.appendText("Compression Ratio: " + com+ "%");
						});
						Statistic.setStyle("-fx-text-fill: Black");
						Statistic.setPrefSize(180, 50);
						Statistic.setFont(new Font(18));
						Button Huffman1  = new Button("Huffman");
						Huffman1.setOnAction(p->{
							ta.clear();
							ta.appendText( "\n\nASCII\tCharacter\t\tFrequency\tHuffCode\n");
							for (int k = 0; k < Huffman.huffCodeArray.length; k++) {
								if ((int) Huffman.huffCodeArray[k].character == 10
										|| (int) Huffman.huffCodeArray[k].character == 9)
									continue;
								ta.appendText(String.valueOf((int) Huffman.huffCodeArray[k].character) + "\t\t  "
										+ Huffman.huffCodeArray[k].character + "\t\t\t"
										+ String.valueOf(Huffman.huffCodeArray[k].counter) + "\t\t\t"
										+ Huffman.huffCodeArray[k].huffCode + "\n");
							}
						});
						Huffman1.setStyle("-fx-text-fill: Black");
						Huffman1.setPrefSize(180, 50);
						Huffman1.setFont(new Font(18));
						Button Header = new Button("Header");
						Header .setOnAction(p->{
							ta.clear();
							// Display sizes in the TextArea
							ta.appendText("Header Information:\n");
							ta.appendText("Original File Name: " + file.getName() + "\n");
							// show Huffman table
							ta.appendText("File path: " + file.getPath() + "\nCompressed file path: " + Huffman.outFileName+"\n");
							String header =Huffman.heder;
							int maxLineLength = 60; // Set the maximum number of characters per line

							// Split the header into chunks of 60 characters and append each to the TextArea
							for (int i = 0; i < header.length(); i += maxLineLength) {
							    int end = Math.min(i + maxLineLength, header.length());
							    ta.appendText(header.substring(i, end) + "\n");
							}

						});
						Header.setStyle("-fx-text-fill: Black");
						Header.setPrefSize(180, 50);
						Header.setFont(new Font(18));
						HBox hb =new HBox(20,Statistic ,Huffman1,Header );
						hb.setAlignment(Pos.CENTER);
						contentPane.setBottom(hb);





					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else {
					// Show an alert if no file is selected
					Alert alert = new Alert(Alert.AlertType.WARNING);
					alert.setContentText("No file selected for decompression.");
					alert.showAndWait();
				}

			});

			decompress.setOnAction(e -> {
				FileChooser fc = new FileChooser();
				File file = fc.showOpenDialog(primaryStage);

				if (file != null) {
					int[] sizes = h.deCompress(file); // Assuming ta is your TextArea

					Alert alert = new Alert(Alert.AlertType.INFORMATION);
					alert.setContentText("Decompression is done");
					alert.showAndWait();

					// Display sizes in the TextArea
					//ta.appendText("Original File Size: " + sizes[0] + " bytes\n");
					ta.clear();
					ta.appendText("Decompression is done");
				} else {
					// Show an alert if no file is selected
					Alert alert = new Alert(Alert.AlertType.WARNING);
					alert.setContentText("No file selected for decompression.");
					alert.showAndWait();
				}
			});


			// ------------------------------

			vboxLeft.getChildren().addAll(hboxButton,ta);
			contentPane.setLeft(vboxLeft);
			//contentPane.setBottom(hboxHuff);

			root.getChildren().addAll( contentPane);

			Scene scene = new Scene(root, 700, 600);
			primaryStage.setScene(scene);
			primaryStage.setMaximized(true);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
