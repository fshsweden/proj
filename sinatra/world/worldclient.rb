require 'rest-client'

class WorldClient 
  attr_writer :name, :lat, :lng 

  def initialize name 
    @name = name 
  end 

  def update_position(lat, lng)
    @lat = lat
    @lng = lng
    response = RestClient.post 'http://localhost:8080/update_position', 
      :data => {name: @name, lat: @lat, lng: @lng}, 
      :accept => :json 
    #puts response 
  end 

  #def update_position1 
  #  response = RestClient.post 'http://localhost:8080/updpos', 
  #    :data => {name: @name, lat: @lat, lng: @lng}.to_json, 
  #    :accept => :json 
  #  #puts JSON.parse(response,:symbolize_names => true) 
  #end 

  def get_position 
    response = RestClient.get 'http://localhost:8080/get_position', 
      {:params => {:name => @name}} 
    #puts response 
  end 

  def get_users
    response = RestClient.get 'http://localhost:8080/get_users' 
    #puts response  # a JSON string
  end

end

