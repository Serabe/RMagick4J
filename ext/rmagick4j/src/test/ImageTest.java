package test;

import magick4j.*;

public class ImageTest {

	private void cropToANewSize() {
		MagickImage clown = openClown();
		MagickImage face = clown.cropped(50, 15, 150, 165);
		MagickImage whiteBg =
				new MagickImage(clown.getWidth(), clown.getHeight());
		clown = whiteBg.composited(face, 50, 15, CompositeOperator.OVER);
		clown.display();
	}

	private void flipIt() {
		MagickImage clown = openClown();
		clown.flip();
		clown.display();
	}

	private void makeAThumbnail() {
		MagickImage clown = openClown();
		MagickImage tiny = clown.resized(0.25);
		MagickImage whiteBg =
				new MagickImage(clown.getWidth(), clown.getHeight());
		clown =
				whiteBg
						.composited(tiny, Gravity.CENTER,
								CompositeOperator.OVER);
		clown.display();
	}

	private MagickImage openClown() {
		MagickImage clown =
				new MagickImage(getClass().getResource("clown.jpg"));
		return clown;
	}

	private void rotateToAnyAngle() {
		MagickImage clown = openClown();
		clown = clown.rotated(-130);
		clown.display();
	}

	public void run() {
		switch (4) {
			case 1:
				cropToANewSize();
				break;
			case 2:
				makeAThumbnail();
				break;
			case 3:
				rotateToAnyAngle();
				break;
			case 4:
				flipIt();
				break;
		}
	}

}
