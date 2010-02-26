module Magick
  Effects = Java::Magick4jEffects
  Magick4J = Java::magick4j
  RMagick4J = Java::rmagick4j


  QuantumDepth = 8
  QuantumRange = 2**QuantumDepth-1
  QuantumScale = 1.0/QuantumRange.to_f
  
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
