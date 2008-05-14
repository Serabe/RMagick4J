
module Magick
  Pixel = Magick4J::PixelPacket
  
  class Pixel
    def initialize(red, green, blue, opacity=0)
      super(red, green, blue, opacity)
    end
    
    def self.from_color
       #...
    end
  end
end

module Magick
  class Pixel
    include Comparable
    include Observable
    attr_accessor :pixel
    
    def initialize(red, green, blue, opacity=0)
      # TODO Add support to CMYKColorspace.
      @pixel = Magick4J::PixelPacket.new(red, green, blue, opacity)
    end
    
    #TODO: Find a good name for pixel_aux
    def Pixel.from_color(color_name)
      pixel_aux = Magick4J::ColorDatabase.lookup(color_name)
      raise ArgumentError, "invalid color name: #{color_name}" if pixel.aux.nil?
      result = Pixel.new(0,0,0,0)
      result.pixel = pixel_aux
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
    
    def blue
      @pixel.blue.round.to_i
    end
    alias_method :yellow, :blue
    
    def blue=(value)
      raise TypeError, 'can\'t convert String into Integer' if value.to_i.to_s != value
      @pixel.blue=value
    end
    alias_method :yellow=, :blue=
    
    # TODO: Add ColorSpace parameter.
    # Extracted from color.c:1698
    def fcmp(pixel_aux, fuzz=0.0)
      fuzz = 3.0*([fuzz, 0.7071067811865475244008443621048490].max**2)
      
      # TODO: How does matte affect this algorithm?
      
      distance = (red-pixel_aux.red)**2
      return false if distance > fuzz
      distance += (green-pixel_aux.green)**2
      return false if distance > fuzz
      distance += (blue-pixel_aux.blue)**2
      return false if distance > fuzz
      true #The colors are similar!!!
    end
    
    def green
      pixel.green.round.to_i
    end
    alias_method :magenta, :green
    
    def green=(value)
      raise TypeError, 'can\'t convert String into Integer' if value.to_i.to_s != value
      pixel.green=value
    end
    alias_method :magenta=, :green=
    
    # Thanks, FSM, for the RMagick documentation.
    def intensity
      0.299*red+0.587*green+0.114*blue
    end
    
    def opacity
      pixel.opacity
    end
    alias_method :black, :opacity
    
    def opacity=(value)
      raise TypeError, 'can\'t convert String into Integer' if value.to_i.to_s != value
      pixel.opacity=value
    end
    alias_method :black=, :opacity=
    
    def red
      pixel.red.round.to_i
    end
    alias_method :cyan, :red
    
    def red=(value)
      raise TypeError, 'can\'t convert String into Integer' if value.to_i.to_s != value
      pixel.red=value
    end
    alias_method :cyan=, :red=
  end
end
