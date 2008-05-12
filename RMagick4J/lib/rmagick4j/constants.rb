module Magick
  Magick4J = Java::magick4j
  RMagick4J = Java::rmagick4j
  
  # Geometries
  JGeometry        = Magick4J::Geometry
  
  JGeometries = [
                  JGeometry,
                  Magick4J::PercentGeometry,
                  Magick4J::AspectGeometry,
                  Magick4J::LessGeometry,
                  Magick4J::GreaterGeometry,
                  Magick4J::AreaGeometry
                ]
end
