# Ruby Uno Client 

require 'json' 
require 'rest-client' 

class WorldClient 
  attr_writer :name, :lat, :lng 

  def initialize name 
    @name = name 
  end 

  def update_position 
    response = RestClient.post 'http://localhost:8080/updpos', 
      :data => {name: @name, lat: @lat, lng: @lng}, 
      :accept => :json 
    puts response 
  end 

  def update_position1 
    response = RestClient.post 'http://localhost:8080/updpos', 
      :data => {name: @name, lat: @lat, lng: @lng}.to_json, 
      :accept => :json 
    puts JSON.parse(response,:symbolize_names => true) 
  end 

  def get_position 
    response = RestClient.get 'http://localhost:8080/getpos', 
      {:params => {:name => @name}} 
    puts response 
  end 

end

kalle = WorldClient.new 'kalle'
kalle.get_position

kalle.lat = 7.787878
kalle.lng = 9.898989

kalle.update_position

kalle.get_position
