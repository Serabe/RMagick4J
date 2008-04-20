package test;

import magick4j.CompositeOperator;
import magick4j.Gravity;
import magick4j.MagickImage;

public class ImageTest {
    private void cropToANewSize() {
        MagickImage clown = openClown();
        MagickImage face = clown.crop(50, 15, 150, 165);
        MagickImage whiteBg = new MagickImage(clown.getWidth(), clown.getHeight());
        clown = whiteBg.composited(face, 50, 15, CompositeOperator.OVER);
        face.display();
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
        MagickImage whiteBg = new MagickImage(clown.getWidth(), clown.getHeight());
        clown = whiteBg.composited(tiny, Gravity.CENTER, CompositeOperator.OVER);
        clown.display();
    }

    private MagickImage openClown() {
        return new MagickImage(getClass().getResource("clown.jpg"));
    }

    public void run() {
        switch (1) {
            case 1:
                cropToANewSize();
                break;
            case 2:
                makeAThumbnail();
                break;
            case 3:
                flipIt();
                break;
        }
    }
}
