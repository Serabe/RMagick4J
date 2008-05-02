
module Magick
  class TextureFill
    
    def fill(image)
      @fill.fill(image._image)
    end
    
    def initialize(image)
      image = image.cur_image if image.respond_to? :cur_image
      @fill = Magick4J.TextureFill.new(image._image)
    end
  end
end
