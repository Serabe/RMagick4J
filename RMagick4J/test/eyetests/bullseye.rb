require 'java'
require 'rubygems'
require 'Singleton'

require 'tests/new_image.rb'
require 'profligacy/swing'
require 'profligacy/lel'

module Bullseye
  
  include Profligacy
  
  DefaultListModel = javax.swing.DefaultListModel
  ImageIO = javax.imageio.ImageIO
  JButton = javax.swing.JButton
  JFile = java.io.File
  JFrame = javax.swing.JFrame
  JLabel = javax.swing.JLabel
  JList = javax.swing.JList
  JPanel = javax.swing.JPanel
  JTextField = javax.swing.JTextField
  
  class ImagePanel < JPanel
    attr_accessor :image_1, :image_2
    
    def paint(graphics)
      unless image_1.nil? || image_2.nil?
        pic1 = ImageIO.read(JFile.new(image_1))
        pic2 = ImageIO.read(JFile.new(image_2))
        graphics.drawImage(pic1, 0, 0, nil)
        graphics.drawImage(pic2, pic1.width + 10, 0, nil)
      end
    end
  end
  
  class ScriptRunner
    include Singleton
    
    def running()
      @running
    end
    
    def running=(value)
      @running = value
    end
    
    def output=(value)
      @output=value if value.respond_to?(:image_1) && value.respond_to?(:image_2)
    end
    
    def output()
      @output
    end
    
    def source=(value)
      @source=value if value.respond_to?(:selected_value)
    end
    
    def source()
      @source
    end
    
    def run_script()
      running = true
      raise ArgumentError, 'source and output cannot be nil' if source.nil? || output.nil?
      selected_script = source.selected_value
      script_name = selected_script.downcase.gsub(' ','_')
      selected_script = File.join(File.expand_path(File.dirname(__FILE__)),
                                  'tests',
                                  script_name+'.rb')
      if `#{File.join(File.expand_path(File.dirname(__FILE__)), 'execute_test')} #{selected_script}` == ''
        Notifier.instance.notificate 'Done'
        output.image_1 = File.join( File.expand_path(File.dirname(__FILE__)),
                                    'images',
                                    script_name+'.jruby.jpg')
        output.repaint
        output.image_2 = File.join( File.expand_path(File.dirname(__FILE__)),
                                    'images',
                                    script_name+'.mri.jpg')
        output.repaint
      else
        Notifier.instance.notificate 'Error'
      end
      running = false
    end
  end
  
  class Notifier
    include Singleton
    
    def output=(value)
      @output=value if value.respond_to?(:text=)
    end
    
    def output()
      @output
    end
    
    def notificate(string)
      output.text = string.to_s
    end
  end
  
  class ScriptList < JList
    
   def initialize()
     @default_model = DefaultListModel.new
     super(@default_model)
     load
   end
   
   def load()
     Dir['tests/*.rb'].each do |script|
       script = script.gsub('tests/','').gsub('.rb', '').split('_').map{|x| x.capitalize}.join(' ')
       @default_model.addElement(java.lang.String.new(script.to_java_bytes)) unless script == 'New Image'
     end
   end
  end
  
  lel = '
          [ <label_scripts | <label_results       ]
          [ (610)scripts_list   | (610,300)image_panel ]
          [ >run_button    | text_field           ]
        '
  ui = Swing::LEL.new(JFrame, lel) do |c, i|
    c.label_scripts = JLabel.new('Scripts')
    c.scripts_list = ScriptList.new
    c.run_button = JButton.new('Run')
    c.label_results = JLabel.new('Results')
    c.image_panel = ImagePanel.new
    c.text_field = JTextField.new
    
    c.text_field.editable = false
    
    Notifier.instance.output = c.text_field
    ScriptRunner.instance.output= c.image_panel
    ScriptRunner.instance.source= c.scripts_list
    
    i.run_button = {:action =>  proc do|t,e|
                                  c.scripts_list.enabled = false
                                  c.run_button.enabled=false  
                                  Thread.new do
                                    Notifier.instance.notificate 'Running scripts'
                                    ScriptRunner.instance.run_script
                                    c.run_button.enabled=true
                                    c.scripts_list.enabled = true
                                  end
                                end}
  end.build :args => 'Bullseye'
 
  
  ui.default_close_operation = JFrame::EXIT_ON_CLOSE
 
  def running_script
    Notifier.instance.notificate 'Running scripts'
    
  end
end