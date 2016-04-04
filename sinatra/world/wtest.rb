require_relative "world"
require_relative "worldclient"
require "json"

w = World.new

users = {}

puts "updating 100 users with random positions..."
100.times.collect do |t|
  users[t] = Player.new "user-"+t.to_s
  
  lat = 1 + (Random.rand(10000) / 100000.0)
  lng = 2 + (Random.rand(10000) / 100000.0)

  users[t].set_position(lat, lng)
end

puts "getting position for 100 users..."
100.times.collect do |t|
  puts users[t].name + " " + users[t].get_position.to_s
end

pl = w.get_player "user-1"
pos = pl.get_position
puts w.get_players_within pos, 0.0001

