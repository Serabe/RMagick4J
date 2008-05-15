
module Magick
  Pixel = Magick4J::PixelPacket
  
  class Pixel
    include Comparable
    include Observable
    
    alias_method :to_java_color, :to_color
    alias_method :to_color, :to_string
    
    def initialize(red, green, blue, opacity=0)
      # TODO Add support to CMYKColorspace.
      super(red, green, blue, opacity)
    end
    
    def self.from_color(color_name)
      result = Magick4J::ColorDatabase.lookUp(color_name)
      raise ArgumentError, "invalid color name: #{color_name}" if result.nil?
      result
    end
    
    def self.from_HSL(array)
      raise ArgumentError, 'array argument must have at least 3 elements' if array.size < 3
      # It can't be 'hue, saturation, lightness = array' because array length
      # can be greater than 3.
      hue, saturation, lightness = array[0], array[1], array[2]
      r, g, b = if saturation == 0
                  3*[[[0, QuantumRange*lightness].max, QuantumRange].min]
                else
                  m2 =  if lightness <= 0.5
                          lightness*(saturation + 1.0)
                        else
                          lightness + saturation - (lightness*saturation)
                        end
                  m1 = 2.0*lightness - m2
                  
                  [
                    [[0, QuantumRange*from_hue(m1, m2, hue+1.0/3.0)].max, QuantumRange].min,
                    [[0, QuantumRange*from_hue(m1, m2, hue)].max, QuantumRange].min,
                    [[0, QuantumRange*from_hue(m1, m2, hue-1.0/3.0)].max, QuantumRange].min
                  ]
                end
      Pixel.new(r, g, b)
    end
    
    def <=>(pixel)
      # Alphabetical algorithm.
      if red != pixel.red
        red <=> pixel.red
      elsif green != pixel.green
        green <=> pixel.green
      elsif blue == pixel.blue
        blue <=> pixe2.blue
      elsif opacity != pixel.opacity
        opacity <=> pixel.opacity
      end
      self.class <=> pixel.class
    end
    
    def blue
      get_blue.round.to_i
    end
    alias_method :yellow, :blue
    alias_method :yellow=, :blue=
    
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
    
    def green
      get_green.round.to_i
    end
    alias_method :magenta, :green
    alias_method :magenta=, :green=
    
    # Thanks, FSM, for the RMagick documentation.
    def intensity
      0.299*red+0.587*green+0.114*blue
    end
    
    def opacity
      get_opacity.round.to_i
    end
    alias_method :black, :opacity
    alias_method :black=, :opacity=
    
    def red
      get_red.round.to_i
    end
    alias_method :cyan, :red
    alias_method :cyan=, :red=
    
    # Extracted from gem.c:429
    def to_HSL
      r, g, b = QuantumScale*red, QuantumScale*green, QuantumScale*blue
      max = [r, g, b].max
      min = [r, g, b].min
      lightness = (min+max)/2.0
      delta = max - min
      if(delta == 0.0)
        hue, saturation = 0.0, 0.0
      else
        saturation =  if lightness <0.5
                        delta /(min+max)
                      else
                        delta / (2.0-(min+max))
                      end
        hue = if r == max
                calculate_hue(b, g, max, delta)
              elsif g == max
                (1.0/3.0) + calculate_hue(r, b, max, delta)
              elsif b == max
                (2.0/3.0)+calculate_hue(g, r, max, delta)
              end
        if hue < 0.0
          hue += 1.0
        elsif hue > 1.0
          hue -= 1.0
        end
      end
      [hue, saturation, lightness]
    end
    
    private
    
    def calculate_hue(val1, val2, max, delta)
      (( ((max-val1)/6.0)+ (delta/2.0))- (((max-val2)/6.0)+(delta/2.0)))/delta
    end
    
    def from_hue(m1, m2, hue)
      hue += 1.0 if hue < 0.0
      hue -= 1.0 if hue > 1.0
      
      return m1+6.0*(m2-m1)*hue if (6.0*hue) < 1.0
      return m2 if (2.0*hue) < 1.0
      return m1+6.0*(m2-m1)*(2.0/3.0-hue) if (3.0*hue) < 2.0
      m1
    end
  end
end
