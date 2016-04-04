# -------------------------------------------------------
#
#
#
# -------------------------------------------------------

require_relative 'player'

class World

  def initialize ()
    puts "Initializing World!"
    #@position = Hash.new 
    @players = Hash.new
  end 

  def get_player(name)
    @players[name] ||= add_player(name) 
  end 

  def add_player(name) 
    if @players.key?(name)
      false
    else
      @players[name] = Player.new(name)
    end
  end

  def get_players
    @players.keys
  end

  def get_players_pos(name)
    player = get_player(name)
    return player.get_position
  end

  def get_players_within position, dist
    @selected_players = []
    reflat = position[0]
    reflng = position[1]

    @players.keys.each do |pl|

      puts "Testing user " + pl

      p = @players[pl]
      latlng = p.get_position
      lat = latlng[0]
      lng = latlng[1]

      if ((lat-reflat).abs <= dist)
        puts "Selecting user " + pl
        @selected_players << pl
      else 
        if ((lng-reflng).abs <= dist)
        puts "Selecting user " + pl
          @selected_players << pl
        else
          puts "Skipping user " + pl

        end
      end

    end
    @selected_players
  end

end

