require File.join(File.dirname(__FILE__),'..','..','lib','RMagick')

include Magick

describe Draw do
  
  it "should save the state when calling push and recover it after pop" do
    a = Draw.new
    a.line(5, 5, 10, 10)
    b = a.clone
    b.push
    b.stroke('blue')
    a.inspect.should != b.inspect
    b.pop
    a.inspect.should == b.inspect
  end
  
  it "should add one line per primitive" do
    a = Draw.new
    a.path 'M150,150
            l50,50'
    a.inspect.split('\n').size.should == 1
  end
end