# app.rb
require "sinatra/activerecord"

set :database, {adapter: "sqlite3", database: "foo.sqlite3"}
# or set :database_file, "path/to/database.yml"

class YourApplication < Sinatra::Base
  register Sinatra::ActiveRecordExtension
end
