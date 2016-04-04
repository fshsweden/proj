
require 'ib'

ib = IB::Connection.new :host => '192.168.0.198', :port => 6661
puts 'Connected!'

ib.subscribe(:Alert){ |msg|
   puts "ALERT:" + msg.to_human
}

#
#
#
@estx505C = IB::Contract.new(:symbol => "ESTX50",:expiry => "20150320", :exchange => "DTB",:currency => "EUR",:sec_type => :future,:description => "ESTX505C")
@contracts = {
    1 => @estx505C
}

# Subscribe to HistoricalData incoming events.  The code passed in the block
# will be executed when a message of that type is received, with the received
# message as its argument. In this case, we just print out the data.
#
# Note that we have to look the ticker id of each incoming message
# up in local memory to figure out what it's for.

ib.subscribe(IB::Messages::Incoming::HistoricalData) do |msg|
  #puts "HIST REQ ID:" + msg.request_id.to_s
  puts @contracts[msg.request_id].description + ": #{msg.count} items:"
  msg.results.each { |entry| 
#    puts "  #{entry[:time]},#{entry[:open]},#{entry[:high]},#{entry[:low]},#{entry[:close]},#{entry[:volume]},#{entry[:wap]},#{entry[:has_gaps]},#{entry[:trades]}"
   puts entry.inspect
  }
end

# Now we actually request historical data for the symbols we're interested in. TWS will
# respond with a HistoricalData message, which will be processed by the code above.

@contracts.each_pair do |key,contract|

  puts "Requesting data for :" + contract.to_s

  silence_warnings do
  ib.send_message IB::Messages::Outgoing::RequestHistoricalData.new(
                      :request_id => key,
                      :contract => contract,
                      :end_date_time => Time.now.to_ib,
                      :duration => '5 Y', #    ?
                      :bar_size => '1 day', #  IB::BAR_SIZES.key(:hour)?
                      :what_to_show => :trades,
                      :use_rth => 1,
                      :format_date => 1)

  end
end

sleep 10

