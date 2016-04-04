#!/usr/bin/ruby

##  #!/usr/bin/env ruby
 
require 'rubygems'
require 'commander'
require 'mysql2'
require 'set'

class MyApplication

  include Commander::Methods

  SYMBOL = "MSFT"

  def run
   
    program :version, '0.0.1'
    program :description, 'Does oh so many things.'
 
    default_command :get_price_at

    command :get_price_at do |c|
      c.syntax = '<app> [options]'
      c.summary = 'Gets the price of SYMBOL at a certain TIME'
      c.description = c.summary # Because we're lazy
      c.example 'Gets price of GWPH at 2015-06-23', '<app> -s GWPH -d 2015-06-23 '
      c.option '-s', '--symbol SYMBOL', String, 'Specify a symbol' # Option aliasing
      c.action do |args, options|
        #name = args.shift # or args.first or args[0] if you wish
        puts options.inspect
        puts "Hello #{SYMBOL}!"
        # Or in a real app, you would call a method and pass command line arguments to it.
        run_query
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

  def run_query
      puts "RUNNING!"
      client = Mysql2::Client.new(:host => "alphatrading.dnsalias.com", :username => "alpha", :password => "alpha", :database => "alpha")

      symbol = SYMBOL
      dt = "2014-10-16"
      tm = "17:00:00"

      qry = "select " +
      "  symbol, TIME(FROM_UNIXTIME(ts / 1000)), dt, price,size  from zdat_trades " +
      "where " +
      "  symbol = '#{symbol}' " +
      "AND " +
      "  dt = '#{dt}'  " + 
      "AND " +
      "  TIME(FROM_UNIXTIME(ts / 1000)) >= '#{tm}' " +
      "LIMIT 1,1 "
  
      params = client.query(qry)
      params.each do |x|
        puts x["symbol"] + " " + x["price"].to_s
      end

    end
end


puts "Instantiate"
MyApplication.new.run if $0 == __FILE__
