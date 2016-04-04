# -------------------------------------------------------
#
#
#
# -------------------------------------------------------

class Player
	attr_reader :name, :lat, :lng

	def initialize(name)
		@name = name
		@lat = 0.0
		@lng = 0.0
	end

	def moveTo(lat,lng)
		@lat = lat
		@lng = lng 
	end

	def get_position
		return [@lat, @lng]
	end

	def set_position(lat, lng)
		@lat = lat
		@lng = lng
	end

end
