
module Magick
  
  class ImageList < Array

    def to_blob(&add)
      # TODO Support lists.
      first.to_blob(&add)
    end
  end
end
