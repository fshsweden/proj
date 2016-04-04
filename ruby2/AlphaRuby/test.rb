require 'rubygems'
require 'active_record'

ActiveRecord::Base.establish_connection(
  :adapter  => 'mysql2',
  :database => 'alpha',
  :username => 'alpha',
  :password => 'alpha',
  :host     => 'alphatrading.dnsalias.com')

class Product < ActiveRecord::Base

end

Product.all.each do |p|
  puts p.symbol
end



