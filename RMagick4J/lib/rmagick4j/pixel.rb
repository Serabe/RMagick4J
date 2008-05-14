
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
    
    # Add accessors to RGBA values.
    [:red, :green, :blue, :opacity].each do |channel|
      define_method(channel)do
        @_pixel.send(channel).round.to_i
      end
      
      define_method(channel.to_s+'=')do |value|
        raise TypeError, 'can\'t convert String into Integer' if value.to_i.to_s != value
        @_pixel.send(channel.to_s+'=', value.to_i)
      end
    end
    
    def Pixel.from_color(color_name)
      pixel = Magick4J::ColorDatabase.lookup(color_name)
      raise ArgumentError, "invalid color name: #{color_name}" if pixel.nil?
      result = Pixel.new(0,0,0,0)
      result._pixel = pixel
      result
    end
    
    def Pixel.from_HSL(array)
      raise ArgumentError, 'array argument must have at least 3 elements' if array.size != 3
      color = Color.getHSBColor(array[0].to_f, array[1].to_f, array[2].to_f)
      Pixel.new(color.red, color.green, color.blue, color.opacity)
    end
    
    def <=>(pixel2)
      # Alphabetical algorithm.
      if red == pixel2.red
        if green == pixel2.green
          if blue == pixel2.blue
            opacity <=> pixel2.opacity
          else
            blue <=> pixe2.blue
          end
        else
          green <=> pixel2.green
        end
      else
        red <=> pixel2.red
      end
    end
    
    # TODO: Add ColorSpace parameter.
    # Extracted from color.c:1698
    def fcmp(pixel, fuzz=0.0)
      fuzz = 3.0*([fuzz, 0.7071067811865475244008443621048490].max**2)
      
      # TODO: How does matte affect this algorithm?
      
      distance = (red-pixel.red)**2
      return false if distance > fuzz
      distance += (green-pixel.green)**2
      return false if distance > fuzz
      distance += (blue-pixel.blue)**2
      return false if distance > fuzz
      true #The colors are similar!!!
    end
    
    # Thanks, FSM, for the RMagick documentation.
    def intensity
      0.299*red+0.587*green+0.114*blue
    end
    
    protected :_pixel=
  end
end
