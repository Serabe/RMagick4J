package magick4j;

import java.util.*;

public class ImageList {

	private List<MagickImage> images;

	public ImageList(MagickImage... images) {
		this.images = new ArrayList<MagickImage>(Arrays.asList(images));
	}

	public List<MagickImage> getImages() {
		return images;
	}

}
