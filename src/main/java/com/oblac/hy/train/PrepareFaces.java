package com.oblac.hy.train;

import com.oblac.hy.FaceDetector;
import com.oblac.hy.User;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.List;
import java.util.function.Consumer;

/**
 * Extract faces from raw photos and saves them for each user.
 */
public class PrepareFaces {

	private FaceDetector faceDetector;

	public void detectAllFacesFromRawPhotos() {
		faceDetector = new FaceDetector();
		List<User> users = User.allUsers();

		users.forEach(user -> {
			System.out.println("user: " + user.name());

			user.listRawFiles().forEach(rawPhotoFile -> {

				System.out.print("\t" + rawPhotoFile.getName());

				Mat rawFaceImage = Imgcodecs.imread(rawPhotoFile.getAbsolutePath(), Imgcodecs.CV_LOAD_IMAGE_COLOR);

				extractFace(rawFaceImage, detectedFace -> {
					System.out.print("\t -> face detected");

					String destinationImageFile = user.newFaceFile(rawPhotoFile.getName()).getAbsolutePath();

					Imgcodecs.imwrite(destinationImageFile, detectedFace);
				});

				System.out.println("");
			});
		});
	}

	private void extractFace(Mat faceImage, Consumer<Mat> detectedFaceConsumer) {
		List<Rect> faces = faceDetector.detectFace(faceImage);

		if (faces.size() == 1) {
			Rect faceRect = faces.get(0);

			Rect cropFace = new Rect(faceRect.x, faceRect.y, faceRect.width, faceRect.height);

			detectedFaceConsumer.accept(new Mat(faceImage, cropFace));
		}
	}
}
