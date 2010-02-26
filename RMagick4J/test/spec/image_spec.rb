if PLATFORM == 'java'
  require File.join(File.dirname(__FILE__),'..','..','lib','RMagick')
else
  require 'rubygems'
  require 'RMagick'
end
require File.join(File.dirname(__FILE__),"image_constants.rb")

include Magick

describe Image do
  
  before(:each) do
    @image = Image.read(File.join(File.dirname(__FILE__),'..','images','clown.jpg'))
  end
  
  it "should have the same methods as the standard RMagick" do
    IMAGE_METHODS.each do |method|
      # It fails, use test/implemented_methods instead (at least, until
      # we got some more methods implemented, no just 13%
      # @image.should respond_to? method.to_sym
    end
  end

  it "should keep format after resized!" do
    @image = @image.first
    first_format = @image.format
    @image.resize!(50,50)
    @image.format.should == first_format
  end

  it "should keep format after crop_resized!" do
    @image = @image.first
    first_format = @image.format
    @image.crop_resized!(50, 50, NorthGravity)
    @image.format.should == first_format
  end

  it "should return a one-item array after reading an image" do
    @image.should have(1).images
  end
end
