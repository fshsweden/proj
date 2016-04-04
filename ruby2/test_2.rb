#!/usr/bin/ruby

require 'mysql2'
require 'set'

client = Mysql2::Client.new(:host => "localhost", :username => "alpha", :password => "alpha", :database => "alpha")

params = client.query("SELECT id,symbol FROM channel_param_sets ")

missing = Set.new
existing = Set.new

#
# for each param set
#
params.each do |pa|
  current_param_id = pa["id"].to_s

  # Lookup summary
  summaries = client.query("SELECT id from channels_pnl_summary WHERE param_id = " + current_param_id)
  if summaries.size == 0
    #puts current_param_id + "(" + pa["symbol"] + ") has no SUMMARY"

    channels = client.query("SELECT count(*) from channels where param_id = " + current_param_id)
    if channels.size > 0
      missing.add pa["symbol"] + "-" + pa["id"].to_s
    else
    end
  else
    #puts current_param_id + "(" + pa["symbol"] + ") has a SUMMARY -------------------------------> GOOD!"
    existing.add pa["symbol"] + "-" + pa["id"].to_s
  end
end

puts "We have " + existing.size.to_s + " existing and " + missing.size.to_s + " missing!"

#missing.each do |m|
#  puts m + " has no SUMMARY"
#end
