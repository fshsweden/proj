# -------------------------------------------------------
# The main module and its REST interface
# 
#
#
# -------------------------------------------------------

#
# The approximate conversions are:
#
# Latitude: 1 deg = 110.574 km
#
# Longitude: 1 deg = 111.320*cos(latitude) km
#
require 'sinatra' 
require 'json' 
require_relative 'world'

require 'sinatra/activerecord'

ActiveRecord::Base.establish_connection(
  adapter: 'sqlite3',
  database:  'db.sqlite3'
)

#
# Create the world!
#
world = World.new
#world.test

###### Sinatra Part ###### 

set :port, 8080 
set :environment, :production 

#
# /get_position?name=kalle
# returns JSON {lat=0.101010101, lng=0.202020202}
#
get '/get_position' do 
  return_message = {} 
  if params.has_key?('name')
    name = params['name'] 
    player = world.get_player(name)
    if player == nil
      player = world.add_player(name)
    end
    return_message = player.get_position
  end 
  return_message.to_json 
end 

get '/test' do
  stream do |out|
    out << "It's gonna be legen -\n"
    sleep 0.5
    out << " (wait for it) \n"
    sleep 1
    out << "- dary!\n"
  end
end





#
# /update_position
# POST params: {data=>{lat=>0.0101010, lng=0.2020202}}
# returns JSON {"success"} or {"fail"}
#
post "/update_position" do
  puts "PARAMS:" + params.inspect
  request.body.rewind  # in case someone already read it
  #data = JSON.parse request.body.read
  #puts "DATA:" + data.inspect

  name = params["data"]["name"]
  puts "Trying to update " + name
  
  player = world.get_player(name)
  
  if (player != nil)
    lat = params["data"]["lat"]
    lng = params["data"]["lng"]
    player.set_position(lat, lng)
  end

  ret = {retcode: "success"}
  return ret.to_json
end

#
# get all usernames that are within X*X km 
#
# /getuserswithin user, km
get '/get_users_within' do
  return_message = {} 
  if params.has_key?('name')
    name = params['name'] 
    position = world.get_position name
    players = world.get_players_within(position, 0.01)
    players["retcode"] = "Success"
    return players.to_json 
  else
    return {"retcode"=>"fail"}.to_json
  end 
end

#
# get all user 
#
# /getusers
get '/get_users' do
  return_message = world.get_players 
  return_message.to_json 
end

post "/updpos1" do
  puts "PARAMS:" + params.inspect
  request.body.rewind  # in case someone already read it
  data = JSON.parse request.body.read
  puts "DATA:" + data.inspect
  world.update_position data["name"], data["lat"], data["lng"]

  return "success".to_json
end

post "/hej" do
  return_message = {} 
  jdata = JSON.parse(params[:data],:symbolize_names => true) 
  if jdata.has_key?(:name) && jdata.has_key?(:lat) && jdata.has_key?(:lng) 
    world.update_pos jdata[:name], jdata[:lat], jdat[:lng]
    return_message[:status] = 'done' 
  else 
    return_message[:status] = 'missing argument name, lat or lng' 
  end 
  return_message.to_json 
end 
