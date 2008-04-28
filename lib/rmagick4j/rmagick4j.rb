require 'java'
# TODO See about using Raven to get gems of jhlabs and svgsalamander?
require File.join(File.dirname(__FILE__), '..', 'jhlabs-filters.jar')
require File.join(File.dirname(__FILE__), '..', 'magick4j.jar')
require File.join(File.dirname(__FILE__), '..', 'svgsalamander.jar')

#RMagick classes, keep in alphabetical order, please.
require File.join(File.dirname(__FILE__), 'constants')
require File.join(File.dirname(__FILE__), 'draw')
require File.join(File.dirname(__FILE__), 'enum')
require File.join(File.dirname(__FILE__), 'gradient_fill')
require File.join(File.dirname(__FILE__), 'image')
require File.join(File.dirname(__FILE__), 'image_list')
require File.join(File.dirname(__FILE__), 'type_metric')
