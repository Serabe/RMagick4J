module Magick
  Effects = Java::Magick4jEffects
  Magick4J = Java::magick4j
  RMagick4J = Java::rmagick4j


  QuantumDepth = 8
  QuantumRange = 2**QuantumDepth-1
  QuantumScale = 1.0/QuantumRange.to_f
  
  # Geometries
  JGeometry        = Magick4J::Geometry

  JRelativeHeightDistance = Magick4J::RelativeHeightDistance
  JRelativeWidthDistance = Magick4J::RelativeWidthDistance
  
  JHeightDistances = [
                      Magick4J::SimpleValueHeightDistance,
                      Magick4J::PercentValueHeightDistance,
                      Magick4J::AspectValueHeightDistance,
                      Magick4J::LessValueHeightDistance,
                      Magick4J::GreaterValueHeightDistance,
                      Magick4J::AreaValueHeightDistance
                     ]

  JWidthDistances = [
                      Magick4J::SimpleValueWidthDistance,
                      Magick4J::PercentValueWidthDistance,
                      Magick4J::AspectValueWidthDistance,
                      Magick4J::LessValueWidthDistance,
                      Magick4J::GreaterValueWidthDistance,
                      Magick4J::AreaValueWidthDistance
                     ]
end
