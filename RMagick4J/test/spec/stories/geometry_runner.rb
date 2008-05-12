require "rubygems"
require 'spec/story'

require File.join(File.dirname(__FILE__), '..', '..', '..','lib','RMagick')

include Magick

require File.join(File.dirname(__FILE__), 'geometry_steps.rb')


with_steps_for(:geometry) do
  run File.join(File.dirname(__FILE__), 'geometry_story.rb')
end