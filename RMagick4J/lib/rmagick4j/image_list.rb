
module Magick
  
  class ImageList < Array

    def flatten_images
      img = @images.map { |obj| obj._image }
      
      image = img.inject do |memo, obj|
        memo.flatten(obj)
      end
      
      Image.from_image(image)
    end
    
    def to_blob(&add)
      # TODO Support lists.
      first.to_blob(&add)
    end
  end
end
