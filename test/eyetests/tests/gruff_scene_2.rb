require 'rubygems'
require 'gruff'
require File.join(File.dirname(__FILE__), 'new_image.rb')


g = Gruff::Scene.new("500x100", File.join(File.dirname(__FILE__),'..','..','gruff_tests/assets/city_scene') )
g.layers = %w(background haze sky clouds)
g.weather_group = %w(clouds)
g.time_group = %w(background sky)

g.weather = "stormy"
g.haze = false
g.time = Time.mktime(2006, 7, 4, 8, 00)
g.write "gruff_scene_2.jpg"