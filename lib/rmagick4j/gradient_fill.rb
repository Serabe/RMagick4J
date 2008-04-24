
module Magick
  class GradientFill

    def fill(image)
      @fill.fill(image._image)
    end

    def initialize(x1, y1, x2, y2, start_color, end_color)
      @fill = Magick4J.GradientFill.new(x1, y1, x2, y2, Magick4J.ColorDatabase.queryDefault(start_color), Magick4J.ColorDatabase.queryDefault(end_color))
    end

  end
end
