#!/usr/bin/ruby

##  #!/usr/bin/env ruby

require 'rubygems'
require 'commander'
require 'mysql2'
require 'set'
require 'sequel'
require 'dm-core'
require 'dm-constraints'
require 'dm-migrations'
require 'time'
require 'active_support/time'

DataMapper::Logger.new($stdout, :debug)
DataMapper.setup(:default, 'mysql://alpha:alpha@alphatrading.dnsalias.com/alpha')

class ZDat_Trade
  # set the storage name for the :legacy repository
  include DataMapper::Resource

  storage_names[:default] = "zdat_trades"

  property :id, Serial
  property :symbol, String
  property :ts, Integer
  property :dt, String
  property :price, Float
  property :size, Integer
end

class MyApplication

  include Commander::Methods

  SYMBOL = "MSFT"
  def run

    program :version, '0.0.1'
    program :description, 'Verifies data in ZDAT_TRADES.'

    default_command :verify_zdat_prices

    command :verify_zdat_prices do |c|
      c.syntax = '<app> [options]'
      c.summary = 'Verifies ZDAT_PRICES of SYMBOL'
      c.description = c.summary # Because we're lazy
      c.example 'Verifies GWPH', '<app> -s GWPH '
      c.option '-s', '--symbol SYMBOL', String, 'Specify a symbol' # Option aliasing
      c.option '-d', '--date DATE', String, 'Specify a date' # Option aliasing
      c.action do |args, options|
        #name = args.shift # or args.first or args[0] if you wish
        puts "Verifying #{options.symbol}!"
        # Or in a real app, you would call a method and pass command line arguments to it.
        
        run_query options.symbol, options.date
        test_time
        
      end
    end

    command :goodbye do |c|
      c.syntax = 'my-app goodbye NAME [options]'
      c.summary = 'Says goodbye'
      c.description = c.summary
      c.example 'Say goodbye to world', 'my-app goodbye -n world'
      c.example 'Say goodbye to world', 'my-app goodbye --name world'
      c.option '-n', '--name NAME', String, 'Specify a name' # Option aliasing
      c.action do |args, options|
        puts "Hello #{options.name}!"
      end
    end

    run!
  end

  #
  #   Read all ZDAT_TRADES for a certain day
  #   Calculate OPEN and CLOSE from  opening_time and closing_time
  #   Calculate CHG
  #   Calculate HIGH and LOW
  #   Calculate intraday Volatility
  #   Calculate <other interesting stuff> ?
  #   Verify data (no sudden jumps). Remove data before and after trading?
  #
  #

  def run_query (s, d)
    
    puts ENV['TZ']
    
    zt = ZDat_Trade.all(:symbol => 'AAPL', :dt => '2014-10-28')
    puts zt.size
    
    # this one doesnt work
    open_time  = Time.parse("2014-10-28 15.30.00.000").to_i
    close_time = Time.parse("2014-10-28 22.00.00.000").to_i
    
    #this one does
    open_time2  = DateTime.new(2014,10,28,14,30,0).to_time.to_i;
    close_time2 = DateTime.new(2014,10,28,22,0,0).to_time.to_i
    
    puts "Open:" + open_time.to_s + " Close:" + close_time.to_s
    puts "Open:" + open_time2.to_s + " Close:" + close_time2.to_s
    
    dt_open = Time.at(open_time2).to_datetime
    dt_close = Time.at(close_time2).to_datetime
    
    puts dt_open.to_s
    puts dt_close.to_s
    
    #Sleep 15
    
    zt.each do |z|
      ts = z.ts / 1000
      puts z.price.to_s + " " +
           z.size.to_s + " " +
           Time.at(ts).in_time_zone("EST").to_s + 
            " (" + Time.at(ts).in_time_zone("Europe/Stockholm").to_s + ")"
    end
    
  end
  
  
  def test_time
    now = Time.now
    puts "EST      :" + Time.at(now).to_datetime.in_time_zone("EST").to_s
    puts "New York :" + Time.at(now).to_datetime.in_time_zone("America/New_York").to_s
    puts "London   :" + Time.at(now).to_datetime.in_time_zone("Europe/London").to_s
    puts "Stockholm:" + Time.at(now).to_datetime.in_time_zone("Europe/Stockholm").to_s
    puts "Helsinki :" + Time.at(now).to_datetime.in_time_zone("Europe/Helsinki").to_s
  end
  
  
end

puts "Instantiate"
MyApplication.new.run if $0 == __FILE__
