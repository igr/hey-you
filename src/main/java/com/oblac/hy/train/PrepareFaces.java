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
		final List<User> users = User.allUsers();

		users.forEach(user -> {
			System.out.println("user: " + user.name());

			user.listRawFiles().forEach(rawPhotoFile -> {

				System.out.print("\t" + rawPhotoFile.getName());

				final Mat rawFaceImage = Imgcodecs.imread(rawPhotoFile.getAbsolutePath(), Imgcodecs.IMREAD_COLOR);

				extractFace(rawFaceImage, detectedFace -> {
					System.out.print("\t -> face detected");

					final String destinationImageFile = user.newFaceFile(rawPhotoFile.getName()).getAbsolutePath();

					Imgcodecs.imwrite(destinationImageFile, detectedFace);
				});

				System.out.println("");
			});
		});
	}

	private void extractFace(final Mat faceImage, final Consumer<Mat> detectedFaceConsumer) {
		final List<Rect> faces = faceDetector.detectFace(faceImage);

		if (faces.size() == 1) {
			final Rect faceRect = faces.get(0);

			final Rect cropFace = new Rect(faceRect.x, faceRect.y, faceRect.width, faceRect.height);

			detectedFaceConsumer.accept(new Mat(faceImage, cropFace));
		}
	}
}
