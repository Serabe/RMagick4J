# -*- encoding: utf-8 -*-
lib = File.expand_path('../lib', __FILE__)
$LOAD_PATH.unshift(lib) unless $LOAD_PATH.include?(lib)
require 'date'
require 'rmagick4j/version'

Gem::Specification.new do |s|
  s.name = %q{rmagick4j}
  s.version = RMagick4J::Version::VERSION
  s.authors = ['Thomas Palmer', 'Sergio Arbeo', 'Thomas Enebo', 'Uwe Kubosch']
  s.date = Date.today.to_s
  s.description = File.read('README.txt').split("\n\n")[0]
  s.email = %w(serabe@gmail.com tom.enebo@gmail.com)
  s.files = %w(History.txt README.txt Rakefile LICENSE.txt) +
      Dir['lib/**/*.{rb,jar}'] + Dir['test/**/*.rb'] +
      Dir['test/**/execute_test'] + Dir['test/images/*.jpg'] +
      Dir['ext/rmagick4j/src/**/*.java']
  s.homepage = 'https://github.com/Serabe/RMagick4J'
  s.require_paths = %w(lib)
  s.rubyforge_project = %q{jruby-extras}
  s.summary = 'RMagick for Java'
  s.test_files = s.files.grep(%r{^(test|spec|features)/})
  s.executables = s.files.grep(%r{^bin/}).map { |f| File.basename(f) }
  s.specification_version = Gem::Specification::CURRENT_SPECIFICATION_VERSION
  s.add_development_dependency('rake')
  s.license = 'MIT'
end
