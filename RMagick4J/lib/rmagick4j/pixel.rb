
module Magick
  class Pixel
    include Comparable
    include Observable
    
    def initialize(red, green, blue, opacity)
      # TODO Add support to CMYKColorspace.
      @_pixel = Magick4J::PixelPacket.new(red, green, blue, opacity)
    end
    
    def _pixel
      @_pixel
    end
    
    def _pixel=(value)
      @_pixel = value if value.is_a? Magick4J::PixelPacket
    end
    
    def Pixel.from_color(color_name)
      pixel = Magick4J::ColorDatabase.lookup(color_name)
      raise ArgumentError, "invalid color name: #{color_name}" if pixel.nil?
      result = Pixel.new(0,0,0,0)
      result._pixel = pixel
      result
    end
    
    protected :_pixel=
  end
end
