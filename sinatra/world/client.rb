# -------------------------------------------------------
#
#
#
# -------------------------------------------------------

require 'json' 
require 'rest-client' 
require 'csv'
require_relative 'worldclient'

#id","ident","type","name","latitude_deg","longitude_deg","elevation_ft","continent","is#o_country","iso_region","municipality","scheduled_service","gps_code","iata_code","loca#l_code","home_link","wikipedia_link","keywords"
#airports = CSV.read('airports.csv')

class Hash
  def method_missing(method, *args)
    if method =~ /=$/
      # Protip: $` means "the string before the last Regex match"
      self[$`.to_sym] = args[0]
    else
      self[method.to_sym]
    end
  end
end

#CSV.foreach('airports.csv') do |row|
#  puts row.inspect
#end




# --------------------------------------
#
# --------------------------------------
  
admin = WorldClient.new 'admin'

users = Hash.new

puts "updating 100 users with random positions..."
100.times.collect do |t|
  users[t] = WorldClient.new "user-"+t.to_s
  
  lat = 1 + (Random.rand(10000) / 100000.0)
  lng = 2 + (Random.rand(10000) / 100000.0)

  users[t].update_position(lat, lng)
end


puts "getting position for 100 users..."
100.times.collect do |t|
  puts users[t].get_position
end

puts "Getting all users!  "
puts "get_users => " + admin.get_users

#puts "get_position =>" + kalle.get_position
#kalle.lat = 7.787878
#kalle.lng = 9.898989
#puts "update_position ==> " + kalle.update_position
#puts "get_position ==> " + kalle.get_position

#10.times.map{ 20 + Random.rand(11) } 

