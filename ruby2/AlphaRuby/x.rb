require 'ib'

    ib = IB::Connection.new :port => 7496
    ib.subscribe(:Alert, :AccountValue) { |msg| puts msg.to_human }
    ib.send_message :RequestAccountData
    ib.wait_for :AccountDownloadEnd

    ib.subscribe(:OpenOrder) { |msg| puts "Placed: #{msg.order}!" }
    ib.subscribe(:ExecutionData) { |msg| puts "Filled: #{msg.execution}!" }
    contract = IB::Contract.new :symbol => 'WFC', :exchange => 'NYSE',:currency => 'USD', :sec_type => :stock
