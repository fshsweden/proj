#!/usr/bin/ruby

require 'mysql2'


client = Mysql2::Client.new(:host => "localhost", :username => "alpha", :password => "alpha", :database => "alpha")
params = client.query("SELECT id,symbol FROM channel_param_sets ")

missing = Hash.new

params.each do |pa|
  # puts "Examining param set " + pa["id"].to_s
	channels = client.query("SELECT id FROM channels where param_id = " + pa["id"].to_s)
  #	puts "Found " + channels.size.to_s + " channels"

	channels.each do |c|
		pnls = client.query("SELECT id FROM channels_pnl where channel_id = " + c["id"].to_s)
		if pnls.size == 0 
			missing[pa["symbol"]] = "MISSING" # using HASH since I don't know how to use sets....... ;-O
		end
	end
end

missing.each do |k,v|
	puts k + " is missing PNL's"
end

# puts("For param-set " + pa["id"].to_s + " " + pa["symbol"].to_s + " we have no PNL's")
