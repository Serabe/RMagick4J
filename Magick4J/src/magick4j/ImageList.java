package magick4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageList {

    private List<MagickImage> images;

    public ImageList(MagickImage... images) {
        this.images = new ArrayList<MagickImage>(Arrays.asList(images));
    }

    public List<MagickImage> getImages() {
        return images;
    }
}
