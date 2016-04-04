class World

  #attr_reader :position

  def initialize 
    @position = Hash.new 
  end 

  def update_position player_name, lat, lng 
    position = { 
        lat: lat,
        lng: lng
    } 
    @position[player_name] = position
  end 

  def get_position player_name
    @position[player_name] ||= {:lat => 0, :lng => 0}
  end 

  def test 
    pos = get_position "Peter"
    puts pos.inspect
    update_position "Peter", 0.01010101010, 2.020202020202
    pos = get_position "Peter"
    puts pos.inspect
  end 

  def init
    update_position "peter", 1.0101010101, 2.0202020202
    update_position "fshsweden", 3.03030303, 4.04040404
    update_position "kalle", 5.050505050, 6.06060606
  end

end

