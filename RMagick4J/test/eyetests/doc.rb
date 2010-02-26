require 'erb'
require 'ftools'

if ARGV.size == 0
  puts 'usage: ruby doc.rb directory'
  exit
end

File.makedirs ARGV[0] unless File.exists?(ARGV[0]) && File.directory?(ARGV[0])

IMAGE_DIR = File.join(ARGV[0], 'img')

File.makedirs IMAGE_DIR unless File.exists?(IMAGE_DIR) && File.directory?(IMAGE_DIR)

TESTS= Dir[File.join(File.dirname(__FILE__),'tests','*.rb')].reject{|x| x=~ /new_image.rb$/ }

IMAGES = []

i = 0
size = TESTS.size

TESTS.each do |test|
  i += 1
  puts "Running #{test} in JRuby. #{i}/#{size}"
  `jruby #{test} #{IMAGE_DIR}`
  puts "Running #{test} in Ruby. #{i}/#{size}"
  `sh -c 'ruby #{test} #{IMAGE_DIR}'`
  IMAGES << test.split(/\//).last[0...-3]
end

template = %{
  <html>

    <head>
      <title>RMagick4J Vs. RMagick</title>
    </head>

    <body>
      These examples are supposed to show:<br/>
      <ul>
        <li>There are some bugs in RMagick4J<li>
        <li>There are still a lot of features left in RMagick4J (I will continue working on this project after GSoC)</li>
        <li>There are some bugs in ImageMagick and, therefore, in RMagick</li>
      </ul>
      <table>
        <% i = 0 %>
        <% IMAGES.sort.each do |img| %>
        <% if i % 4 == 0 %>
        <tr> 
          <td >RMagick4J</td>
          <td>RMagick</td>
        </tr>
        <% end %>
        <tr>
          <td> 
            <img src="<%= File.join('img', img) %>.jruby.jpg"/>
          </td>
          <td>
            <img src="<%= File.join('img', img) %>.mri.jpg"/>
          </td>
        </tr>
        <% i += 1%>
        <% end %>
      </table>
    </body>

  </html>
}.gsub(/^  /, '')

puts 'Running template'
rhtml = ERB.new template

doc = rhtml.result binding

html = File.new(File.join(ARGV[0], 'index.html'), 'w')

html.write doc

html.close
