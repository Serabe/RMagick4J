require File.join(File.dirname(__FILE__),'..','..','lib','RMagick')

include Magick

describe Pixel do
  Red     = 233
  Green   = 234
  Blue    = 235
  Opacity = 151
  Color = ['AliceBlue', Pixel.new(240, 248, 255, 0)]
  
  
  before(:each) do
    @pixel = Pixel.new(Red, Green, Blue, Opacity)
  end

  it "should return the red value" do
    @pixel.red.should eql(Red)
  end
  
  it "should return the green value" do
    @pixel.green.should eql(Green)
  end
  
  it "should return the blue value" do
    @pixel.blue.should eql(Blue)
  end
  
  it "should return the opacity value" do
    @pixel.opacity.should eql(Opacity)
  end
  
  it "should not find the value and raise and exception" do
    lambda{Pixel.from_color('mathematica')}.should raise_error(ArgumentError)
  end
  
  it "should find the right value for #{Color[0]}" do
    lambda{
      Pixel.from_color(Color[0]).should eql(Color[1])
    }.should_not raise_error(ArgumentError)
  end
  
  it "should return the color name (#{Color[0]})" do
    Color[1].to_color.should eql(Color[0])
  end
  
end

